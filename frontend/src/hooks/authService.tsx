import { UserService } from '../service/UserService'
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

// Profile/password updates remain the team's existing mock implementation.
// These must be connected to real endpoints separately.
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


