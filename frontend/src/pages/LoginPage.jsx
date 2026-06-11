import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext.jsx'

export default function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [username, setUsername] = useState('gamemaster')
  const [password, setPassword] = useState('password123')
  const [error, setError] = useState(null)
  const [busy, setBusy] = useState(false)

  const submit = async (e) => {
    e.preventDefault()
    setError(null)
    setBusy(true)
    try {
      await login(username, password)
      navigate('/campaigns')
    } catch (err) {
      setError(err.response?.data?.message || 'Prijava nije uspela.')
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="auth-card">
      <h1>⚔️ Prijava</h1>
      <form onSubmit={submit}>
        <label>Korisničko ime
          <input value={username} onChange={(e) => setUsername(e.target.value)} autoFocus />
        </label>
        <label>Lozinka
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
        </label>
        {error && <div className="error">{error}</div>}
        <button type="submit" disabled={busy}>{busy ? 'Prijavljivanje…' : 'Prijavi se'}</button>
      </form>
      <p className="muted" style={{ textAlign: 'center', marginTop: 18 }}>
        Nemaš nalog? <Link to="/register">Registruj se</Link>
      </p>
      <p className="hint" style={{ textAlign: 'center' }}>
        Demo: <b>gamemaster</b> / password123 (GM)<br />
        <b>aragorn</b> / password123 (Player)
      </p>
    </div>
  )
}
