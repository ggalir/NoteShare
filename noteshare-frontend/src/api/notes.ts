export interface Note {
  id: number
  userId: number
  title: string
  content: string
  createdAt: string
  updatedAt: string
}

function authHeaders(): HeadersInit {
  const token = localStorage.getItem('token')
  return {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  }
}

export async function getNotes(): Promise<Note[]> {
  const res = await fetch('/api/notes', { headers: authHeaders() })
  if (!res.ok) throw new Error(await res.text())
  return res.json()
}

export async function createNote(title: string, content: string): Promise<Note> {
  const res = await fetch('/api/notes', {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify({ title, content }),
  })
  if (!res.ok) throw new Error(await res.text())
  return res.json()
}

export async function updateNote(id: number, title: string, content: string): Promise<Note> {
  const res = await fetch(`/api/notes/${id}`, {
    method: 'PUT',
    headers: authHeaders(),
    body: JSON.stringify({ title, content }),
  })
  if (!res.ok) throw new Error(await res.text())
  return res.json()
}

export async function deleteNote(id: number): Promise<void> {
  const res = await fetch(`/api/notes/${id}`, {
    method: 'DELETE',
    headers: authHeaders(),
  })
  if (!res.ok) throw new Error(await res.text())
}
