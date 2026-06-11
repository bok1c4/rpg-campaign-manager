import axios from 'axios'

// Backend i frontend se serviraju sa istog origin-a -> baseURL je /api.
const client = axios.create({ baseURL: '/api' })

export const tokenStore = {
  get access() {
    return localStorage.getItem('accessToken')
  },
  get refresh() {
    return localStorage.getItem('refreshToken')
  },
  set(access, refresh) {
    if (access) localStorage.setItem('accessToken', access)
    if (refresh) localStorage.setItem('refreshToken', refresh)
  },
  clear() {
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
  }
}

// Svaki zahtev nosi access token.
client.interceptors.request.use((config) => {
  const token = tokenStore.access
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Automatsko osvežavanje tokena: na 401 pozovi /auth/refresh i ponovi zahtev.
let refreshPromise = null
client.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original = error.config
    const status = error.response?.status
    const isAuthCall = original?.url?.includes('/auth/')

    if (status === 401 && !original._retry && !isAuthCall && tokenStore.refresh) {
      original._retry = true
      try {
        // single-flight: paralelni 401-evi dele isti refresh poziv
        refreshPromise = refreshPromise ||
          axios.post('/api/auth/refresh', { refreshToken: tokenStore.refresh })
        const { data } = await refreshPromise
        refreshPromise = null
        tokenStore.set(data.accessToken, data.refreshToken)
        original.headers.Authorization = `Bearer ${data.accessToken}`
        return client(original)
      } catch (refreshError) {
        refreshPromise = null
        tokenStore.clear()
        localStorage.removeItem('user')
        window.location.href = '/login'
        return Promise.reject(refreshError)
      }
    }
    return Promise.reject(error)
  }
)

export default client
