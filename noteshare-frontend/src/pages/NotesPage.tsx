import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import ReactMarkdown from 'react-markdown'
import { type Note, getNotes, createNote, updateNote, deleteNote } from '../api/notes'
import { createShare } from '../api/sharing'

export default function NotesPage() {
  const [notes, setNotes] = useState<Note[]>([])
  const [selected, setSelected] = useState<Note | null>(null)
  const [title, setTitle] = useState('')
  const [content, setContent] = useState('')
  const [shareLink, setShareLink] = useState('')
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const navigate = useNavigate()

  useEffect(() => {
    if (!localStorage.getItem('token')) {
      navigate('/login')
      return
    }
    getNotes()
      .then(setNotes)
      .catch(() => navigate('/login'))
  }, [navigate])

  function selectNote(note: Note) {
    setSelected(note)
    setTitle(note.title)
    setContent(note.content)
    setShareLink('')
    setError('')
  }

  function newNote() {
    setSelected(null)
    setTitle('')
    setContent('')
    setShareLink('')
    setError('')
  }

  async function save() {
    if (!title.trim()) return
    setSaving(true)
    try {
      if (selected) {
        const updated = await updateNote(selected.id, title, content)
        setSelected(updated)
        setNotes(prev => prev.map(n => n.id === updated.id ? updated : n))
      } else {
        const created = await createNote(title, content)
        setNotes(prev => [created, ...prev])
        setSelected(created)
      }
      setError('')
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to save note')
    } finally {
      setSaving(false)
    }
  }

  async function remove() {
    if (!selected || !window.confirm('Delete this note?')) return
    try {
      await deleteNote(selected.id)
      setNotes(prev => prev.filter(n => n.id !== selected.id))
      newNote()
    } catch {
      setError('Failed to delete note')
    }
  }

  async function share() {
    if (!selected) return
    setShareLink('')
    try {
      const { token } = await createShare(selected.id)
      setShareLink(`${window.location.origin}/share/${token}`)
      setError('')
    } catch {
      setError('Failed to generate share link')
    }
  }

  const hasChanges = selected
    ? title !== selected.title || content !== selected.content
    : title !== '' || content !== ''

  return (
    <div className="d-flex" style={{ height: 'calc(100vh - 56px)' }}>
      {/* Sidebar */}
      <div className="d-flex flex-column border-end bg-light" style={{ width: 240, minWidth: 240 }}>
        <div className="p-2 border-bottom">
          <button className="btn btn-dark btn-sm w-100" onClick={newNote}>+ New note</button>
        </div>
        <div className="overflow-auto flex-grow-1">
          {notes.map(note => (
            <button
              key={note.id}
              onClick={() => selectNote(note)}
              className={`btn btn-link text-start w-100 px-3 py-2 border-bottom text-decoration-none rounded-0 ${
                selected?.id === note.id ? 'fw-semibold text-dark bg-white' : 'text-secondary'
              }`}
            >
              <div className="text-truncate small">{note.title || 'Untitled'}</div>
            </button>
          ))}
          {notes.length === 0 && (
            <p className="text-muted text-center small mt-4 px-2">No notes yet. Create one!</p>
          )}
        </div>
      </div>

      {/* Editor area */}
      <div className="d-flex flex-column flex-grow-1 overflow-hidden">
        {/* Toolbar */}
        <div className="d-flex align-items-center gap-2 px-3 py-2 border-bottom bg-white">
          <input
            className="form-control form-control-sm"
            placeholder="Note title"
            value={title}
            onChange={e => setTitle(e.target.value)}
          />
          <button
            className="btn btn-dark btn-sm text-nowrap"
            onClick={save}
            disabled={saving || !hasChanges || !title.trim()}
          >
            {saving ? 'Saving…' : 'Save'}
          </button>
          {selected && (
            <>
              <button className="btn btn-outline-secondary btn-sm text-nowrap" onClick={share}>
                Share
              </button>
              <button className="btn btn-outline-danger btn-sm" onClick={remove}>
                Delete
              </button>
            </>
          )}
        </div>

        {/* Error banner */}
        {error && (
          <div className="d-flex align-items-center gap-2 px-3 py-2 bg-danger-subtle border-bottom">
            <span className="small text-danger flex-grow-1">{error}</span>
            <button
              type="button"
              className="btn-close btn-sm"
              onClick={() => setError('')}
            />
          </div>
        )}

        {/* Share link banner */}
        {shareLink && (
          <div className="d-flex align-items-center gap-2 px-3 py-2 bg-success-subtle border-bottom">
            <span className="small text-truncate flex-grow-1">{shareLink}</span>
            <button
              className="btn btn-sm btn-outline-success text-nowrap"
              onClick={() => navigator.clipboard.writeText(shareLink)}
            >
              Copy link
            </button>
          </div>
        )}

        {/* Split view: editor + preview */}
        <div className="d-flex flex-grow-1 overflow-hidden">
          <textarea
            className="flex-grow-1 p-3 font-monospace border-0 border-end"
            style={{ resize: 'none', outline: 'none', width: '50%' }}
            placeholder="Write Markdown here…"
            value={content}
            onChange={e => setContent(e.target.value)}
          />
          <div className="p-3 overflow-auto" style={{ width: '50%' }}>
            {content ? (
              <ReactMarkdown>{content}</ReactMarkdown>
            ) : (
              <p className="text-muted small">Preview will appear here</p>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}