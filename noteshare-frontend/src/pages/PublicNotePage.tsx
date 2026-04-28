import { useState, useEffect } from 'react'
import { useParams } from 'react-router-dom'
import ReactMarkdown from 'react-markdown'
import { type NoteDto, resolveShare } from '../api/sharing'

export default function PublicNotePage() {
  const { token } = useParams<{ token: string }>()
  const [note, setNote] = useState<NoteDto | null>(null)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!token) return
    resolveShare(token)
      .then(setNote)
      .catch(() => setError('Note not found or the link has expired.'))
  }, [token])

  if (error) {
    return (
      <div className="container mt-5 text-center">
        <h5 className="text-danger">{error}</h5>
      </div>
    )
  }

  if (!note) {
    return (
      <div className="container mt-5 text-center text-muted">Loading…</div>
    )
  }

  return (
    <div className="container mt-4" style={{ maxWidth: 800 }}>
      <h2 className="mb-1">{note.title}</h2>
      <p className="text-muted small mb-4">Shared via NoteShare</p>
      <div className="border rounded p-4 bg-white">
        <ReactMarkdown>{note.content}</ReactMarkdown>
      </div>
    </div>
  )
}
