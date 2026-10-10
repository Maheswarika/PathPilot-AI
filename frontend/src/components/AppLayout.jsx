import { useState } from 'react'
import { NavLink, Link, useLocation, useNavigate } from 'react-router-dom'
import useAuth from '../hooks/useAuth.js'
import { initials } from '../utils/format.js'

const navGroups = [
  { title: 'OVERVIEW', links: [['/dashboard', '⌂', 'Dashboard'], ['/profile', '◉', 'My profile']] },
  { title: 'CAREER TOOLS', links: [['/careers', '◇', 'Explore careers'], ['/roadmap', '⌁', 'Career roadmap'], ['/skills', '◈', 'Skill gap analysis'], ['/resources', '▤', 'Learning resources']] },
  { title: 'PREPARATION', links: [['/interview-prep', '✳', 'Interview prep'], ['/resume-review', '▧', 'Resume review'], ['/internships', '▣', 'Internship tracker']] },
  { title: 'INSIGHTS', links: [['/assistant', '✧', 'Career assistant'], ['/progress', '↗', 'Progress tracking']] },
]

export default function AppLayout({ children }) {
  const [open, setOpen] = useState(false)
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const title = navGroups.flatMap((group) => group.links).find(([path]) => path === location.pathname)?.[2] || 'Workspace'

  function signOut() {
    logout()
    navigate('/')
  }

  return (
    <div className="app-shell">
      <aside className={`sidebar ${open ? 'sidebar-open' : ''}`}>
        <Link className="brand sidebar-brand" to="/dashboard" onClick={() => setOpen(false)}>
          <span className="brand-mark">P<span>✦</span></span><span>pathpilot<span className="brand-ai">AI</span></span>
        </Link>
        <div className="sidebar-label">YOUR WORKSPACE</div>
        <nav className="side-nav" aria-label="Main navigation">
          {navGroups.map((group) => (
            <div className="nav-group" key={group.title}>
              <p>{group.title}</p>
              {group.links.map(([path, icon, label]) => (
                <NavLink key={path} to={path} onClick={() => setOpen(false)} className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
                  <span className="nav-icon" aria-hidden="true">{icon}</span>{label}
                </NavLink>
              ))}
            </div>
          ))}
          {user?.roles?.includes('ROLE_ADMIN') && (
            <div className="nav-group"><p>MANAGEMENT</p><NavLink to="/admin" onClick={() => setOpen(false)} className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}><span className="nav-icon">⚙</span>Admin dashboard</NavLink></div>
          )}
        </nav>
        <div className="sidebar-bottom">
          <div className="help-card"><span>Need a little direction?</span><p>Your next step is closer than you think.</p><Link to="/assistant">Explore your options <span>↗</span></Link></div>
          <button className="profile-menu" onClick={signOut} title="Sign out">
            <span className="avatar">{initials(user?.name)}</span><span className="profile-menu-copy"><b>{user?.name || 'Student'}</b><small>{user?.email}</small></span><span className="logout-icon">↗</span>
          </button>
        </div>
      </aside>
      {open && <button className="sidebar-scrim" aria-label="Close menu" onClick={() => setOpen(false)} />}
      <main className="main-area">
        <header className="topbar">
          <button className="mobile-menu" aria-label="Open navigation" onClick={() => setOpen(!open)}>☰</button>
          <div className="breadcrumbs"><span>Workspace</span><span>/</span><b>{title}</b></div>
          <div className="topbar-right"><span className="topbar-date">Your next chapter starts here</span><span className="top-avatar">{initials(user?.name)}</span></div>
        </header>
        <div className="content-area">{children}</div>
        <footer className="app-footer"><span>PathPilot AI</span><span>Find your direction. Build your future.</span></footer>
      </main>
    </div>
  )
}
