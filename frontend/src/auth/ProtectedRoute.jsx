import { Navigate } from 'react-router-dom'
import { useAuth } from './AuthContext.jsx'

export default function ProtectedRoute({ children, requireGameMaster = false }) {
  const { user, isGameMaster } = useAuth()
  if (!user) {
    return <Navigate to="/login" replace />
  }
  if (requireGameMaster && !isGameMaster) {
    return <Navigate to="/campaigns" replace />
  }
  return children
}
