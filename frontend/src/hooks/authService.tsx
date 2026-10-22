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
export function changePassword(request: ChangePasswordRequest): Promise<APIResponse<null>> {
    if (request.CurrentPassword !== "apple123") {
        return UserService.changePassword(0);
    }
    return UserService.changePassword(1);
}

export function updateProfile (
    currentUser: User,
    changes: UpdateProfileRequest,
    ): Promise<APIResponse<User>> {
        const { FirstName, LastName, Email, PhoneNumber } = changes;

        const badFirstName = FirstName !== undefined && FirstName.trim() === "";
        const badLastName = LastName !== undefined && LastName.trim() === "";
        const badEmail = Email !== undefined && !Email.includes("@");
        const badPhoneNumber = PhoneNumber !== undefined && PhoneNumber.trim() === "";

        const invalid = badFirstName || badLastName || badEmail || badPhoneNumber;

        return UserService.updateUser(invalid ? 0 : 1, currentUser, changes);
}

/** Checks whether the previously signed-in user still exists. */
export function getCurrentUser(storedUsername: string): Promise<APIResponse<User>> {
    return UserService.getUser(storedUsername ? 1 : 0);
}

export async function logout(): Promise<void> {
  clearToken()
    // No UserService.logout yet. A real backend would invalidate the session here.
}