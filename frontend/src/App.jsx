import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import AppLayout from './components/AppLayout.jsx'
import ProtectedRoute from './components/ProtectedRoute.jsx'
import { AuthProvider } from './context/AuthContext.jsx'
import {
  AdminPage,
  AssistantPage,
  CareerExplorationPage,
  DashboardPage,
  InterviewPage,
  InternshipTrackerPage,
  LearningResourcesPage,
  LoginPage,
  ProfilePage,
  ProgressPage,
  RegistrationPage,
  ResumeReviewPage,
  RoadmapPage,
  SkillGapPage,
} from './pages/Pages.jsx'
import HomePage from './pages/HomePage.jsx'
import './App.css'

function Workspace({ children }) {
  return (
    <ProtectedRoute>
      <AppLayout>{children}</AppLayout>
    </ProtectedRoute>
  )
}

export default function App() {
  return (
    <BrowserRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
      <AuthProvider>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegistrationPage />} />
          <Route path="/dashboard" element={<Workspace><DashboardPage /></Workspace>} />
          <Route path="/profile" element={<Workspace><ProfilePage /></Workspace>} />
          <Route path="/careers" element={<Workspace><CareerExplorationPage /></Workspace>} />
          <Route path="/roadmap" element={<Workspace><RoadmapPage /></Workspace>} />
          <Route path="/skills" element={<Workspace><SkillGapPage /></Workspace>} />
          <Route path="/resources" element={<Workspace><LearningResourcesPage /></Workspace>} />
          <Route path="/interview-prep" element={<Workspace><InterviewPage /></Workspace>} />
          <Route path="/resume-review" element={<Workspace><ResumeReviewPage /></Workspace>} />
          <Route path="/internships" element={<Workspace><InternshipTrackerPage /></Workspace>} />
          <Route path="/assistant" element={<Workspace><AssistantPage /></Workspace>} />
          <Route path="/progress" element={<Workspace><ProgressPage /></Workspace>} />
          <Route path="/admin" element={<Workspace><AdminPage /></Workspace>} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}
