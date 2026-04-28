export interface ShareResponse {
  id: number
  noteId: number
  token: string
  createdAt: string
}

export interface NoteDto {
  id: number
  userId: number
  title: string
  content: string
  createdAt: string
  updatedAt: string
}

export async function createShare(noteId: number): Promise<ShareResponse> {
  const token = localStorage.getItem('token')
  const res = await fetch(`/api/share/${noteId}`, {
    method: 'POST',
    headers: token ? { Authorization: `Bearer ${token}` } : {},
  })
  if (!res.ok) throw new Error(await res.text())
  return res.json()
}

export async function resolveShare(token: string): Promise<NoteDto> {
  const res = await fetch(`/api/share/${token}`)
  if (!res.ok) throw new Error(await res.text())
  return res.json()
}
