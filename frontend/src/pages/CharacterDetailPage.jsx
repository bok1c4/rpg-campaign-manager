import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import client from '../api/client.js'

export default function CharacterDetailPage() {
  const { id } = useParams()
  const [character, setCharacter] = useState(null)
  const [inventory, setInventory] = useState([])
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [addForm, setAddForm] = useState({ itemId: '', quantity: 1, equipped: false })
  const [error, setError] = useState(null)

  const load = async () => {
    setLoading(true)
    try {
      const [ch, inv, allItems] = await Promise.all([
        client.get(`/characters/${id}`),
        client.get(`/characters/${id}/items`),
        client.get('/items')
      ])
      setCharacter(ch.data)
      setInventory(inv.data)
      setItems(allItems.data)
      setAddForm((f) => ({ ...f, itemId: allItems.data[0]?.id || '' }))
    } finally {
      setLoading(false)
    }
  }
  useEffect(() => { load() }, [id])

  const addItem = async (e) => {
    e.preventDefault()
    setError(null)
    try {
      await client.post(`/characters/${id}/items`, {
        itemId: Number(addForm.itemId),
        quantity: Number(addForm.quantity),
        equipped: addForm.equipped
      })
      await load()
    } catch (err) {
      setError(err.response?.data?.message || 'Greška pri dodavanju.')
    }
  }

  const toggleEquip = async (entry) => {
    await client.put(`/characters/${id}/items/${entry.id}`, { equipped: !entry.equipped })
    await load()
  }
  const changeQty = async (entry, delta) => {
    const q = entry.quantity + delta
    if (q < 1) return
    await client.put(`/characters/${id}/items/${entry.id}`, { quantity: q })
    await load()
  }
  const removeEntry = async (entry) => {
    if (!confirm(`Izbaciti "${entry.item.name}" iz inventara?`)) return
    await client.delete(`/characters/${id}/items/${entry.id}`)
    await load()
  }

  if (loading) return <div className="loading">Učitavanje…</div>
  if (!character) return <p className="empty">Lik nije pronađen.</p>

  return (
    <>
      <div className="page-head">
        <h1>🧙 {character.name}</h1>
        <Link to="/characters"><button className="btn-ghost">← Nazad</button></Link>
      </div>

      <div className="panel">
        <p className="meta">{character.race} {character.characterClass} · Level {character.level} · ❤️ {character.hitPoints} HP</p>
        <p className="meta">Kampanja: {character.campaignTitle} · Igrač: {character.player?.username}</p>
        {character.backstory && <p>{character.backstory}</p>}
      </div>

      <div className="panel">
        <div className="section-title"><h2>🎒 Inventar</h2></div>

        <div className="inline-form">
          <form onSubmit={addItem}>
            <div className="form-row">
              <label>Predmet
                <select value={addForm.itemId} onChange={(e) => setAddForm({ ...addForm, itemId: e.target.value })}>
                  {items.map((i) => <option key={i.id} value={i.id}>{i.name} ({i.rarity})</option>)}
                </select>
              </label>
              <label>Količina<input type="number" min="1" value={addForm.quantity} onChange={(e) => setAddForm({ ...addForm, quantity: e.target.value })} /></label>
              <label className="checkbox">Opremljeno
                <input type="checkbox" checked={addForm.equipped} onChange={(e) => setAddForm({ ...addForm, equipped: e.target.checked })} />
              </label>
            </div>
            {error && <div className="error">{error}</div>}
            <button type="submit" disabled={items.length === 0}>Dodaj u inventar</button>
          </form>
        </div>

        {inventory.length === 0 ? (
          <p className="empty">Inventar je prazan.</p>
        ) : (
          <table>
            <thead>
              <tr><th>Predmet</th><th>Tip</th><th>Retkost</th><th>Količina</th><th>Status</th><th></th></tr>
            </thead>
            <tbody>
              {inventory.map((e) => (
                <tr key={e.id}>
                  <td><b>{e.item.name}</b></td>
                  <td><span className="tag">{e.item.itemType}</span></td>
                  <td className={`rarity-${e.item.rarity}`}>{e.item.rarity}</td>
                  <td>
                    <button className="btn-sm btn-ghost" onClick={() => changeQty(e, -1)}>−</button>
                    {' '}{e.quantity}{' '}
                    <button className="btn-sm btn-ghost" onClick={() => changeQty(e, 1)}>+</button>
                  </td>
                  <td>{e.equipped ? <span className="tag equipped">opremljeno</span> : <span className="tag">u torbi</span>}</td>
                  <td style={{ whiteSpace: 'nowrap' }}>
                    <button className="btn-sm" onClick={() => toggleEquip(e)}>{e.equipped ? 'Skini' : 'Opremi'}</button>{' '}
                    <button className="btn-sm btn-danger" onClick={() => removeEntry(e)}>Izbaci</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </>
  )
}
