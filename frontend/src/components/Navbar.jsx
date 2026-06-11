import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext.jsx'

export default function Navbar() {
  const { user, isGameMaster, logout } = useAuth()
  const navigate = useNavigate()

  if (!user) return null

  const doLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <nav className="navbar">
      <Link to="/campaigns" className="brand">⚔️ RPG Campaign Manager</Link>
      <div className="links">
        <Link to="/campaigns">Kampanje</Link>
        <Link to="/characters">Moji likovi</Link>
        <Link to="/items">Predmeti</Link>
      </div>
      <div className="user">
        <span className={`badge ${isGameMaster ? 'badge-gm' : 'badge-player'}`}>
          {user.username} · {isGameMaster ? 'Game Master' : 'Player'}
        </span>
        <button className="btn-ghost" onClick={doLogout}>Odjava</button>
      </div>
    </nav>
  )
}
