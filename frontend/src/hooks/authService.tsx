import type { RegisterRequest, LoginRequest } from "../models/models";

const BASE_URL = 'http://localhost:8080/auth';

let token: string = '';
let role: string = '';

export const setToken = (t: string) => {
  token = t;
};

export const getToken = () => token;
export const getRole = () => role;

export const login = async (request : LoginRequest): Promise<string> => {
  const res = await fetch(`${BASE_URL}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request)
  });
  if (!res.ok) throw new Error('Invalid credentials');
  return res.text();
};

export const register = async (request : RegisterRequest): Promise<any> => {
  const res = await fetch(`${BASE_URL}/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request)
  });
  if (!res.ok) throw new Error('Registration failed');
  return res.json();
};

export async function logout(): Promise<void> {
    // No UserService.logout yet. A real backend would invalidate the session here.
}