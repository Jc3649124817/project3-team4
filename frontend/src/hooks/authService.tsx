import type {
    APIResponse, ChangePasswordRequest, LoginRequest,
    RegisterRequest, UpdateProfileRequest, User,
} from '../models/models'

const BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080').replace(/\/$/, '')
const KEY = 'Token'

export const setToken = (token: string) => localStorage.setItem(KEY, token)
export const getToken = () => localStorage.getItem(KEY)
export const clearToken = () => localStorage.removeItem(KEY)

export async function login(request: LoginRequest): Promise<string> {
    const response = await fetch(`${BASE_URL}/auth`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(request),
    })
    if (!response.ok) throw new Error('Login failed. Check your credentials.')
    const token = (await response.text()).trim()
    if (!token) throw new Error('The server did not return a login token.')
    setToken(token)
    return token
}

export async function register(request: RegisterRequest): Promise<void> {
    if (request.Password !== request.Password2) {
        throw new Error('Passwords do not match.')
    }
    const response = await fetch(`${BASE_URL}/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(request),
    })
    if (!response.ok) throw new Error('Registration failed. Check your information.')
}

export async function getCurrentUser(token: string): Promise<User> {
    const response = await fetch(`${BASE_URL}/auth/me`, {
        headers: { Authorization: `Bearer ${token}` },
    })
    if (!response.ok) throw new Error('Unable to restore your login. Please log in again.')
    return response.json()
}

export async function logout(): Promise<void> {
    clearToken()
}

export async function changePassword(
    request: ChangePasswordRequest,
): Promise<APIResponse<null>> {
    try {
        const res = await fetch(`${BASE_URL}/me/password`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${getToken()}`,
            },
            body: JSON.stringify(request),
        });

        if (!res.ok) {
            return { data: null, error: (await res.text()) || "Unable to change password.", status: res.status };
        }
        return { data: null, error: null, status: res.status };
    } catch {
        return { data: null, error: "Could not reach the server. Try again.", status: 0 };
    }
}

export async function updateProfile(
    changes: UpdateProfileRequest,
): Promise<APIResponse<User>> {
    const { FirstName, LastName, Email, PhoneNumber } = changes;

    const invalid =
        (FirstName !== undefined && FirstName.trim() === "") ||
        (LastName !== undefined && LastName.trim() === "") ||
        (Email !== undefined && !Email.includes("@")) ||
        (PhoneNumber !== undefined && PhoneNumber.trim() === "");

    if (invalid) {
        return { data: null, error: "Failed Profile Update: Invalid changes attempted.", status: 400 };
    }

    try {
        const res = await fetch(`${BASE_URL}/me`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${getToken()}`,
            },
            body: JSON.stringify(changes),
        });

        if (!res.ok) {
            return { data: null, error: (await res.text()) || "Unable to save changes.", status: res.status };
        }
        return { data: await res.json(), error: null, status: res.status };
    } catch {
        return { data: null, error: "Could not reach the server. Try again.", status: 0 };
    }
}
