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
    token: string | null;
    isAuthenticated: boolean;
    isLoading: boolean;    // checking for an existing user on first load
    isSubmitting: boolean; // a login or register request is in flight
    error: string | null;
    login: (request: LoginRequest) => Promise<boolean>;
    register: (request: RegisterRequest) => Promise<boolean>;
    logout: () => Promise<void>;
}

// Exported so useAuth can read it. Components should use useAuth, not this.
export const AuthContext = createContext<AuthContextValue | undefined>(undefined);

// Only the username is persisted. Never store the password.
const SESSION_KEY = "Token";

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    // const [user, setUser] = useState<User | null>(null);
    const [token, setToken] = useState<string|null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);

    // // On first load, check whether the previously signed-in user still exists.
    // useEffect(() => {
    //     let cancelled = false;

    //     async function restoreSession() {
    //         if (!storedToken) {
    //             setIsLoading(false);
    //             return;
    //         }
    //
    //         try {
    //             const res = await authService.login(storedToken);
    //             if (cancelled) return;
    //
    //             if (res.data) {
    //                 setToken(res.data);
    //             } else if (res.status === 404) {
    //                 // The user is really gone. Other failures leave the key so a refresh can retry.
    //             }
    //         } catch {
    //             // Network failure: stay signed out for now, keep the key.
    //         } finally {
    //             if (!cancelled) setIsLoading(false);
    //         }
    //     }
    //
    //     restoreSession();
    //     return () => {
    //         cancelled = true;
    //     };
    // }, []);

    // restoreSession: there's no endpoint to validate the token yet, so trust it.
// If you add GET /auth/me later, call it here and clear the token on 401.
    useEffect(() => {
        setToken(authService.getToken());
        setIsLoading(false);
    }, []);

    // Login and register share the same flow, so they share one helper.
    const authenticate = useCallback(
        async (action: () => Promise<string>, fallbackError: string) => {
            setError(null);
            setIsSubmitting(true);
            try {
                const res = await action();
                // if (!res.data) {
                //     setError(res.error ?? fallbackError);
                //     return false;
                // }
                setToken(res);
                console.log(res);
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
        setToken(null);
        setError(null);
        try {
            await authService.logout();
        } catch {
            // Already signed out locally; nothing else to do.
        }
    }, []);

    // const updateUser = useCallback((next: User) => setUser(next), []);

    const value = useMemo<AuthContextValue>(
        () => ({
            token,
            isAuthenticated: token !== null,
            isLoading,
            isSubmitting,
            error,
            login,
            register,
            logout,
        }),
        [token, isLoading, isSubmitting, error, login, register, logout],
    );

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};