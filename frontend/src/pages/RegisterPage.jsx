import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext.jsx'

export default function RegisterPage() {
  const { register } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ username: '', email: '', password: '' })
  const [error, setError] = useState(null)
  const [busy, setBusy] = useState(false)

  const change = (field) => (e) => setForm({ ...form, [field]: e.target.value })

  const submit = async (e) => {
    e.preventDefault()
    setError(null)
    setBusy(true)
    try {
      await register(form.username, form.email, form.password)
      navigate('/campaigns')
    } catch (err) {
      const data = err.response?.data
      setError(data?.message || 'Registracija nije uspela.')
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="auth-card">
      <h1>📜 Registracija</h1>
      <form onSubmit={submit}>
        <label>Korisničko ime
          <input value={form.username} onChange={change('username')} autoFocus />
        </label>
        <label>Email
          <input type="email" value={form.email} onChange={change('email')} />
        </label>
        <label>Lozinka (min. 6 karaktera)
          <input type="password" value={form.password} onChange={change('password')} />
        </label>
        {error && <div className="error">{error}</div>}
        <button type="submit" disabled={busy}>{busy ? 'Kreiranje…' : 'Kreiraj nalog'}</button>
      </form>
      <p className="muted" style={{ textAlign: 'center', marginTop: 18 }}>
        Već imaš nalog? <Link to="/login">Prijavi se</Link>
      </p>
      <p className="hint" style={{ textAlign: 'center' }}>Novi nalozi dobijaju ulogu Player.</p>
    </div>
  )
}
