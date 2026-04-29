import { useState, useEffect } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { verifyAccount } from '../api/auth'

export default function VerifyPage() {
  const [searchParams] = useSearchParams()
  const token = searchParams.get('token')
  const [status, setStatus] = useState<'loading' | 'success' | 'error'>(
    token ? 'loading' : 'error'
  )

  useEffect(() => {
    if (!token) return
    verifyAccount(token)
      .then(() => setStatus('success'))
      .catch(() => setStatus('error'))
  }, [token])

  if (status === 'loading') {
    return (
      <div className="d-flex justify-content-center align-items-center" style={{ minHeight: 'calc(100vh - 56px)' }}>
        <p className="text-muted">Verifying your account…</p>
      </div>
    )
  }

  if (status === 'success') {
    return (
      <div className="d-flex justify-content-center align-items-center" style={{ minHeight: 'calc(100vh - 56px)' }}>
        <div className="text-center">
          <h4 className="text-success mb-2">Account verified!</h4>
          <p className="text-muted mb-4">You can now sign in to NoteShare.</p>
          <Link to="/login" className="btn btn-dark">Go to login</Link>
        </div>
      </div>
    )
  }

  return (
    <div className="d-flex justify-content-center align-items-center" style={{ minHeight: 'calc(100vh - 56px)' }}>
      <div className="text-center">
        <h4 className="text-danger mb-2">Verification failed</h4>
        <p className="text-muted mb-4">The link is invalid or has already been used.</p>
        <Link to="/register" className="btn btn-dark">Back to register</Link>
      </div>
    </div>
  )
}
