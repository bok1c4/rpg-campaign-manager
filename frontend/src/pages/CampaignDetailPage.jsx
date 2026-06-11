import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import client from '../api/client.js'
import { useAuth } from '../auth/AuthContext.jsx'

const EMPTY_SESSION = { sessionNumber: 1, title: '', summary: '', playedOn: '' }

export default function CampaignDetailPage() {
  const { id } = useParams()
  const { isGameMaster } = useAuth()
  const [campaign, setCampaign] = useState(null)
  const [characters, setCharacters] = useState([])
  const [sessions, setSessions] = useState([])
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)

  const [showSession, setShowSession] = useState(false)
  const [editingSession, setEditingSession] = useState(null)
  const [sessionForm, setSessionForm] = useState(EMPTY_SESSION)
  const [playerToAdd, setPlayerToAdd] = useState('')
  const [error, setError] = useState(null)

  const load = async () => {
    setLoading(true)
    try {
      const [c, chs, sess] = await Promise.all([
        client.get(`/campaigns/${id}`),
        client.get('/characters', { params: { campaignId: id } }),
        client.get(`/campaigns/${id}/sessions`)
      ])
      setCampaign(c.data)
      setCharacters(chs.data)
      setSessions(sess.data)
      if (isGameMaster) {
        const u = await client.get('/users')
        setUsers(u.data)
      }
    } finally {
      setLoading(false)
    }
  }
  useEffect(() => { load() }, [id])

  // ----- sessions -----
  const openCreateSession = () => {
    setEditingSession(null)
    setSessionForm({ ...EMPTY_SESSION, sessionNumber: sessions.length + 1 })
    setShowSession(true)
    setError(null)
  }
  const openEditSession = (s) => {
    setEditingSession(s)
    setSessionForm({ sessionNumber: s.sessionNumber, title: s.title, summary: s.summary || '', playedOn: s.playedOn || '' })
    setShowSession(true)
    setError(null)
  }
  const changeSession = (f) => (e) => setSessionForm({ ...sessionForm, [f]: e.target.value })
  const submitSession = async (e) => {
    e.preventDefault()
    setError(null)
    const payload = {
      ...sessionForm,
      sessionNumber: Number(sessionForm.sessionNumber),
      playedOn: sessionForm.playedOn || null
    }
    try {
      if (editingSession) await client.put(`/sessions/${editingSession.id}`, payload)
      else await client.post(`/campaigns/${id}/sessions`, payload)
      setShowSession(false)
      await load()
    } catch (err) {
      setError(err.response?.data?.message || 'Greška pri čuvanju.')
    }
  }
  const removeSession = async (s) => {
    if (!confirm(`Obrisati sesiju "${s.title}"?`)) return
    await client.delete(`/sessions/${s.id}`)
    await load()
  }

  // ----- players -----
  const addPlayer = async () => {
    if (!playerToAdd) return
    await client.post(`/campaigns/${id}/players/${playerToAdd}`)
    setPlayerToAdd('')
    await load()
  }
  const removePlayer = async (uid) => {
    await client.delete(`/campaigns/${id}/players/${uid}`)
    await load()
  }

  if (loading) return <div className="loading">Učitavanje…</div>
  if (!campaign) return <p className="empty">Kampanja nije pronađena.</p>

  const memberIds = new Set(campaign.players.map((p) => p.id))
  const candidates = users.filter((u) => !memberIds.has(u.id))

  return (
    <>
      <div className="page-head">
        <h1>{campaign.title}</h1>
        <Link to="/campaigns"><button className="btn-ghost">← Nazad</button></Link>
      </div>

      <div className="panel">
        <p className="meta">🗺️ {campaign.setting || '—'} · GM: {campaign.gameMaster?.username}</p>
        {campaign.description && <p>{campaign.description}</p>}
      </div>

      {/* Party / players */}
      <div className="panel">
        <div className="section-title"><h2>🛡️ Igrači (party)</h2></div>
        {campaign.players.length === 0 ? (
          <p className="empty">Nema igrača u partyju.</p>
        ) : (
          <table>
            <thead><tr><th>Korisnik</th><th>Uloga</th>{isGameMaster && <th></th>}</tr></thead>
            <tbody>
              {campaign.players.map((p) => (
                <tr key={p.id}>
                  <td>{p.username}</td>
                  <td><span className="tag">{p.role}</span></td>
                  {isGameMaster && <td><button className="btn-sm btn-danger" onClick={() => removePlayer(p.id)}>Ukloni</button></td>}
                </tr>
              ))}
            </tbody>
          </table>
        )}
        {isGameMaster && candidates.length > 0 && (
          <div className="form-row" style={{ marginTop: 14 }}>
            <select value={playerToAdd} onChange={(e) => setPlayerToAdd(e.target.value)}>
              <option value="">— izaberi igrača —</option>
              {candidates.map((u) => <option key={u.id} value={u.id}>{u.username} ({u.role})</option>)}
            </select>
            <button onClick={addPlayer} disabled={!playerToAdd}>Dodaj u party</button>
          </div>
        )}
      </div>

      {/* Characters */}
      <div className="panel">
        <div className="section-title"><h2>🧙 Likovi</h2></div>
        {characters.length === 0 ? (
          <p className="empty">Nema likova u ovoj kampanji.</p>
        ) : (
          <table>
            <thead><tr><th>Ime</th><th>Rasa/Klasa</th><th>Lvl</th><th>Igrač</th><th></th></tr></thead>
            <tbody>
              {characters.map((c) => (
                <tr key={c.id}>
                  <td><Link to={`/characters/${c.id}`}><b>{c.name}</b></Link></td>
                  <td>{c.race} {c.characterClass}</td>
                  <td>{c.level}</td>
                  <td>{c.player?.username}</td>
                  <td><Link to={`/characters/${c.id}`}><button className="btn-sm btn-ghost">Inventar</button></Link></td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
        <p className="hint" style={{ marginTop: 10 }}>Lika kreiraš na stranici „Moji likovi“ i biraš ovu kampanju.</p>
      </div>

      {/* Sessions */}
      <div className="panel">
        <div className="section-title">
          <h2>📜 Sesije</h2>
          {isGameMaster && <button className="btn-sm" onClick={openCreateSession}>+ Nova sesija</button>}
        </div>
        {showSession && (
          <div className="inline-form">
            <h3>{editingSession ? 'Izmena sesije' : 'Nova sesija'}</h3>
            <form onSubmit={submitSession}>
              <div className="form-row">
                <label>Broj<input type="number" min="0" value={sessionForm.sessionNumber} onChange={changeSession('sessionNumber')} /></label>
                <label>Datum<input type="date" value={sessionForm.playedOn} onChange={changeSession('playedOn')} /></label>
              </div>
              <label>Naslov<input value={sessionForm.title} onChange={changeSession('title')} required /></label>
              <label>Rezime<textarea value={sessionForm.summary} onChange={changeSession('summary')} /></label>
              {error && <div className="error">{error}</div>}
              <div className="form-row">
                <button type="submit">Sačuvaj</button>
                <button type="button" className="btn-ghost" onClick={() => setShowSession(false)}>Otkaži</button>
              </div>
            </form>
          </div>
        )}
        {sessions.length === 0 ? (
          <p className="empty">Još nema sesija.</p>
        ) : (
          <table>
            <thead><tr><th>#</th><th>Naslov</th><th>Datum</th><th>Rezime</th>{isGameMaster && <th></th>}</tr></thead>
            <tbody>
              {sessions.map((s) => (
                <tr key={s.id}>
                  <td>{s.sessionNumber}</td>
                  <td><b>{s.title}</b></td>
                  <td>{s.playedOn || '—'}</td>
                  <td className="muted">{s.summary}</td>
                  {isGameMaster && (
                    <td style={{ whiteSpace: 'nowrap' }}>
                      <button className="btn-sm" onClick={() => openEditSession(s)}>Izmeni</button>{' '}
                      <button className="btn-sm btn-danger" onClick={() => removeSession(s)}>Obriši</button>
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </>
  )
}
