import {
    createContext,
    useCallback,
    useEffect,
    useMemo,
    useState,
    type ReactNode,
} from "react";
import * as authService from "./authService.tsx";
import type {
    APIResponse,
    LoginRequest,
    RegisterRequest,
    User,
} from "../models/models.ts";

export interface AuthContextValue {
    user: User | null;
    isAuthenticated: boolean;
    isLoading: boolean;    // checking for an existing user on first load
    isSubmitting: boolean; // a login or register request is in flight
    error: string | null;
    login: (request: LoginRequest) => Promise<boolean>;
    register: (request: RegisterRequest) => Promise<boolean>;
    logout: () => Promise<void>;
    updateUser: (user: User) => void;
}

// Exported so useAuth can read it. Components should use useAuth, not this.
export const AuthContext = createContext<AuthContextValue | undefined>(undefined);

// Only the username is persisted. Never store the password.
const SESSION_KEY = "sessionUsername";

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [user, setUser] = useState<User | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);

    // On first load, check whether the previously signed-in user still exists.
    useEffect(() => {
        let cancelled = false;

        async function restoreSession() {
            const storedUsername = localStorage.getItem(SESSION_KEY);
            if (!storedUsername) {
                setIsLoading(false);
                return;
            }

            try {
                const res = await authService.getCurrentUser(storedUsername);
                if (cancelled) return;

                if (res.data) {
                    setUser(res.data);
                } else if (res.status === 404) {
                    // The user is really gone. Other failures leave the key so a refresh can retry.
                    localStorage.removeItem(SESSION_KEY);
                }
            } catch {
                // Network failure: stay signed out for now, keep the key.
            } finally {
                if (!cancelled) setIsLoading(false);
            }
        }

        restoreSession();
        return () => {
            cancelled = true;
        };
    }, []);

    // Login and register share the same flow, so they share one helper.
    const authenticate = useCallback(
        async (action: () => Promise<APIResponse<User>>, fallbackError: string) => {
            setError(null);
            setIsSubmitting(true);
            try {
                const res = await action();
                if (!res.data) {
                    setError(res.error ?? fallbackError);
                    return false;
                }
                setUser(res.data);
                localStorage.setItem(SESSION_KEY, res.data.Username);
                return true;
            } catch {
                setError("Could not reach the server. Try again.");
                return false;
            } finally {
                setIsSubmitting(false);
            }
        },
        [],
    );

    const login = useCallback(
        (request: LoginRequest) =>
            authenticate(() => authService.login(request), "Login failed"),
        [authenticate],
    );

    const register = useCallback(
        (request: RegisterRequest) =>
            authenticate(() => authService.register(request), "Registration failed"),
        [authenticate],
    );

    const logout = useCallback(async () => {
        // Clear local state first so the user is signed out immediately.
        setUser(null);
        setError(null);
        localStorage.removeItem(SESSION_KEY);
        try {
            await authService.logout();
        } catch {
            // Already signed out locally; nothing else to do.
        }
    }, []);

    const updateUser = useCallback((next: User) => setUser(next), []);

    const value = useMemo<AuthContextValue>(
        () => ({
            user,
            isAuthenticated: user !== null,
            isLoading,
            isSubmitting,
            error,
            login,
            register,
            logout,
            updateUser,
        }),
        [user, isLoading, isSubmitting, error, login, register, logout, updateUser],
    );

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};