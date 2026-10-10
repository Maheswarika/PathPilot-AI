import { Link, Navigate } from 'react-router-dom'
import useAuth from '../hooks/useAuth.js'
import { LoadingState } from './UI.jsx'

export default function ProtectedRoute({ children, adminOnly = false }) {
  const { user, restoring, restoreError } = useAuth()
  if (restoring) return <main className="auth-page"><div className="auth-panel"><LoadingState label="Verifying your session..." /></div></main>
  if (restoreError) return <main className="auth-page"><div className="auth-panel"><p className="eyebrow">SESSION EXPIRED</p><h1 className="auth-intro">We couldn't restore your session.</h1><p className="form-error" role="alert">{restoreError}</p><Link className="button button-primary" to="/login">Sign in again <span>↗</span></Link></div></main>
  if (!user) return <Navigate to="/login" replace />
  if (adminOnly && !user.roles?.includes('ROLE_ADMIN')) return <Navigate to="/dashboard" replace />
  return children
}
