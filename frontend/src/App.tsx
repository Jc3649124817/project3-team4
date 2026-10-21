import { Navigate, Outlet, Route, Routes } from 'react-router-dom'
import { AppNavBar } from './components/AppNavBar'
import { ProtectedRoute } from './components/ProtectedRoute'
import { LoginPage } from './pages/LoginPage'
import { RegisterPage } from './pages/RegisterPage'
import { DashboardPage } from './pages/DashboardPage'
import { TransactionsPage } from './pages/TransactionsPage'
import { TransactionHistoryPage } from './pages/TransactionHistoryPage'
import { VideoBackground } from './components/VideoBackground'
import { UserProfilePage} from "./pages/UserProfilePage.tsx";

function AppLayout() {
  return (
    <>
      <AppNavBar />
      <Outlet />
    </>
  )
}

function App() {
  return (
    <>
    <VideoBackground src={`${import.meta.env.BASE_URL}ocean-background.mp4`} />
    <div className="app-content">
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />

      <Route element={<ProtectedRoute />}>
        <Route element={<AppLayout />}>
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/transactions" element={<TransactionsPage />} />
          <Route path="/transaction-history" element={<TransactionHistoryPage />} />
          <Route path="/user-profile" element={<UserProfilePage />} />
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
    </div>
    </>
  )
}

export default App
