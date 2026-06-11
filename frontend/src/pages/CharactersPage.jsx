import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import client from '../api/client.js'

const EMPTY = { name: '', race: '', characterClass: '', level: 1, hitPoints: 10, backstory: '', campaignId: '' }

export default function CharactersPage() {
  const [characters, setCharacters] = useState([])
  const [campaigns, setCampaigns] = useState([])
  const [loading, setLoading] = useState(true)
  const [showForm, setShowForm] = useState(false)
  const [editing, setEditing] = useState(null)
  const [form, setForm] = useState(EMPTY)
  const [error, setError] = useState(null)

  const load = async () => {
    setLoading(true)
    try {
      const [chs, cmps] = await Promise.all([client.get('/characters/mine'), client.get('/campaigns')])
      setCharacters(chs.data)
      setCampaigns(cmps.data)
    } finally {
      setLoading(false)
    }
  }
  useEffect(() => { load() }, [])

  const openCreate = () => {
    setEditing(null)
    setForm({ ...EMPTY, campaignId: campaigns[0]?.id || '' })
    setShowForm(true)
    setError(null)
  }
  const openEdit = (c) => {
    setEditing(c)
    setForm({
      name: c.name, race: c.race || '', characterClass: c.characterClass || '',
      level: c.level, hitPoints: c.hitPoints, backstory: c.backstory || '', campaignId: c.campaignId
    })
    setShowForm(true)
    setError(null)
  }
  const change = (f) => (e) => setForm({ ...form, [f]: e.target.value })

  const submit = async (e) => {
    e.preventDefault()
    setError(null)
    const base = { ...form, level: Number(form.level), hitPoints: Number(form.hitPoints) }
    try {
      if (editing) {
        const { campaignId, ...upd } = base
        await client.put(`/characters/${editing.id}`, upd)
      } else {
        await client.post('/characters', { ...base, campaignId: Number(form.campaignId) })
      }
      setShowForm(false)
      await load()
    } catch (err) {
      setError(err.response?.data?.message || 'Greška pri čuvanju.')
    }
  }

  const remove = async (c) => {
    if (!confirm(`Obrisati lika "${c.name}"?`)) return
    await client.delete(`/characters/${c.id}`)
    await load()
  }

  if (loading) return <div className="loading">Učitavanje…</div>

  return (
    <>
      <div className="page-head">
        <h1>Moji likovi</h1>
        <button onClick={openCreate} disabled={campaigns.length === 0}>+ Novi lik</button>
      </div>
      {campaigns.length === 0 && <p className="hint">Prvo mora postojati bar jedna kampanja da bi kreirao lika.</p>}

      {showForm && (
        <div className="inline-form">
          <h3>{editing ? 'Izmena lika' : 'Novi lik'}</h3>
          <form onSubmit={submit}>
            <div className="form-row">
              <label>Ime<input value={form.name} onChange={change('name')} required /></label>
              <label>Kampanja
                <select value={form.campaignId} onChange={change('campaignId')} disabled={!!editing} required>
                  {campaigns.map((c) => <option key={c.id} value={c.id}>{c.title}</option>)}
                </select>
              </label>
            </div>
            <div className="form-row">
              <label>Rasa<input value={form.race} onChange={change('race')} placeholder="Human, Elf…" /></label>
              <label>Klasa<input value={form.characterClass} onChange={change('characterClass')} placeholder="Ranger, Wizard…" /></label>
              <label>Level<input type="number" min="1" value={form.level} onChange={change('level')} /></label>
              <label>HP<input type="number" min="0" value={form.hitPoints} onChange={change('hitPoints')} /></label>
            </div>
            <label>Priča lika<textarea value={form.backstory} onChange={change('backstory')} /></label>
            {error && <div className="error">{error}</div>}
            <div className="form-row">
              <button type="submit">Sačuvaj</button>
              <button type="button" className="btn-ghost" onClick={() => setShowForm(false)}>Otkaži</button>
            </div>
          </form>
        </div>
      )}

      {characters.length === 0 ? (
        <p className="empty">Još nemaš likova.</p>
      ) : (
        <div className="grid">
          {characters.map((c) => (
            <div className="card" key={c.id}>
              <h3><Link to={`/characters/${c.id}`}>{c.name}</Link></h3>
              <p className="meta">{c.race} {c.characterClass} · Level {c.level}</p>
              <p className="meta">❤️ {c.hitPoints} HP · 🎒 {c.itemCount} predmeta</p>
              <p className="meta">Kampanja: {c.campaignTitle}</p>
              <div className="actions">
                <Link to={`/characters/${c.id}`}><button className="btn-sm btn-ghost">Inventar</button></Link>
                <button className="btn-sm" onClick={() => openEdit(c)}>Izmeni</button>
                <button className="btn-sm btn-danger" onClick={() => remove(c)}>Obriši</button>
              </div>
            </div>
          ))}
        </div>
      )}
    </>
  )
}
