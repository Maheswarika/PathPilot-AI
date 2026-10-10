import { Link } from 'react-router-dom'
import useAuth from '../hooks/useAuth.js'

const featureItems = [
  ['01', 'Find your direction', 'Explore career paths that connect your interests to the skills employers look for.'],
  ['02', 'Make a clear plan', 'Turn a career goal into practical learning steps and track what you have completed.'],
  ['03', 'Keep moving forward', 'Bring your preparation into one calm, focused space built around your next step.'],
]

export default function HomePage() {
  const { user } = useAuth()
  return (
    <div className="marketing">
      <header className="marketing-header">
        <Link className="brand" to="/"><span className="brand-mark">P<span>✦</span></span><span>pathpilot<span className="brand-ai">AI</span></span></Link>
        <nav className="marketing-nav"><a href="#how-it-works">How it works</a><a href="#features">What you can do</a></nav>
        <div className="marketing-actions">{user ? <Link className="button button-primary" to="/dashboard">Go to workspace <span>↗</span></Link> : <><Link className="login-link" to="/login">Log in</Link><Link className="button button-primary" to="/register">Get started <span>↗</span></Link></>}</div>
      </header>
      <main>
        <section className="hero-section">
          <div className="hero-copy">
            <div className="hero-kicker"><span /> A clearer path to your career</div>
            <h1>Your future is a journey. <em>Let's map it.</em></h1>
            <p>Discover careers, understand the skills you need, and turn your ambitions into a roadmap you can actually follow.</p>
            <div className="hero-actions"><Link className="button button-primary button-large" to={user ? '/dashboard' : '/register'}>Start finding your path <span>↗</span></Link><a href="#how-it-works" className="text-link">See how it works <span>↓</span></a></div>
            <div className="hero-proof"><div className="proof-avatars"><span>J</span><span>A</span><span>M</span></div><span>Designed for the next generation of talent</span></div>
          </div>
          <div className="hero-art" aria-label="A preview of the PathPilot career planning dashboard">
            <div className="art-orbit orbit-one" /><div className="art-orbit orbit-two" />
            <div className="art-spark spark-one">✦</div><div className="art-spark spark-two">✧</div>
            <div className="preview-card">
              <div className="preview-top"><div><span className="preview-label">YOUR CAREER SNAPSHOT</span><h3>A path taking shape</h3></div><span className="preview-menu">···</span></div>
              <div className="preview-chart"><div className="chart-y"><span>100</span><span>75</span><span>50</span><span>25</span></div><div className="chart-lines"><i /><i /><i /><i /><svg viewBox="0 0 300 150" role="presentation"><defs><linearGradient id="chartFill" x1="0" x2="0" y1="0" y2="1"><stop offset="0%" stopColor="#7b8cf9" stopOpacity=".24" /><stop offset="100%" stopColor="#7b8cf9" stopOpacity="0" /></linearGradient></defs><path d="M0 125 C30 116 38 92 70 98 S116 112 142 75 S180 84 205 55 S244 65 270 30 S288 34 300 14 V150 H0 Z" fill="url(#chartFill)" /><path d="M0 125 C30 116 38 92 70 98 S116 112 142 75 S180 84 205 55 S244 65 270 30 S288 34 300 14" fill="none" stroke="#6578ef" strokeWidth="3" strokeLinecap="round" /><circle cx="205" cy="55" r="5" fill="#fff" stroke="#6578ef" strokeWidth="3" /></svg></div></div>
              <div className="chart-x"><span>Start</span><span>Skills</span><span>Practice</span><span>Ready</span></div>
              <div className="preview-roadmap"><div className="roadmap-icon">↗</div><div><span>YOUR NEXT MILESTONE</span><b>Build your foundation</b></div><span className="milestone-dot" /></div>
            </div>
            <div className="floating-note"><span className="note-check">✓</span><span><b>Small steps, big momentum</b><small>Progress adds up.</small></span></div>
            <div className="hero-caption">A workspace for your next chapter <span>✦</span></div>
          </div>
        </section>
        <section className="trust-strip"><span>ONE PLACE TO MOVE YOUR CAREER FORWARD</span><div><b>Explore</b><i>✦</i><b>Plan</b><i>✦</i><b>Practice</b><i>✦</i><b>Progress</b></div></section>
        <section className="marketing-features" id="features"><div className="marketing-section-heading"><p className="eyebrow">A LITTLE MORE CLARITY</p><h2>Everything you need to make<br />your next move with confidence.</h2><p>Less guessing. More intentional steps toward work that fits you.</p></div><div className="feature-grid">{featureItems.map(([number, title, copy]) => <article className="feature-card" key={number}><span>{number}</span><div className="feature-symbol">{number === '01' ? '⌕' : number === '02' ? '⌁' : '↗'}</div><h3>{title}</h3><p>{copy}</p><Link to={user ? '/dashboard' : '/register'}>Explore the workspace <span>→</span></Link></article>)}</div></section>
        <section className="journey-section" id="how-it-works"><div><p className="eyebrow">YOUR JOURNEY, YOUR PACE</p><h2>Start where you are.<br /><em>Grow from there.</em></h2></div><p>PathPilot brings career exploration, skill insights, and learning plans together, so every small win has a place to lead.</p><Link className="button button-light" to={user ? '/dashboard' : '/register'}>Build your roadmap <span>↗</span></Link></section>
      </main>
      <footer className="marketing-footer"><Link className="brand" to="/"><span className="brand-mark">P<span>✦</span></span><span>pathpilot<span className="brand-ai">AI</span></span></Link><span>Move forward, one step at a time.</span><span>© 2026 PathPilot AI</span></footer>
    </div>
  )
}
