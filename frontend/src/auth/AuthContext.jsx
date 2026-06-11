import { createContext, useContext, useState } from 'react'
import client, { tokenStore } from '../api/client.js'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const raw = localStorage.getItem('user')
    return raw ? JSON.parse(raw) : null
  })

  const persist = (data) => {
    tokenStore.set(data.accessToken, data.refreshToken)
    const u = { username: data.username, role: data.role }
    localStorage.setItem('user', JSON.stringify(u))
    setUser(u)
  }

  const login = async (username, password) => {
    const { data } = await client.post('/auth/login', { username, password })
    persist(data)
  }

  const register = async (username, email, password) => {
    const { data } = await client.post('/auth/register', { username, email, password })
    persist(data)
  }

  const logout = () => {
    tokenStore.clear()
    localStorage.removeItem('user')
    setUser(null)
  }

  const isGameMaster = user?.role === 'GAME_MASTER'

  return (
    <AuthContext.Provider value={{ user, isGameMaster, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
