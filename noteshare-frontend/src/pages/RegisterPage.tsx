import { useState, FormEvent } from 'react'
import { Link } from 'react-router-dom'
import { register } from '../api/auth'

export default function RegisterPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [done, setDone] = useState(false)

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setError('')
    try {
      await register(email, password)
      setDone(true)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Registration failed')
    }
  }

  if (done) {
    return (
      <div className="d-flex justify-content-center align-items-center" style={{ minHeight: 'calc(100vh - 56px)' }}>
        <div className="text-center">
          <h4>Check your email</h4>
          <p className="text-muted">We sent a verification link to <strong>{email}</strong></p>
          <Link to="/login" className="btn btn-dark">Go to login</Link>
        </div>
      </div>
    )
  }

  return (
    <div className="d-flex justify-content-center align-items-center" style={{ minHeight: 'calc(100vh - 56px)' }}>
      <div className="card shadow" style={{ width: 400 }}>
        <div className="card-body p-4">
          <h4 className="card-title mb-4">Create account</h4>
          {error && <div className="alert alert-danger py-2 small">{error}</div>}
          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label className="form-label">Email</label>
              <input
                type="email"
                className="form-control"
                value={email}
                onChange={e => setEmail(e.target.value)}
                required
                autoFocus
              />
            </div>
            <div className="mb-3">
              <label className="form-label">Password</label>
              <input
                type="password"
                className="form-control"
                value={password}
                onChange={e => setPassword(e.target.value)}
                required
              />
            </div>
            <button type="submit" className="btn btn-dark w-100">Register</button>
          </form>
          <p className="text-center mt-3 mb-0 small">
            Have an account? <Link to="/login">Login</Link>
          </p>
        </div>
      </div>
    </div>
  )
}
