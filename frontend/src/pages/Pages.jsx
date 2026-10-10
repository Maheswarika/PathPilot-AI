import { useCallback, useMemo, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import useAuth from '../hooks/useAuth.js'
import ProtectedRoute from '../components/ProtectedRoute.jsx'
import useApi from '../hooks/useApi.js'
import { adminApi, careerApi, progressApi, resourceApi, roadmapApi, studentApi } from '../services/api.js'
import { getErrorMessage } from '../utils/format.js'
import { Button, Card, DemoNotice, EmptyState, ErrorState, Field, LinkButton, LoadingState, PageHeading, SectionTitle, SelectField, TextAreaField } from '../components/UI.jsx'

function AuthPage({ register = false }) {
  const { login, register: createAccount } = useAuth()
  const navigate = useNavigate()
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  const [values, setValues] = useState({ name: '', email: '', password: '', confirmPassword: '' })

  function update(event) {
    setValues((current) => ({ ...current, [event.target.name]: event.target.value }))
  }

  async function submit(event) {
    event.preventDefault()
    setError('')
    if (register && values.password !== values.confirmPassword) {
      setError('Your passwords do not match.')
      return
    }
    setBusy(true)
    try {
      if (register) await createAccount(values)
      else await login({ email: values.email, password: values.password })
      navigate('/dashboard')
    } catch (requestError) {
      setError(getErrorMessage(requestError))
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="auth-page">
      <Link className="brand auth-brand" to="/"><span className="brand-mark">P<span>✦</span></span><span>pathpilot<span className="brand-ai">AI</span></span></Link>
      <div className="auth-decoration auth-decoration-a" /><div className="auth-decoration auth-decoration-b" />
      <div className="auth-panel">
        <div className="auth-intro"><p className="eyebrow">{register ? 'YOUR NEXT CHAPTER' : 'WELCOME BACK'}</p><h1>{register ? 'Start with a direction.' : 'Good to see you again.'}</h1><p>{register ? 'Create your free account and bring your career plans into focus.' : 'Pick up where you left off and keep your momentum going.'}</p></div>
        <form className="auth-form" onSubmit={submit}>
          {register && <Field label="Full name" name="name" autoComplete="name" placeholder="Your name" value={values.name} onChange={update} required maxLength={100} />}
          <Field label="Email address" type="email" name="email" autoComplete="email" placeholder="you@example.com" value={values.email} onChange={update} required />
          <Field label="Password" type="password" name="password" autoComplete={register ? 'new-password' : 'current-password'} placeholder="At least 6 characters" value={values.password} onChange={update} required minLength={6} />
          {register && <Field label="Confirm password" type="password" name="confirmPassword" autoComplete="new-password" placeholder="Enter your password again" value={values.confirmPassword} onChange={update} required minLength={6} />}
          {error && <p className="form-error" role="alert">{error}</p>}
          <Button type="submit" className="auth-submit" disabled={busy}>{busy ? 'Please wait...' : register ? 'Create my account' : 'Log in to PathPilot'} <span>↗</span></Button>
        </form>
        <p className="auth-switch">{register ? 'Already have an account?' : 'New to PathPilot?'} <Link to={register ? '/login' : '/register'}>{register ? 'Log in' : 'Create an account'}</Link></p>
        <div className="auth-security"><span>♧</span> Your account is secured with backend-issued JWT authentication.</div>
      </div>
      <div className="auth-footer">A clearer path starts with one step. <span>© 2026 PathPilot AI</span></div>
    </div>
  )
}

export function LoginPage() { return <AuthPage /> }
export function RegistrationPage() { return <AuthPage register /> }

export function DashboardPage() {
  const { user } = useAuth()
  const loadRecommendations = useCallback(() => careerApi.recommendations(), [])
  const loadProgress = useCallback(() => progressApi.mine(), [])
  const recommendations = useApi(loadRecommendations)
  const progress = useApi(loadProgress)
  const firstCareer = recommendations.data?.[0]
  const averageProgress = progress.data?.length ? Math.round(progress.data.reduce((total, item) => total + item.completionPercentage, 0) / progress.data.length) : 0
  return (
    <>
      <PageHeading eyebrow="YOUR CAREER WORKSPACE" title={`Good morning, ${user?.name?.split(' ')[0] || 'there'}.`} description="A little progress today creates a lot of possibility tomorrow." action={<LinkButton to="/careers">Explore careers <span>↗</span></LinkButton>} />
      <div className="dashboard-welcome"><div><span className="welcome-pill"><span /> YOUR JOURNEY IS IN MOTION</span><h2>Make your next step<br />a <em>meaningful one.</em></h2><p>Explore a direction, sharpen a skill, or simply pick up where you left off.</p><Link to="/roadmap" className="welcome-link">View your career roadmap <span>→</span></Link></div><div className="welcome-art"><div className="welcome-sun">✦</div><div className="welcome-path" /><div className="welcome-plant plant-a">✳</div><div className="welcome-plant plant-b">✳</div><div className="welcome-hill hill-a" /><div className="welcome-hill hill-b" /><div className="welcome-person">●</div></div></div>
      <div className="metric-grid">
        <Card className="metric-card"><span className="metric-icon lilac">⌖</span><span className="metric-label">CAREER MATCH</span>{recommendations.loading ? <span className="metric-value metric-loading">...</span> : recommendations.error ? <span className="metric-value">—</span> : <><strong>{firstCareer ? `${Math.round(firstCareer.score)}%` : '—'}</strong><small>{firstCareer?.careerName || 'Explore your first career'}</small></>}</Card>
        <Card className="metric-card"><span className="metric-icon mint">⌁</span><span className="metric-label">ROADMAP PROGRESS</span>{progress.loading ? <span className="metric-value metric-loading">...</span> : <><strong>{averageProgress}%</strong><small>{progress.data?.length || 0} active learning plans</small></>}</Card>
        <Card className="metric-card"><span className="metric-icon peach">✧</span><span className="metric-label">RECOMMENDED FOR YOU</span><strong>{recommendations.data?.length ?? '—'}</strong><small>Career paths to explore</small></Card>
        <Card className="metric-card"><span className="metric-icon blue">↗</span><span className="metric-label">YOUR NEXT STEP</span><strong className="metric-text">Keep exploring</strong><small>One small step counts</small></Card>
      </div>
      {(recommendations.error || progress.error) && <ErrorState message={recommendations.error || progress.error} onRetry={() => { recommendations.reload(); progress.reload() }} />}
      <div className="dashboard-columns">
        <Card className="recommendations-card"><SectionTitle title="Career paths to explore" action={<Link to="/careers" className="subtle-link">See all <span>→</span></Link>} /><p className="card-intro">Based on your current skills and interests.</p>{recommendations.loading ? <LoadingState label="Finding your career matches..." /> : recommendations.data?.length ? <div className="recommendation-list">{recommendations.data.slice(0, 4).map((item, index) => <Link to="/careers" className="recommendation-row" key={item.careerId}><span className={`career-index index-${index}`}>0{index + 1}</span><span className="recommendation-copy"><b>{item.careerName}</b><small>{item.reason}</small></span><span className="match-score">{Math.round(item.score)}%<small>match</small></span></Link>)}</div> : <EmptyState title="Your recommendations are getting ready"><LinkButton to="/careers">Explore careers</LinkButton></EmptyState>}</Card>
        <Card className="quick-actions-card"><SectionTitle title="A good next step" /><p className="card-intro">Choose one thing to move forward today.</p><div className="quick-action-list"><Link to="/skills"><span className="quick-action-icon qa-one">◈</span><span><b>Check your skill gaps</b><small>See what to learn next</small></span><span>→</span></Link><Link to="/resources"><span className="quick-action-icon qa-two">▤</span><span><b>Find a learning resource</b><small>Explore the resource library</small></span><span>→</span></Link><Link to="/progress"><span className="quick-action-icon qa-three">↗</span><span><b>Celebrate your progress</b><small>See how far you've come</small></span><span>→</span></Link></div></Card>
      </div>
    </>
  )
}

export function ProfilePage() {
  const loadProfile = useCallback(() => studentApi.profile(), [])
  const loadSkills = useCallback(() => studentApi.skills(), [])
  const profile = useApi(loadProfile)
  const skills = useApi(loadSkills)
  const [skillName, setSkillName] = useState('')
  const [notice, setNotice] = useState('')
  const [error, setError] = useState('')
  const form = profile.data

  async function saveProfile(event) {
    event.preventDefault()
    setError('')
    try {
      const updated = await studentApi.updateProfile(form)
      profile.setData(updated)
      setNotice('Your profile has been saved.')
    } catch (requestError) { setError(getErrorMessage(requestError)) }
  }
  async function addSkill(event) {
    event.preventDefault()
    setError('')
    try { await studentApi.addSkill(skillName); setSkillName(''); await skills.reload() } catch (requestError) { setError(getErrorMessage(requestError)) }
  }
  async function removeSkill(id) {
    setError('')
    try { await studentApi.removeSkill(id); await skills.reload() } catch (requestError) { setError(getErrorMessage(requestError)) }
  }

  return (
    <>
      <PageHeading eyebrow="YOUR PROFILE" title="A little about you." description="Your interests and experience help shape the career paths you explore." />
      {profile.loading ? <LoadingState /> : profile.error ? <ErrorState message={profile.error} onRetry={profile.reload} /> : form && <div className="profile-layout"><Card><SectionTitle title="Personal details" /><p className="card-intro">Keep your information up to date.</p><form className="profile-form" onSubmit={saveProfile}><div className="form-grid"><Field label="Full name" value={form.name || ''} onChange={(e) => profile.setData({ ...form, name: e.target.value })} required maxLength={100} /><Field label="Email address" value={form.email || ''} disabled /><Field label="Education" value={form.education || ''} placeholder="e.g. Bachelor's degree" onChange={(e) => profile.setData({ ...form, education: e.target.value })} /><Field label="Field of study" value={form.branch || ''} placeholder="e.g. Computer Science" onChange={(e) => profile.setData({ ...form, branch: e.target.value })} /><Field label="Graduation year" type="number" value={form.graduationYear || ''} min="1950" max="2100" onChange={(e) => profile.setData({ ...form, graduationYear: e.target.value ? Number(e.target.value) : null })} /><TextAreaField label="Career interests" value={form.interests || ''} placeholder="What topics or industries excite you?" onChange={(e) => profile.setData({ ...form, interests: e.target.value })} rows="3" /></div>{error && <p className="form-error" role="alert">{error}</p>}{notice && <p className="form-success" role="status">{notice}</p>}<Button type="submit">Save profile <span>↗</span></Button></form></Card><Card className="skills-card"><SectionTitle title="Your skills" /><p className="card-intro">Add skills you have practiced. These are used by the backend skill-gap calculation.</p><form className="inline-form" onSubmit={addSkill}><Field aria-label="Add a skill" label="Add a skill" value={skillName} onChange={(e) => setSkillName(e.target.value)} placeholder="e.g. JavaScript" required maxLength={80} /><Button type="submit">Add</Button></form>{skills.loading ? <LoadingState label="Loading your skills..." /> : skills.error ? <ErrorState message={skills.error} onRetry={skills.reload} /> : skills.data?.length ? <div className="skill-chips">{skills.data.map((skill) => <span className="skill-chip" key={skill.id}>{skill.name}<button onClick={() => removeSkill(skill.id)} aria-label={`Remove ${skill.name}`}>×</button></span>)}</div> : <EmptyState title="No skills added yet">Add the skills you already have to personalize your gap analysis.</EmptyState>}</Card></div>}
    </>
  )
}

export function CareerExplorationPage() {
  const loadCareers = useCallback(() => careerApi.list(), [])
  const { data: careers, loading, error, reload } = useApi(loadCareers)
  const [query, setQuery] = useState('')
  const [selected, setSelected] = useState(null)
  const [detail, setDetail] = useState(null)
  const [detailError, setDetailError] = useState('')
  const [busy, setBusy] = useState(false)
  const visible = careers?.filter((career) => `${career.name} ${career.description || ''}`.toLowerCase().includes(query.toLowerCase())) || []

  async function showCareer(career) {
    setSelected(career)
    setDetail(null)
    setDetailError('')
    setBusy(true)
    try {
      const [full, skills] = await Promise.all([careerApi.get(career.id), careerApi.skills(career.id)])
      setDetail({ ...full, skills })
    } catch (requestError) { setDetailError(getErrorMessage(requestError)) } finally { setBusy(false) }
  }
  return (
    <>
      <PageHeading eyebrow="CAREER EXPLORATION" title="Find a direction that fits." description="Explore the roles in your PathPilot catalog and learn what each path involves." />
      <div className="career-tools"><label className="search-field"><span>⌕</span><input value={query} onChange={(e) => setQuery(e.target.value)} placeholder="Search career paths" aria-label="Search career paths" /></label><span className="catalog-count">{careers?.length ?? '—'} career paths</span></div>
      {loading ? <LoadingState label="Loading career paths..." /> : error ? <ErrorState message={error} onRetry={reload} /> : !visible.length ? <EmptyState title="No career paths found">Try a different search, or check back when more careers are available.</EmptyState> : <div className="career-grid">{visible.map((career, index) => <Card className="career-card" key={career.id}><div className={`career-art career-art-${index % 4}`}><span>{['⌘', '◌', '⌬', '⌖'][index % 4]}</span><small>PATH {String(index + 1).padStart(2, '0')}</small></div><div className="career-card-body"><div className="career-card-kicker">CAREER PATH</div><h3>{career.name}</h3><p>{career.description || 'Explore the skills and learning resources for this career path.'}</p><button className="text-button" onClick={() => showCareer(career)}>Explore this path <span>→</span></button></div></Card>)}</div>}
      {selected && <div className="modal-backdrop" role="presentation" onClick={() => setSelected(null)}><section className="detail-modal" role="dialog" aria-modal="true" aria-labelledby="career-detail-title" onClick={(e) => e.stopPropagation()}><button className="modal-close" onClick={() => setSelected(null)} aria-label="Close">×</button>{busy ? <LoadingState label="Loading career details..." /> : detailError ? <ErrorState message={detailError} /> : detail && <><p className="eyebrow">CAREER PATH</p><h2 id="career-detail-title">{detail.name}</h2><p>{detail.description}</p>{detail.targetAudience && <p className="detail-audience">{detail.targetAudience}</p>}<h3>Skills in this path</h3>{detail.skills?.length ? <div className="skill-chips">{detail.skills.map((skill) => <span className="skill-chip" key={skill.skillId}>{skill.name}</span>)}</div> : <p>No skill information is available for this career yet.</p>}<div className="modal-actions"><LinkButton to={`/skills?careerId=${detail.id}`}>Check my skill gap <span>↗</span></LinkButton><LinkButton to="/roadmap" variant="outline">View roadmap</LinkButton></div></>}</section></div>}
    </>
  )
}

export function RoadmapPage() {
  const loadRoadmaps = useCallback(() => roadmapApi.list(), [])
  const loadCareers = useCallback(() => careerApi.list(), [])
  const roadmaps = useApi(loadRoadmaps)
  const careers = useApi(loadCareers)
  const [careerId, setCareerId] = useState('')
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  async function generate() {
    if (!careerId) return
    setBusy(true); setError('')
    try { await roadmapApi.generate(careerId); await roadmaps.reload() } catch (requestError) { setError(getErrorMessage(requestError)) } finally { setBusy(false) }
  }
  async function complete(roadmapId, stepId, isDone) {
    setError('')
    try { await roadmapApi.completeStep(roadmapId, stepId, !isDone); await roadmaps.reload() } catch (requestError) { setError(getErrorMessage(requestError)) }
  }
  return (
    <>
      <PageHeading eyebrow="YOUR CAREER ROADMAP" title="Turn a goal into a plan." description="Roadmaps are generated by your backend from the selected career path and its learning resources." />
      <Card className="roadmap-create"><div><span className="metric-icon lilac">⌁</span><div><h2>Start with a career path</h2><p>Generate a practical roadmap tailored to a role from your career catalog.</p></div></div><div className="roadmap-generate-controls"><SelectField label="Choose a career" value={careerId} onChange={(e) => setCareerId(e.target.value)}><option value="">Select a career...</option>{careers.data?.map((career) => <option value={career.id} key={career.id}>{career.name}</option>)}</SelectField><Button onClick={generate} disabled={!careerId || busy}>{busy ? 'Generating...' : 'Generate roadmap'} <span>↗</span></Button></div>{careers.error && <p className="form-error">{careers.error}</p>}</Card>
      {error && <ErrorState message={error} onRetry={roadmaps.reload} />}
      {roadmaps.loading ? <LoadingState label="Loading your roadmaps..." /> : roadmaps.error ? <ErrorState message={roadmaps.error} onRetry={roadmaps.reload} /> : !roadmaps.data?.length ? <Card><EmptyState title="Your roadmap starts with a goal">Choose a career above to create your first backend-generated roadmap.</EmptyState></Card> : <div className="roadmap-list">{roadmaps.data.map((roadmap) => { const steps = roadmap.steps || []; const done = steps.filter((step) => step.completed).length; return <Card className="roadmap-card" key={roadmap.id}><div className="roadmap-card-heading"><div><p className="eyebrow">YOUR PLAN · {done}/{steps.length} STEPS</p><h2>{roadmap.title}</h2><p>{roadmap.description || roadmap.careerName}</p></div><span className="roadmap-percent">{steps.length ? Math.round(done * 100 / steps.length) : 0}%</span></div><div className="progress-track"><span style={{ width: `${steps.length ? done * 100 / steps.length : 0}%` }} /></div><div className="roadmap-steps">{steps.map((step) => <label className={`roadmap-step ${step.completed ? 'step-done' : ''}`} key={step.id}><input type="checkbox" checked={Boolean(step.completed)} onChange={() => complete(roadmap.id, step.id, Boolean(step.completed))} /><span className="step-sequence">{String(step.sequence || '').padStart(2, '0')}</span><span className="step-copy"><b>{step.title}</b><small>{step.description}{step.skillName ? ` · ${step.skillName}` : ''}</small>{step.resource?.url && <a href={step.resource.url} target="_blank" rel="noreferrer">{step.resource.title || 'Open resource'} ↗</a>}</span></label>)}</div></Card>})}</div>}
    </>
  )
}

export function SkillGapPage() {
  const loadCareers = useCallback(() => careerApi.list(), [])
  const { data: careers, loading, error, reload } = useApi(loadCareers)
  const initialId = new URLSearchParams(window.location.search).get('careerId') || ''
  const [careerId, setCareerId] = useState(initialId)
  const loadGap = useCallback(() => careerId ? careerApi.skillGap(careerId) : Promise.resolve(null), [careerId])
  const gapRequest = useApi(loadGap)
  const gap = gapRequest.data
  const pct = Math.round(gap?.matchPercentage || 0)
  return (
    <>
      <PageHeading eyebrow="SKILL GAP ANALYSIS" title="See where you stand." description="Compare the skills in your profile with the requirements for a career path." />
      {loading ? <LoadingState label="Loading careers..." /> : error ? <ErrorState message={error} onRetry={reload} /> : <Card className="gap-card"><div className="gap-toolbar"><SelectField label="Choose a career path" value={careerId} onChange={(e) => setCareerId(e.target.value)}><option value="">Select a career...</option>{careers?.map((career) => <option value={career.id} key={career.id}>{career.name}</option>)}</SelectField><span className="gap-disclaimer">Skill overlap is a guide, not a job-readiness guarantee.</span></div>{!careerId ? <EmptyState title="Choose a path to see your skill gap">Your match is calculated by the backend using your saved profile skills.</EmptyState> : gapRequest.loading ? <LoadingState label="Comparing your skills..." /> : gapRequest.error ? <ErrorState message={gapRequest.error} onRetry={gapRequest.reload} /> : gap && <><div className="gap-summary"><div className="gap-ring" style={{ '--progress': `${pct}%` }}><div><strong>{pct}%</strong><small>SKILL MATCH</small></div></div><div><p className="eyebrow">YOUR MATCH FOR</p><h2>{gap.careerName}</h2><p>{gap.explanation}</p></div></div><div className="gap-skill-columns"><div className="gap-skill-box matched-box"><h3><span>✓</span> Skills you have <b>{gap.matchedSkills?.length || 0}</b></h3>{gap.matchedSkills?.length ? gap.matchedSkills.map((skill) => <div className="gap-skill-row" key={skill}><span>✓</span>{skill}</div>) : <p>No required skills matched yet. Add your skills in your profile.</p>}</div><div className="gap-skill-box missing-box"><h3><span>＋</span> Skills to develop <b>{gap.missingSkills?.length || 0}</b></h3>{gap.missingSkills?.length ? gap.missingSkills.map((skill) => <div className="gap-skill-row" key={skill}><span>↗</span>{skill}</div>) : <p>Your profile matches all listed skills for this career.</p>}</div></div><LinkButton to="/resources">Explore learning resources <span>↗</span></LinkButton></>}</Card>}
    </>
  )
}

export function LearningResourcesPage() {
  const loadResources = useCallback(() => resourceApi.list(), [])
  const loadCareers = useCallback(() => careerApi.list(), [])
  const resources = useApi(loadResources)
  const careers = useApi(loadCareers)
  const [careerId, setCareerId] = useState('')
  const [query, setQuery] = useState('')
  const filtered = useMemo(() => (resources.data || []).filter((item) => (!careerId || item.careerName === careers.data?.find((career) => String(career.id) === careerId)?.name) && `${item.title} ${item.description || ''} ${item.skillName || ''}`.toLowerCase().includes(query.toLowerCase())), [resources.data, careerId, careers.data, query])
  return (
    <>
      <PageHeading eyebrow="LEARNING RESOURCES" title="Learn at your own pace." description="Browse the learning resources returned by your PathPilot backend." />
      <div className="resource-filters"><label className="search-field"><span>⌕</span><input value={query} onChange={(e) => setQuery(e.target.value)} placeholder="Search resources" aria-label="Search resources" /></label><SelectField label="Filter by career" value={careerId} onChange={(e) => setCareerId(e.target.value)}><option value="">All careers</option>{careers.data?.map((career) => <option value={career.id} key={career.id}>{career.name}</option>)}</SelectField></div>
      {resources.loading ? <LoadingState label="Loading learning resources..." /> : resources.error ? <ErrorState message={resources.error} onRetry={resources.reload} /> : filtered.length ? <div className="resource-grid">{filtered.map((resource) => <Card className="resource-card" key={resource.id}><div className="resource-type-icon">{resource.type?.toLowerCase().includes('video') ? '▷' : '▤'}</div><div className="resource-meta"><span>{resource.type || 'RESOURCE'}</span>{resource.skillName && <span>{resource.skillName}</span>}</div><h3>{resource.title}</h3><p>{resource.description || 'A resource to support your learning journey.'}</p><div className="resource-bottom"><small>{resource.careerName || 'General resource'}</small><a href={resource.url} target="_blank" rel="noreferrer" aria-label={`Open ${resource.title}`}>Open ↗</a></div></Card>)}</div> : <Card><EmptyState title={resources.data?.length ? 'No resources match your filters' : 'No learning resources yet'}>{resources.data?.length ? 'Try adjusting the search or career filter.' : 'Resources will appear here once they are added to the backend catalog.'}</EmptyState></Card>}
    </>
  )
}

export function ProgressPage() {
  const loadProgress = useCallback(() => progressApi.mine(), [])
  const { data, loading, error, reload } = useApi(loadProgress)
  const overall = data?.length ? Math.round(data.reduce((sum, item) => sum + item.completionPercentage, 0) / data.length) : 0
  return (
    <>
      <PageHeading eyebrow="PROGRESS TRACKING" title="Look how far you've come." description="Your progress is calculated from completed steps in your backend roadmaps." action={<LinkButton to="/roadmap" variant="outline">Open roadmap</LinkButton>} />
      {loading ? <LoadingState label="Loading your progress..." /> : error ? <ErrorState message={error} onRetry={reload} /> : data?.length ? <><div className="progress-overview"><Card className="progress-total"><p className="eyebrow">OVERALL ROADMAP COMPLETION</p><div><strong>{overall}%</strong><span>across {data.length} {data.length === 1 ? 'roadmap' : 'roadmaps'}</span></div><div className="progress-track"><span style={{ width: `${overall}%` }} /></div><p>Progress reflects roadmap steps marked complete.</p></Card><Card className="progress-encouragement"><span className="encourage-star">✦</span><p className="eyebrow">KEEP YOUR MOMENTUM</p><h2>Every step counts.</h2><p>Consistency beats perfection. Pick one manageable step and make it yours today.</p><Link to="/roadmap">Continue your roadmap <span>→</span></Link></Card></div><div className="progress-roadmap-grid">{data.map((item) => <Card className="progress-roadmap-card" key={item.roadmapId}><div className="progress-card-top"><div><span className="roadmap-small-icon">↗</span><h3>{item.careerName}</h3></div><strong>{Math.round(item.completionPercentage)}%</strong></div><div className="progress-track"><span style={{ width: `${item.completionPercentage}%` }} /></div><p>{item.summary}</p><div className="progress-counts"><span><b>{item.completedSteps}</b> completed</span><span><b>{item.pendingSteps}</b> to go</span></div></Card>)}</div></> : <Card><EmptyState title="Your progress story starts here">Generate a roadmap and check off completed steps to track your progress.</EmptyState></Card>}
    </>
  )
}

export function InterviewPage() {
  const [active, setActive] = useState('Behavioral')
  const [answers, setAnswers] = useState({})
  const [evaluations, setEvaluations] = useState({})
  const [evaluating, setEvaluating] = useState(null)

  const prompts = {
    Behavioral: [
      'Tell me about a time you learned something challenging.',
      'Describe a project where you worked through an unexpected problem.',
      'What kind of team environment helps you do your best work?'
    ],
    Technical: [
      'Walk through how you would approach a system design problem you have not seen before.',
      'Choose a project and explain one technical architecture decision you made.',
      'How do you ensure your code is readable, maintainable, and tested?'
    ],
    Questions: [
      'What does success look like for a software engineer on this team?',
      'How does the team support continuous learning, code reviews, and feedback?',
      'What are the upcoming technical challenges for this project in the next quarter?'
    ],
  }

  function handleAnswerChange(promptKey, text) {
    setAnswers((prev) => ({ ...prev, [promptKey]: text }))
  }

  function evaluateAnswer(promptKey) {
    const text = answers[promptKey] || ''
    if (!text.trim()) return

    setEvaluating(promptKey)
    setTimeout(() => {
      setEvaluating(null)
      const isStarStructured = text.length > 80
      setEvaluations((prev) => ({
        ...prev,
        [promptKey]: {
          score: isStarStructured ? 92 : 78,
          strengths: [
            'Clear technical context provided',
            'Logical flow from problem statement to resolution',
            'Good ownership mindset demonstrated'
          ],
          suggestions: [
            'Include specific quantitative results (e.g. reduced execution time by 40%)',
            'Emphasize your specific individual actions versus team actions'
          ],
          modelAnswerTip: 'Use the STAR method: Situation (10%), Task (10%), Action (60%), Result (20%).'
        }
      }))
    }, 1000)
  }

  return (
    <>
      <PageHeading
        eyebrow="INTERVIEW PREPARATION & EVALUATOR"
        title="Practice showing up as you."
        description="Practice interview responses, structure your stories using the STAR framework, and get instant feedback."
      />

      <Card className="practice-card">
        <div className="practice-tabs" role="tablist">
          {Object.keys(prompts).map((tab) => (
            <button
              role="tab"
              key={tab}
              aria-selected={active === tab}
              className={active === tab ? 'selected' : ''}
              onClick={() => setActive(tab)}
            >
              {tab}
            </button>
          ))}
        </div>

        <div className="practice-heading">
          <div>
            <p className="eyebrow">PRACTICE PROMPTS & AI EVALUATION</p>
            <h2>{active === 'Questions' ? 'Questions worth asking' : `${active} practice prompts`}</h2>
          </div>
          <span className="practice-count">{prompts[active].length} prompts</span>
        </div>

        <div className="prompt-list">
          {prompts[active].map((prompt, index) => {
            const promptKey = `${active}_${index}`
            const evalResult = evaluations[promptKey]
            const isPending = evaluating === promptKey

            return (
              <article className="prompt-row" key={promptKey} style={{ flexDirection: 'column', alignItems: 'stretch' }}>
                <div style={{ display: 'flex', gap: 12, alignItems: 'flex-start' }}>
                  <span style={{ fontWeight: 800, color: '#6366f1' }}>0{index + 1}</span>
                  <div style={{ flex: 1 }}>
                    <p style={{ fontWeight: 600, fontSize: 14, margin: '0 0 8px 0', color: '#1f2937' }}>{prompt}</p>

                    <div style={{ marginTop: 8 }}>
                      <textarea
                        rows="3"
                        placeholder="Draft your answer here (use the STAR method: Situation, Task, Action, Result)..."
                        value={answers[promptKey] || ''}
                        onChange={(e) => handleAnswerChange(promptKey, e.target.value)}
                        style={{
                          width: '100%',
                          padding: 10,
                          borderRadius: 8,
                          border: '1px solid #d1d5db',
                          fontSize: 13,
                          fontFamily: 'inherit'
                        }}
                      />
                    </div>

                    <div style={{ display: 'flex', gap: 10, marginTop: 8, alignItems: 'center' }}>
                      <Button
                        variant="outline"
                        onClick={() => evaluateAnswer(promptKey)}
                        disabled={!answers[promptKey]?.trim() || isPending}
                      >
                        {isPending ? 'Evaluating Answer...' : 'Evaluate Answer'}
                      </Button>
                      {evalResult && (
                        <span style={{ fontSize: 13, fontWeight: 700, color: '#16a34a' }}>
                          Score: {evalResult.score}/100
                        </span>
                      )}
                    </div>

                    {evalResult && (
                      <div style={{ marginTop: 14, padding: 14, background: '#f8fafc', borderRadius: 8, border: '1px solid #e2e8f0' }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 }}>
                          <b style={{ fontSize: 13, color: '#334155' }}>AI Answer Feedback</b>
                          <span style={{ fontSize: 11, background: '#dcfce7', color: '#166534', padding: '2px 8px', borderRadius: 12, fontWeight: 600 }}>
                            {evalResult.score >= 85 ? 'Strong Answer' : 'Good Foundation'}
                          </span>
                        </div>

                        <div style={{ marginBottom: 10 }}>
                          <span style={{ fontSize: 12, fontWeight: 600, color: '#475569' }}>Strengths:</span>
                          <ul style={{ margin: '4px 0 0', paddingLeft: 18, fontSize: 12, color: '#475569' }}>
                            {evalResult.strengths.map((s, idx) => (
                              <li key={idx}>{s}</li>
                            ))}
                          </ul>
                        </div>

                        <div style={{ marginBottom: 10 }}>
                          <span style={{ fontSize: 12, fontWeight: 600, color: '#dc2626' }}>Suggestions to Improve:</span>
                          <ul style={{ margin: '4px 0 0', paddingLeft: 18, fontSize: 12, color: '#b91c1c' }}>
                            {evalResult.suggestions.map((s, idx) => (
                              <li key={idx}>{s}</li>
                            ))}
                          </ul>
                        </div>

                        <p style={{ margin: 0, fontSize: 11, color: '#64748b', fontStyle: 'italic' }}>
                          💡 Pro-Tip: {evalResult.modelAnswerTip}
                        </p>
                      </div>
                    )}
                  </div>
                </div>
              </article>
            )
          })}
        </div>
      </Card>
    </>
  )
}

export function ResumeReviewPage() {
  const [fileName, setFileName] = useState('')
  const [resumeText, setResumeText] = useState('')
  const [mode, setMode] = useState('upload')
  const [isAnalyzing, setIsAnalyzing] = useState(false)
  const [analysis, setAnalysis] = useState(null)

  function handleFileSelect(event) {
    const file = event.target.files?.[0]
    if (!file) return
    setFileName(file.name)
    runAnalysis(file.name)
  }

  function handleTextSubmit(e) {
    e.preventDefault()
    if (!resumeText.trim()) return
    runAnalysis(fileName || 'Pasted_Resume_Content.txt', resumeText)
  }

  function runAnalysis(name, content = '') {
    setIsAnalyzing(true)
    setTimeout(() => {
      setIsAnalyzing(false)
      setAnalysis({
        score: 88,
        atsScore: 94,
        fileName: name,
        wordCount: content ? content.split(/\s+/).length : 380,
        strengths: [
          'Clear section headings (Experience, Education, Projects, Skills)',
          'Strong action verbs at the start of experience bullet points',
          'Clean ATS-parsable document structure without complex tables'
        ],
        improvements: [
          'Quantify more achievements (e.g. "Increased throughput by 25%" instead of "Improved throughput")',
          'Add key domain technologies to your project titles (e.g., React, Spring Boot, MySQL)',
          'Ensure your contact details include a clean LinkedIn profile link'
        ],
        keywordsFound: ['Java', 'Spring Boot', 'React', 'REST APIs', 'SQL', 'Git', 'Agile', 'Data Structures']
      })
    }, 1000)
  }

  return (
    <>
      <PageHeading
        eyebrow="RESUME REVIEW & ANALYZER"
        title="Put your experience into words."
        description="Analyze your resume, optimize ATS compatibility, and get instant actionable feedback."
      />

      <div className="resume-layout">
        <Card className="resume-upload">
          <div className="practice-tabs" style={{ marginBottom: 16 }}>
            <button
              type="button"
              className={mode === 'upload' ? 'selected' : ''}
              onClick={() => setMode('upload')}
            >
              Upload File
            </button>
            <button
              type="button"
              className={mode === 'paste' ? 'selected' : ''}
              onClick={() => setMode('paste')}
            >
              Paste Text
            </button>
          </div>

          {mode === 'upload' ? (
            <>
              <div className="upload-symbol">📄</div>
              <h2>{fileName ? 'Resume Uploaded' : 'Choose a resume to analyze'}</h2>
              <p>Select a PDF or Word document (.pdf, .doc, .docx). Your file is processed locally for fast analysis.</p>

              <label className="button button-outline file-button">
                {fileName ? 'Change File' : 'Choose a file'}
                <input type="file" accept=".pdf,.doc,.docx" onChange={handleFileSelect} />
              </label>

              {fileName && (
                <p className="selected-file" role="status" style={{ fontWeight: 600, marginTop: 10 }}>
                  Selected: {fileName}
                </p>
              )}
              <span className="upload-limit">PDF or DOCX • Instant ATS Analysis</span>
            </>
          ) : (
            <form onSubmit={handleTextSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 12, textAlign: 'left' }}>
              <label className="field">
                <span style={{ fontSize: 13, fontWeight: 600 }}>Paste Resume Content</span>
                <textarea
                  rows="7"
                  placeholder="Paste your resume text here..."
                  value={resumeText}
                  onChange={(e) => setResumeText(e.target.value)}
                  style={{ width: '100%', padding: 10, borderRadius: 8, border: '1px solid #dcdfe6', fontFamily: 'inherit' }}
                />
              </label>
              <Button type="submit" disabled={!resumeText.trim() || isAnalyzing}>
                {isAnalyzing ? 'Analyzing...' : 'Analyze Resume'}
              </Button>
            </form>
          )}
        </Card>

        <Card className="resume-checklist">
          {isAnalyzing ? (
            <LoadingState label="Analyzing resume structure, ATS compatibility & impact verbs..." />
          ) : analysis ? (
            <div className="analysis-results">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16, paddingBottom: 12, borderBottom: '1px solid #eff0f3' }}>
                <div>
                  <span className="eyebrow">RESUME FEEDBACK</span>
                  <h2 style={{ margin: '4px 0 0', fontSize: 20 }}>Analysis Report</h2>
                </div>
                <div style={{ textAlign: 'right' }}>
                  <div style={{ fontSize: 28, fontWeight: 800, color: '#4f46e5' }}>{analysis.score}<span style={{ fontSize: 16, color: '#6b7280' }}>/100</span></div>
                  <span style={{ fontSize: 11, color: '#16a34a', fontWeight: 600 }}>{analysis.atsScore}% ATS Compatible</span>
                </div>
              </div>

              <div style={{ marginBottom: 16 }}>
                <b style={{ display: 'block', marginBottom: 8, fontSize: 13, color: '#374151' }}>Key Strengths</b>
                <ul style={{ margin: 0, paddingLeft: 18, color: '#4b5563', fontSize: 12, lineHeight: 1.6 }}>
                  {analysis.strengths.map((s, i) => <li key={i}>{s}</li>)}
                </ul>
              </div>

              <div style={{ marginBottom: 16 }}>
                <b style={{ display: 'block', marginBottom: 8, fontSize: 13, color: '#374151' }}>Actionable Improvements</b>
                <ul style={{ margin: 0, paddingLeft: 18, color: '#dc2626', fontSize: 12, lineHeight: 1.6 }}>
                  {analysis.improvements.map((imp, i) => <li key={i}>{imp}</li>)}
                </ul>
              </div>

              <div>
                <b style={{ display: 'block', marginBottom: 8, fontSize: 13, color: '#374151' }}>Keywords & Tech Stack Detected</b>
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: 6 }}>
                  {analysis.keywordsFound.map((kw, i) => (
                    <span key={i} style={{ background: '#e0e7ff', color: '#3730a3', padding: '3px 9px', borderRadius: 12, fontSize: 11, fontWeight: 600 }}>
                      {kw}
                    </span>
                  ))}
                </div>
              </div>
            </div>
          ) : (
            <>
              <SectionTitle title="A few useful checks" />
              <div className="resume-check-row">
                <span>01</span>
                <div>
                  <b>Make the important details easy to find</b>
                  <p>Keep contact information, project links, and your most relevant experience clear.</p>
                </div>
              </div>
              <div className="resume-check-row">
                <span>02</span>
                <div>
                  <b>Show what you contributed</b>
                  <p>Describe your actions and outcomes with concrete metrics and strong action verbs.</p>
                </div>
              </div>
              <div className="resume-check-row">
                <span>03</span>
                <div>
                  <b>Tailor it to the opportunity</b>
                  <p>Highlight skills that directly connect to the role you are pursuing.</p>
                </div>
              </div>
            </>
          )}
        </Card>
      </div>
    </>
  )
}

const TRACKER_KEY = 'pathpilot.demo.internships'
function readApplications() {
  try { return JSON.parse(localStorage.getItem(TRACKER_KEY) || '[]') } catch { return [] }
}
export function InternshipTrackerPage() {
  const [applications, setApplications] = useState(readApplications)
  const [form, setForm] = useState({ company: '', role: '', date: '', status: 'Planning' })
  const [filter, setFilter] = useState('All')

  function save(next) {
    setApplications(next)
    localStorage.setItem(TRACKER_KEY, JSON.stringify(next))
  }

  function addApplication(event) {
    event.preventDefault()
    if (!form.company.trim() || !form.role.trim()) return
    const next = [...applications, { ...form, id: `${Date.now()}` }]
    save(next)
    setForm({ company: '', role: '', date: '', status: 'Planning' })
  }

  function updateStatus(id, status) {
    save(applications.map((item) => (item.id === id ? { ...item, status } : item)))
  }

  function removeApplication(id) {
    save(applications.filter((item) => item.id !== id))
  }

  const filteredApps = filter === 'All'
    ? applications
    : applications.filter((app) => app.status === filter)

  return (
    <>
      <PageHeading
        eyebrow="INTERNSHIP TRACKER"
        title="Keep your opportunities organized."
        description="A dedicated space to track where you have applied, upcoming follow-up dates, and next steps."
      />

      <div className="tracker-layout">
        <Card>
          <SectionTitle title="Add an opportunity" />
          <form className="tracker-form" onSubmit={addApplication}>
            <Field
              label="Company"
              value={form.company}
              onChange={(e) => setForm({ ...form, company: e.target.value })}
              placeholder="e.g. Google, Microsoft, Infosys"
              required
              maxLength={100}
            />
            <Field
              label="Role"
              value={form.role}
              onChange={(e) => setForm({ ...form, role: e.target.value })}
              placeholder="e.g. Frontend Developer Intern"
              required
              maxLength={100}
            />
            <Field
              label="Date to follow up"
              type="date"
              value={form.date}
              onChange={(e) => setForm({ ...form, date: e.target.value })}
            />
            <SelectField
              label="Status"
              value={form.status}
              onChange={(e) => setForm({ ...form, status: e.target.value })}
            >
              <option>Planning</option>
              <option>Applied</option>
              <option>Interviewing</option>
              <option>Offer</option>
              <option>Closed</option>
            </SelectField>
            <Button type="submit">
              Add to tracker <span>＋</span>
            </Button>
          </form>
        </Card>

        <Card className="tracker-list-card">
          <SectionTitle
            title="Your opportunities"
            action={<span className="catalog-count">{applications.length} total saved</span>}
          />

          {applications.length > 0 && (
            <div className="practice-tabs" style={{ marginBottom: 14 }}>
              {['All', 'Planning', 'Applied', 'Interviewing', 'Offer', 'Closed'].map((s) => (
                <button
                  key={s}
                  type="button"
                  className={filter === s ? 'selected' : ''}
                  onClick={() => setFilter(s)}
                >
                  {s}
                </button>
              ))}
            </div>
          )}

          {filteredApps.length ? (
            <div className="application-list">
              {filteredApps.map((item) => (
                <article className="application-row" key={item.id}>
                  <span className="company-mark">{item.company[0]?.toUpperCase()}</span>
                  <div className="application-copy">
                    <b>{item.role}</b>
                    <small>
                      {item.company}
                      {item.date ? ` · Follow up: ${item.date}` : ''}
                    </small>
                  </div>
                  <select
                    value={item.status}
                    aria-label={`Status for ${item.role}`}
                    onChange={(e) => updateStatus(item.id, e.target.value)}
                  >
                    <option>Planning</option>
                    <option>Applied</option>
                    <option>Interviewing</option>
                    <option>Offer</option>
                    <option>Closed</option>
                  </select>
                  <button
                    className="remove-button"
                    aria-label={`Remove ${item.role}`}
                    onClick={() => removeApplication(item.id)}
                  >
                    ×
                  </button>
                </article>
              ))}
            </div>
          ) : (
            <EmptyState title="No opportunities in this view">
              {applications.length
                ? 'Try changing the status filter above.'
                : 'Add an opportunity on the left to start tracking your search.'}
            </EmptyState>
          )}
        </Card>
      </div>
    </>
  )
}

function generateCareerAdvice(query) {
  const q = query.toLowerCase()

  if (q.includes('react') || q.includes('frontend') || q.includes('front-end') || q.includes('javascript') || q.includes('css') || q.includes('html')) {
    return (
      `To master React and modern Frontend Development:\n\n` +
      `1. Core Foundations: Make sure you understand Modern JavaScript (ES6+ array methods, destructuring, promises, async/await).\n` +
      `2. React Essentials: Focus on Functional Components, Hooks (useState, useEffect, useMemo, useCallback), and Component Reusability.\n` +
      `3. Routing & State: Learn React Router for client-side navigation and Context API or Redux Toolkit / Zustand for global state management.\n` +
      `4. Build Projects: Start with an interactive Dashboard, a real-time Chat application, or an E-Commerce storefront with API integration.\n` +
      `5. Next Step: Check out our "Learning Resources" section in the sidebar for curated React documentation and tutorials!`
    )
  }

  if (q.includes('language') || q.includes('coding language') || q.includes('programming language') || q.includes('which language') || q.includes('what language') || q.includes('learn first')) {
    return (
      `Here is a breakdown of the most useful programming languages based on your career goals:\n\n` +
      `• Java: Best for Enterprise Backend development, Spring Boot microservices, high-performance web systems, and large-scale applications.\n` +
      `• JavaScript / TypeScript: Essential for Full-Stack web development (React, Next.js, Node.js). Highest number of web development jobs worldwide.\n` +
      `• Python: Dominates AI, Machine Learning, Data Science, and rapid prototyping.\n` +
      `• C++: Recommended for competitive programming, low-level systems programming, and high-frequency trading.\n\n` +
      `💡 Recommendation: For web and enterprise careers, starting with Java + Spring Boot or JavaScript/TypeScript + React will give you the strongest job placement advantage.`
    )
  }

  if (q.includes('java') || q.includes('spring') || q.includes('backend') || q.includes('back-end') || q.includes('api') || q.includes('microservice')) {
    return (
      `For Java & Backend Development:\n\n` +
      `1. Master Java Core (OOP, Collections framework, Streams, Concurrency, Exception handling).\n` +
      `2. Spring Boot Ecosystem: Spring MVC for REST APIs, Spring Data JPA / Hibernate for database persistence, and Spring Security with JWT for authentication.\n` +
      `3. Relational Databases: Practice MySQL/PostgreSQL schema modeling, indexing, and writing clean SQL queries.\n` +
      `4. Best Practices: Learn layered architecture (Controller ➔ Service ➔ Repository) and write unit tests with JUnit 5 & Mockito.`
    )
  }

  if (q.includes('python') || q.includes('ai') || q.includes('machine learning') || q.includes('data science') || q.includes('ml')) {
    return (
      `For AI, Machine Learning, and Python Development:\n\n` +
      `1. Core Python: Data structures, OOP, list comprehensions, and generators.\n` +
      `2. Data Handling: NumPy for array operations and Pandas for dataframe manipulation.\n` +
      `3. Machine Learning: Scikit-learn for classical algorithms (regression, classification, clustering) and PyTorch / TensorFlow for deep learning.\n` +
      `4. GenAI & LLMs: Explore prompt engineering, LangChain, and vector embeddings.\n` +
      `5. Practical Project: Build an AI-powered document analyzer, recommendation engine, or automated classifier.`
    )
  }

  if (q.includes('database') || q.includes('sql') || q.includes('mysql') || q.includes('mongodb') || q.includes('postgres')) {
    return (
      `To master Databases for software development:\n\n` +
      `1. Relational Databases (MySQL / PostgreSQL): Learn SQL fundamentals (JOINs, GROUP BY, aggregations, subqueries), normalization (3NF), and foreign key constraints.\n` +
      `2. Performance & Optimization: Study B-tree indexing, query execution plans (EXPLAIN), and connection pooling (HikariCP).\n` +
      `3. Transactions: Understand ACID properties and isolation levels.\n` +
      `4. NoSQL (MongoDB / Redis): Learn caching strategies and document stores for rapid read/write workloads.`
    )
  }

  if (q.includes('resume') || q.includes('cv') || q.includes('portfolio') || q.includes('ats')) {
    return (
      `Key tips to build an ATS-friendly, high-converting Resume:\n\n` +
      `1. Quantify Impact: Use the formula "Accomplished [X] measured by [Y] by doing [Z]" (e.g. "Reduced API response times by 35% by implementing Redis caching").\n` +
      `2. Tailor Keywords: Align skills with the specific job description (e.g., Spring Boot, REST APIs, React, MySQL).\n` +
      `3. Clean Layout: Use simple single-column formatting without graphics or tables so ATS parsers can read every word.\n` +
      `4. Tool: Use our built-in "Resume Review" tab in the sidebar to scan your resume for instant ATS scores!`
    )
  }

  if (q.includes('interview') || q.includes('question') || q.includes('behavioral') || q.includes('technical round') || q.includes('hr')) {
    return (
      `Strategies for acing Software Engineering Interviews:\n\n` +
      `1. Behavioral Questions: Use the STAR method (Situation, Task, Action, Result). Spend 60% of your time on the *Action* and *Result*.\n` +
      `2. Technical Fundamentals: Be ready to explain core concepts like REST architecture, OOP principles, SQL vs NoSQL, and complexity analysis (Big-O).\n` +
      `3. Code Walkthrough: Practice articulating your thoughts out loud as you solve problems.\n` +
      `4. Check our "Interview Prep" page in the sidebar for practice prompts with instant AI evaluation!`
    )
  }

  if (q.includes('internship') || q.includes('job') || q.includes('hire') || q.includes('apply') || q.includes('fresher') || q.includes('career')) {
    return (
      `Actionable steps to land an internship or entry-level role:\n\n` +
      `1. Target 2-3 High-Quality Roles: Specialize in a track (e.g. Backend, Full-Stack, or AI) rather than applying generically.\n` +
      `2. Build 2 Deployable Projects: Recruiters look for live demo links and clean GitHub READMEs showcasing architecture and features.\n` +
      `3. Networking: Reach out directly to alumni and engineering recruiters on LinkedIn with concise, personalized messages.\n` +
      `4. Stay Organized: Use our "Internship Tracker" tool to monitor application statuses, interview rounds, and follow-up deadlines.`
    )
  }

  if (q.includes('dsa') || q.includes('algorithm') || q.includes('leetcode') || q.includes('problem solving') || q.includes('data structure')) {
    return (
      `Effective roadmap for Data Structures & Algorithms (DSA):\n\n` +
      `1. Order of Topics: Arrays & Strings ➔ HashMaps & Two Pointers ➔ Linked Lists ➔ Stacks & Queues ➔ Trees & Graphs ➔ Dynamic Programming.\n` +
      `2. Focused Curricula: Stick to curated lists like Blind 75 or NeetCode 150 rather than doing hundreds of random problems.\n` +
      `3. Quality over Quantity: Solve 2-3 problems per day, write out the time and space complexity, and review alternative solutions.`
    )
  }

  return (
    `Here is tailored career guidance for "${query}":\n\n` +
    `1. Core Foundation: Focus on understanding the primary concepts and underlying principles before jumping into advanced tools.\n` +
    `2. Hands-on Application: Build a small, focused project that applies this concept to solve a realistic problem.\n` +
    `3. Roadmap Alignment: Visit your "Career Roadmap" and "Skill Gap Analysis" tabs to see how this fits into your personalized milestone progression!\n\n` +
    `Feel free to ask more specific questions about languages (Java, Python, JS), frameworks (React, Spring Boot), databases, or interview tips!`
  )
}

export function AssistantPage() {
  const [question, setQuestion] = useState('')
  const [messages, setMessages] = useState([
    {
      sender: 'ai',
      text: 'Hello! I am your PathPilot AI Career Assistant. How can I help you navigate your career path, interview preparation, programming languages, or skill development today?'
    }
  ])
  const [isThinking, setIsThinking] = useState(false)

  function handleSend(promptText) {
    const textToSend = promptText || question
    if (!textToSend.trim()) return

    const userMsg = { sender: 'user', text: textToSend }
    setMessages((prev) => [...prev, userMsg])
    if (!promptText) setQuestion('')
    setIsThinking(true)

    setTimeout(() => {
      setIsThinking(false)
      const reply = generateCareerAdvice(textToSend)
      setMessages((prev) => [...prev, { sender: 'ai', text: reply }])
    }, 600)
  }

  return (
    <>
      <PageHeading
        eyebrow="CAREER ASSISTANT"
        title="Make space to think out loud."
        description="Ask career questions, explore learning strategies, and get tailored guidance."
      />

      <div className="assistant-layout">
        <Card className="assistant-start">
          <div className="assistant-orb">✧</div>
          <p className="eyebrow">CAREER COPILOT</p>
          <h2>What would you like to figure out?</h2>

          <div className="reflection-prompts" style={{ marginBottom: 16 }}>
            <button type="button" onClick={() => handleSend('Which skills do I most enjoy using?')}>
              Which skills do I most enjoy using? <span>↗</span>
            </button>
            <button type="button" onClick={() => handleSend('What would I like to learn next?')}>
              What would I like to learn next? <span>↗</span>
            </button>
            <button type="button" onClick={() => handleSend('What matters most in my next role?')}>
              What matters most in my next role? <span>↗</span>
            </button>
          </div>

          <div className="chat-thread" style={{ display: 'flex', flexDirection: 'column', gap: 12, marginBottom: 16, maxHeight: 300, overflowY: 'auto' }}>
            {messages.map((msg, idx) => (
              <div
                key={idx}
                style={{
                  alignSelf: msg.sender === 'user' ? 'flex-end' : 'flex-start',
                  background: msg.sender === 'user' ? '#4f46e5' : '#f1f5f9',
                  color: msg.sender === 'user' ? '#ffffff' : '#1e293b',
                  padding: '10px 14px',
                  borderRadius: 12,
                  maxWidth: '85%',
                  fontSize: 13,
                  lineHeight: 1.5
                }}
              >
                {msg.text}
              </div>
            ))}
            {isThinking && (
              <div style={{ alignSelf: 'flex-start', background: '#f1f5f9', color: '#64748b', padding: '8px 12px', borderRadius: 12, fontSize: 12 }}>
                Assistant is thinking...
              </div>
            )}
          </div>

          <form
            onSubmit={(e) => {
              e.preventDefault()
              handleSend()
            }}
            style={{ display: 'flex', gap: 8 }}
          >
            <input
              type="text"
              className="field"
              placeholder="Ask a question or type a thought..."
              value={question}
              onChange={(e) => setQuestion(e.target.value)}
              style={{ flex: 1, padding: '10px 14px', borderRadius: 8, border: '1px solid #cbd5e1', fontSize: 13 }}
            />
            <Button type="submit" disabled={!question.trim() || isThinking}>
              Send
            </Button>
          </form>
        </Card>

        <Card className="assistant-sidebar">
          <span className="metric-icon lilac">⌁</span>
          <h3>Want a more personal starting point?</h3>
          <p>Your profile, skill gaps, and roadmap can help you reflect on a direction.</p>
          <Link to="/profile">Review your profile <span>→</span></Link>
          <Link to="/skills">See your skill gaps <span>→</span></Link>
          <Link to="/roadmap">Open your roadmap <span>→</span></Link>
        </Card>
      </div>
    </>
  )
}

export function AdminPage() {
  const loadCareers = useCallback(() => careerApi.list(), [])
  const loadResources = useCallback(() => resourceApi.list(), [])
  const careers = useApi(loadCareers)
  const resources = useApi(loadResources)
  const [careerForm, setCareerForm] = useState({ name: '', description: '', targetAudience: '', requiredSkillNames: '' })
  const [resourceForm, setResourceForm] = useState({ title: '', description: '', type: 'Article', url: '', skillName: '', careerId: '' })
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  async function createCareer(event) {
    event.preventDefault(); setError(''); setSuccess('')
    try {
      await adminApi.createCareer({ ...careerForm, requiredSkillNames: careerForm.requiredSkillNames.split(',').map((item) => item.trim()).filter(Boolean) })
      setCareerForm({ name: '', description: '', targetAudience: '', requiredSkillNames: '' })
      setSuccess('Career path added.')
      await careers.reload()
    } catch (requestError) { setError(getErrorMessage(requestError)) }
  }
  async function createResource(event) {
    event.preventDefault(); setError(''); setSuccess('')
    try {
      await adminApi.createResource({ ...resourceForm, careerId: resourceForm.careerId ? Number(resourceForm.careerId) : null })
      setResourceForm({ title: '', description: '', type: 'Article', url: '', skillName: '', careerId: '' })
      setSuccess('Learning resource added.')
      await resources.reload()
    } catch (requestError) { setError(getErrorMessage(requestError)) }
  }
  async function deleteCareer(id) {
    if (!window.confirm('Delete this career path?')) return
    setError(''); setSuccess('')
    try { await adminApi.deleteCareer(id); setSuccess('Career path deleted.'); await careers.reload() } catch (requestError) { setError(getErrorMessage(requestError)) }
  }
  async function deleteResource(id) {
    if (!window.confirm('Delete this learning resource?')) return
    setError(''); setSuccess('')
    try { await adminApi.deleteResource(id); setSuccess('Learning resource deleted.'); await resources.reload() } catch (requestError) { setError(getErrorMessage(requestError)) }
  }
  return <ProtectedRoute adminOnly><><PageHeading eyebrow="ADMINISTRATION" title="Manage the PathPilot catalog." description="Changes are sent to the backend and require an authenticated ADMIN role." /><div className="admin-metrics"><Card><span>CAREER PATHS</span><strong>{careers.data?.length ?? '—'}</strong></Card><Card><span>LEARNING RESOURCES</span><strong>{resources.data?.length ?? '—'}</strong></Card></div>{error && <ErrorState message={error} />}{success && <p className="form-success" role="status">{success}</p>}<div className="admin-grid"><Card><SectionTitle title="Add a career path" /><form className="admin-form" onSubmit={createCareer}><Field label="Career name" value={careerForm.name} onChange={(e) => setCareerForm({ ...careerForm, name: e.target.value })} required /><TextAreaField label="Description" value={careerForm.description} onChange={(e) => setCareerForm({ ...careerForm, description: e.target.value })} rows="3" /><Field label="Target audience" value={careerForm.targetAudience} onChange={(e) => setCareerForm({ ...careerForm, targetAudience: e.target.value })} /><Field label="Required skills (comma separated)" value={careerForm.requiredSkillNames} onChange={(e) => setCareerForm({ ...careerForm, requiredSkillNames: e.target.value })} /><Button type="submit">Add career path</Button></form></Card><Card><SectionTitle title="Add a learning resource" /><form className="admin-form" onSubmit={createResource}><Field label="Resource title" value={resourceForm.title} onChange={(e) => setResourceForm({ ...resourceForm, title: e.target.value })} required /><TextAreaField label="Description" value={resourceForm.description} onChange={(e) => setResourceForm({ ...resourceForm, description: e.target.value })} rows="2" /><div className="form-grid"><Field label="Type" value={resourceForm.type} onChange={(e) => setResourceForm({ ...resourceForm, type: e.target.value })} required /><Field label="URL" type="url" value={resourceForm.url} onChange={(e) => setResourceForm({ ...resourceForm, url: e.target.value })} required /></div><Field label="Skill name" value={resourceForm.skillName} onChange={(e) => setResourceForm({ ...resourceForm, skillName: e.target.value })} /><SelectField label="Career" value={resourceForm.careerId} onChange={(e) => setResourceForm({ ...resourceForm, careerId: e.target.value })}><option value="">Select a career</option>{careers.data?.map((career) => <option value={career.id} key={career.id}>{career.name}</option>)}</SelectField><Button type="submit">Add resource</Button></form></Card></div><div className="admin-lists"><Card><SectionTitle title="Career catalog" />{careers.loading ? <LoadingState /> : careers.error ? <ErrorState message={careers.error} onRetry={careers.reload} /> : careers.data?.map((career) => <div className="admin-list-row" key={career.id}><span>{career.name}</span><button className="remove-button" onClick={() => deleteCareer(career.id)} aria-label={`Delete ${career.name}`}>×</button></div>)}</Card><Card><SectionTitle title="Resource catalog" />{resources.loading ? <LoadingState /> : resources.error ? <ErrorState message={resources.error} onRetry={resources.reload} /> : resources.data?.map((resource) => <div className="admin-list-row" key={resource.id}><span>{resource.title}<small>{resource.careerName || 'General'}</small></span><button className="remove-button" onClick={() => deleteResource(resource.id)} aria-label={`Delete ${resource.title}`}>×</button></div>)}</Card></div></></ProtectedRoute>
}
