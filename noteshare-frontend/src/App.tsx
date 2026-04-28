import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import Navbar from './components/Navbar'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import NotesPage from './pages/NotesPage'
import PublicNotePage from './pages/PublicNotePage'
import VerifyPage from './pages/VerifyPage'

export default function App() {
  return (
    <BrowserRouter>
      <Navbar />
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/verify" element={<VerifyPage />} />
        <Route path="/notes" element={<NotesPage />} />
        <Route path="/share/:token" element={<PublicNotePage />} />
        <Route path="*" element={<Navigate to="/notes" replace />} />
      </Routes>
    </BrowserRouter>
  )
}
