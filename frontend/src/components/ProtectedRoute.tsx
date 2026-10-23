import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'
import { LoadingSpinner } from './LoadingSpinner'

export function ProtectedRoute() {
  const { isAuthenticated, isLoading } = useAuth()
  if (isLoading) return <LoadingSpinner label="Checking your login..." />
  return isAuthenticated ? <Outlet /> : <Navigate to="/login" replace />
}
