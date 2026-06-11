import { useEffect, useState } from 'react'
import client from '../api/client.js'
import { useAuth } from '../auth/AuthContext.jsx'

const TYPES = ['WEAPON', 'ARMOR', 'POTION', 'SCROLL', 'TREASURE', 'MISC']
const RARITIES = ['COMMON', 'UNCOMMON', 'RARE', 'EPIC', 'LEGENDARY']
const EMPTY = { name: '', itemType: 'WEAPON', rarity: 'COMMON', goldValue: 0, description: '' }

export default function ItemsPage() {
  const { isGameMaster } = useAuth()
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [showForm, setShowForm] = useState(false)
  const [editing, setEditing] = useState(null)
  const [form, setForm] = useState(EMPTY)
  const [error, setError] = useState(null)

  const load = async () => {
    setLoading(true)
    try {
      const { data } = await client.get('/items')
      setItems(data)
    } finally {
      setLoading(false)
    }
  }
  useEffect(() => { load() }, [])

  const openCreate = () => { setEditing(null); setForm(EMPTY); setShowForm(true); setError(null) }
  const openEdit = (i) => {
    setEditing(i)
    setForm({ name: i.name, itemType: i.itemType, rarity: i.rarity, goldValue: i.goldValue, description: i.description || '' })
    setShowForm(true)
    setError(null)
  }
  const change = (f) => (e) => setForm({ ...form, [f]: e.target.value })

  const submit = async (e) => {
    e.preventDefault()
    setError(null)
    const payload = { ...form, goldValue: Number(form.goldValue) }
    try {
      if (editing) await client.put(`/items/${editing.id}`, payload)
      else await client.post('/items', payload)
      setShowForm(false)
      await load()
    } catch (err) {
      setError(err.response?.data?.message || 'Greška pri čuvanju.')
    }
  }

  const remove = async (i) => {
    if (!confirm(`Obrisati predmet "${i.name}"?`)) return
    await client.delete(`/items/${i.id}`)
    await load()
  }

  if (loading) return <div className="loading">Učitavanje…</div>

  return (
    <>
      <div className="page-head">
        <h1>Katalog predmeta</h1>
        {isGameMaster && <button onClick={openCreate}>+ Novi predmet</button>}
      </div>

      {showForm && (
        <div className="inline-form">
          <h3>{editing ? 'Izmena predmeta' : 'Novi predmet'}</h3>
          <form onSubmit={submit}>
            <div className="form-row">
              <label>Naziv<input value={form.name} onChange={change('name')} required /></label>
              <label>Vrednost (zlato)<input type="number" min="0" value={form.goldValue} onChange={change('goldValue')} /></label>
            </div>
            <div className="form-row">
              <label>Tip
                <select value={form.itemType} onChange={change('itemType')}>
                  {TYPES.map((t) => <option key={t} value={t}>{t}</option>)}
                </select>
              </label>
              <label>Retkost
                <select value={form.rarity} onChange={change('rarity')}>
                  {RARITIES.map((r) => <option key={r} value={r}>{r}</option>)}
                </select>
              </label>
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

      {items.length === 0 ? (
        <p className="empty">Katalog je prazan.</p>
      ) : (
        <div className="panel">
          <table>
            <thead>
              <tr>
                <th>Naziv</th><th>Tip</th><th>Retkost</th><th>Zlato</th><th>Opis</th>
                {isGameMaster && <th></th>}
              </tr>
            </thead>
            <tbody>
              {items.map((i) => (
                <tr key={i.id}>
                  <td><b>{i.name}</b></td>
                  <td><span className="tag">{i.itemType}</span></td>
                  <td className={`rarity-${i.rarity}`}>{i.rarity}</td>
                  <td>{i.goldValue} 🪙</td>
                  <td className="muted">{i.description}</td>
                  {isGameMaster && (
                    <td style={{ whiteSpace: 'nowrap' }}>
                      <button className="btn-sm" onClick={() => openEdit(i)}>Izmeni</button>{' '}
                      <button className="btn-sm btn-danger" onClick={() => remove(i)}>Obriši</button>
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </>
  )
}
