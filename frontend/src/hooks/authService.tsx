import type { RegisterRequest, LoginRequest } from "../models/models";


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

export async function logout(): Promise<void> {
  clearToken()
    // No UserService.logout yet. A real backend would invalidate the session here.
}