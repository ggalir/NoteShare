import { Link, useNavigate } from 'react-router-dom'

export default function Navbar() {
  const navigate = useNavigate()
  const isLoggedIn = !!localStorage.getItem('token')

  function logout() {
    localStorage.removeItem('token')
    navigate('/login')
  }

  return (
    <nav className="navbar navbar-dark bg-dark px-3" style={{ height: 56 }}>
      <Link className="navbar-brand fw-bold" to="/notes">NoteShare</Link>
      <div className="d-flex gap-2">
        {isLoggedIn ? (
          <button className="btn btn-outline-light btn-sm" onClick={logout}>Logout</button>
        ) : (
          <>
            <Link className="btn btn-outline-light btn-sm" to="/login">Login</Link>
            <Link className="btn btn-light btn-sm" to="/register">Register</Link>
          </>
        )}
      </div>
    </nav>
  )
}
