import {
  createContext, useCallback, useEffect, useMemo, useState,
  type ReactNode,
} from 'react'
import * as authService from './authService'
import type { LoginRequest, RegisterRequest, User } from '../models/models'

export interface AuthContextValue {
  token: string | null
  user: User | null
  isAuthenticated: boolean
  isLoading: boolean
  isSubmitting: boolean
  error: string | null
  login: (request: LoginRequest) => Promise<boolean>
  register: (request: RegisterRequest) => Promise<boolean>
  logout: () => Promise<void>
  updateUser: (user: User) => void
}

export const AuthContext = createContext<AuthContextValue | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(null)
  const [user, setUser] = useState<User | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    let active = true
    async function restoreSession() {
      const savedToken = authService.getToken()
      if (!savedToken) {
        if (active) setIsLoading(false)
        return
      }
      try {
        const profile = await authService.getCurrentUser(savedToken)
        if (!active) return
        setToken(savedToken)
        setUser(profile)
      } catch {
        if (!active) return
        authService.clearToken()
        setError('Please log in again.')
      } finally {
        if (active) setIsLoading(false)
      }
    }
    void restoreSession()
    return () => { active = false }
  }, [])

  const login = useCallback(async (request: LoginRequest) => {
    setIsSubmitting(true)
    setError(null)
    setToken(null)
    setUser(null)
    authService.clearToken()
    try {
      const nextToken = await authService.login(request)
      const profile = await authService.getCurrentUser(nextToken)
      setToken(nextToken)
      setUser(profile)
      return true
    } catch (reason: unknown) {
      authService.clearToken()
      setError(reason instanceof Error ? reason.message : 'Login failed.')
      return false
    } finally {
      setIsSubmitting(false)
    }
  }, [])

  const register = useCallback(async (request: RegisterRequest) => {
    setIsSubmitting(true)
    setError(null)
    try {
      await authService.register(request)
      // Registration does not return a JWT. Login is a separate step.
      return true
    } catch (reason: unknown) {
      setError(reason instanceof Error ? reason.message : 'Registration failed.')
      return false
    } finally {
      setIsSubmitting(false)
    }
  }, [])

  const logout = useCallback(async () => {
    setToken(null)
    setUser(null)
    setError(null)
    await authService.logout()
  }, [])

  const updateUser = useCallback((next: User) => setUser(next), [])

  const value = useMemo<AuthContextValue>(() => ({
    token, user, isAuthenticated: token !== null && user !== null,
    isLoading, isSubmitting, error, login, register, logout, updateUser,
  }), [token, user, isLoading, isSubmitting, error, login, register, logout, updateUser])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
