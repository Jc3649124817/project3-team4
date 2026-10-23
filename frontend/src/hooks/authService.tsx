import { UserService } from "../service/UserService.ts";
import type {
    APIResponse, ChangePasswordRequest,
    LoginRequest,
    RegisterRequest, UpdateProfileRequest,
    User,
} from "../models/models.ts";

let token: string = '';
let role: string = '';
const BASE_URL = "http://localhost:8080/auth";
const API_URL = "http://localhost:8080";
const KEY = "Token";

export const setToken = (t: string) => localStorage.setItem(KEY, t);
export const getToken = () => localStorage.getItem(KEY);
export const clearToken = () => localStorage.removeItem(KEY);
// export const getToken = () => token;
export const getRole = () => role;

export const login = async (request : LoginRequest): Promise<string> => {
  const res = await fetch(`${BASE_URL}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request)
  });
  if (!res.ok) throw new Error('Invalid credentials');
  let tokenText = await res.text()
  console.log(tokenText)
  setToken(tokenText)
  return tokenText;
};

export const register = async (request : RegisterRequest): Promise<any> => {
  const res = await fetch(`${BASE_URL}/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request)
  });
  if (!res.ok) throw new Error('Registration failed');
  // return res.json();
};

export async function changePassword(
    request: ChangePasswordRequest,
): Promise<APIResponse<null>> {
    try {
        const res = await fetch(`${API_URL}/me/password`, {
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
        const res = await fetch(`${API_URL}/me`, {
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


/** Checks whether the previously signed-in user still exists. */
export function getCurrentUser(storedUsername: string): Promise<APIResponse<User>> {
    return UserService.getUser(storedUsername ? 1 : 0);
}

export async function logout(): Promise<void> {
  clearToken()
    // No UserService.logout yet. A real backend would invalidate the session here.
}