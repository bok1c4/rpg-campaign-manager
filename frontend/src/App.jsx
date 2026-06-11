import { Routes, Route, Navigate } from 'react-router-dom'
import Navbar from './components/Navbar.jsx'
import ProtectedRoute from './auth/ProtectedRoute.jsx'
import LoginPage from './pages/LoginPage.jsx'
import RegisterPage from './pages/RegisterPage.jsx'
import CampaignsPage from './pages/CampaignsPage.jsx'
import CampaignDetailPage from './pages/CampaignDetailPage.jsx'
import CharactersPage from './pages/CharactersPage.jsx'
import CharacterDetailPage from './pages/CharacterDetailPage.jsx'
import ItemsPage from './pages/ItemsPage.jsx'

export default function App() {
  return (
    <>
      <Navbar />
      <main className="container">
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/campaigns" element={<ProtectedRoute><CampaignsPage /></ProtectedRoute>} />
          <Route path="/campaigns/:id" element={<ProtectedRoute><CampaignDetailPage /></ProtectedRoute>} />
          <Route path="/characters" element={<ProtectedRoute><CharactersPage /></ProtectedRoute>} />
          <Route path="/characters/:id" element={<ProtectedRoute><CharacterDetailPage /></ProtectedRoute>} />
          <Route path="/items" element={<ProtectedRoute><ItemsPage /></ProtectedRoute>} />
          <Route path="*" element={<Navigate to="/campaigns" replace />} />
        </Routes>
      </main>
    </>
  )
}
