import { useEffect, useMemo, useState } from 'react'
import { AuthContext } from './authState.js'
import { authApi } from '../services/api.js'
import { TOKEN_KEY, USER_KEY } from '../utils/authStorage.js'
import { getErrorMessage } from '../utils/format.js'

function readUser() {
  try {
    const saved = localStorage.getItem(USER_KEY)
    return saved ? JSON.parse(saved) : null
  } catch {
    localStorage.removeItem(USER_KEY)
    return null
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(readUser)
  const [restoring, setRestoring] = useState(() => Boolean(localStorage.getItem(TOKEN_KEY)))
  const [restoreError, setRestoreError] = useState('')

  useEffect(() => {
    if (!localStorage.getItem(TOKEN_KEY)) return undefined
    let active = true
    authApi.me()
      .then((currentUser) => {
        if (!active) return
        localStorage.setItem(USER_KEY, JSON.stringify(currentUser))
        setUser(currentUser)
      })
      .catch((error) => {
        if (!active) return
        localStorage.removeItem(TOKEN_KEY)
        localStorage.removeItem(USER_KEY)
        setUser(null)
        setRestoreError(getErrorMessage(error))
      })
      .finally(() => {
        if (active) setRestoring(false)
      })
    return () => { active = false }
  }, [])

  function saveAuth(response) {
    if (!response?.token || !response?.user) {
      throw new Error('The backend did not return a valid authentication response. Please try again.')
    }
    localStorage.setItem(TOKEN_KEY, response.token)
    localStorage.setItem(USER_KEY, JSON.stringify(response.user))
    setUser(response.user)
    setRestoreError('')
    setRestoring(false)
  }

  async function login(credentials) {
    saveAuth(await authApi.login(credentials))
  }

  async function register(details) {
    saveAuth(await authApi.register(details))
  }

  function logout() {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
    setUser(null)
  }

  const value = useMemo(() => ({ user, login, register, logout, restoring, restoreError }), [user, restoring, restoreError])
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
