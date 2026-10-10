import { Link } from 'react-router-dom'

export function PageHeading({ eyebrow, title, description, action }) {
  return (
    <div className="page-heading">
      <div>
        {eyebrow && <p className="eyebrow">{eyebrow}</p>}
        <h1>{title}</h1>
        {description && <p className="page-description">{description}</p>}
      </div>
      {action && <div className="heading-action">{action}</div>}
    </div>
  )
}

export function Button({ children, variant = 'primary', className = '', ...props }) {
  return <button className={`button button-${variant} ${className}`} {...props}>{children}</button>
}

export function Card({ children, className = '' }) {
  return <section className={`card ${className}`}>{children}</section>
}

export function LoadingState({ label = 'Loading your workspace...' }) {
  return <div className="state-panel" role="status"><span className="spinner" />{label}</div>
}

export function ErrorState({ message, onRetry }) {
  return (
    <div className="state-panel error-state" role="alert">
      <span>{message}</span>
      {onRetry && <Button variant="outline" onClick={onRetry}>Try again</Button>}
    </div>
  )
}

export function EmptyState({ title, children, action }) {
  return (
    <div className="empty-state">
      <span className="empty-icon" aria-hidden="true">✦</span>
      <h3>{title}</h3>
      {children && <p>{children}</p>}
      {action}
    </div>
  )
}

export function Field({ label, ...props }) {
  return <label className="field"><span>{label}</span><input {...props} /></label>
}

export function SelectField({ label, children, ...props }) {
  return <label className="field"><span>{label}</span><select {...props}>{children}</select></label>
}

export function TextAreaField({ label, ...props }) {
  return <label className="field"><span>{label}</span><textarea {...props} /></label>
}

export function DemoNotice({ children }) {
  return <div className="demo-notice"><span className="demo-dot" />DEMO ONLY <span>{children}</span></div>
}

export function LinkButton({ to, children, variant = 'primary' }) {
  return <Link className={`button button-${variant}`} to={to}>{children}</Link>
}

export function SectionTitle({ title, action }) {
  return <div className="section-title"><h2>{title}</h2>{action}</div>
}
