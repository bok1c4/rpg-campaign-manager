import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import client from '../api/client.js'
import { useAuth } from '../auth/AuthContext.jsx'

const EMPTY = { title: '', description: '', setting: '' }

export default function CampaignsPage() {
  const { isGameMaster } = useAuth()
  const [campaigns, setCampaigns] = useState([])
  const [loading, setLoading] = useState(true)
  const [showForm, setShowForm] = useState(false)
  const [editing, setEditing] = useState(null)
  const [form, setForm] = useState(EMPTY)
  const [error, setError] = useState(null)

  const load = async () => {
    setLoading(true)
    try {
      const { data } = await client.get('/campaigns')
      setCampaigns(data)
    } finally {
      setLoading(false)
    }
  }
  useEffect(() => { load() }, [])

  const openCreate = () => { setEditing(null); setForm(EMPTY); setShowForm(true); setError(null) }
  const openEdit = (c) => {
    setEditing(c)
    setForm({ title: c.title, description: c.description || '', setting: c.setting || '' })
    setShowForm(true)
    setError(null)
  }
  const change = (f) => (e) => setForm({ ...form, [f]: e.target.value })

  const submit = async (e) => {
    e.preventDefault()
    setError(null)
    try {
      if (editing) await client.put(`/campaigns/${editing.id}`, form)
      else await client.post('/campaigns', form)
      setShowForm(false)
      await load()
    } catch (err) {
      setError(err.response?.data?.message || 'Greška pri čuvanju.')
    }
  }

  const remove = async (c) => {
    if (!confirm(`Obrisati kampanju "${c.title}"? Brišu se i njeni likovi i sesije.`)) return
    await client.delete(`/campaigns/${c.id}`)
    await load()
  }

  if (loading) return <div className="loading">Učitavanje…</div>

  return (
    <>
      <div className="page-head">
        <h1>Kampanje</h1>
        {isGameMaster && <button onClick={openCreate}>+ Nova kampanja</button>}
      </div>

      {showForm && (
        <div className="inline-form">
          <h3>{editing ? 'Izmena kampanje' : 'Nova kampanja'}</h3>
          <form onSubmit={submit}>
            <div className="form-row">
              <label>Naziv<input value={form.title} onChange={change('title')} required /></label>
              <label>Setting<input value={form.setting} onChange={change('setting')} placeholder="npr. Forgotten Realms" /></label>
            </div>
            <label>Opis<textarea value={form.description} onChange={change('description')} /></label>
            {error && <div className="error">{error}</div>}
            <div className="form-row">
              <button type="submit">Sačuvaj</button>
              <button type="button" className="btn-ghost" onClick={() => setShowForm(false)}>Otkaži</button>
            </div>
          </form>
        </div>
      )}

      {campaigns.length === 0 ? (
        <p className="empty">Još nema kampanja.</p>
      ) : (
        <div className="grid">
          {campaigns.map((c) => (
            <div className="card" key={c.id}>
              <h3><Link to={`/campaigns/${c.id}`}>{c.title}</Link></h3>
              <p className="meta">🗺️ {c.setting || '—'}</p>
              <p className="meta">GM: {c.gameMaster?.username}</p>
              <p className="meta">{c.characterCount} likova · {c.sessionCount} sesija · {c.players.length} igrača</p>
              {c.description && <p>{c.description}</p>}
              <div className="actions">
                <Link to={`/campaigns/${c.id}`}><button className="btn-sm btn-ghost">Detalji</button></Link>
                {isGameMaster && <button className="btn-sm" onClick={() => openEdit(c)}>Izmeni</button>}
                {isGameMaster && <button className="btn-sm btn-danger" onClick={() => remove(c)}>Obriši</button>}
              </div>
            </div>
          ))}
        </div>
      )}
    </>
  )
}
