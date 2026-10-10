export function getErrorMessage(error) {
  const payload = error?.response?.data
  const isHtmlResponse = typeof payload === 'string' && payload.trimStart().startsWith('<')
  const isEmptyResponse = payload == null || (typeof payload === 'string' && !payload.trim())
  if (typeof payload === 'string' && payload.trim() && !isHtmlResponse && payload.length < 500) return payload
  if (payload?.message || payload?.detail) return payload.message || payload.detail
  if (payload && typeof payload === 'object') {
    const fieldMessages = Object.values(payload).filter((value) => typeof value === 'string' && value.trim())
    if (fieldMessages.length) return fieldMessages.join(' ')
  }
  if (error?.response?.status >= 500) {
    if (isHtmlResponse || isEmptyResponse) return 'Cannot reach the backend. Verify Spring Boot is running and the frontend API proxy is configured correctly.'
    return `The backend returned an internal error (HTTP ${error.response.status}). Please check the backend logs and try again.`
  }
  if (error?.response) return `The request was rejected (HTTP ${error.response.status}). Check the details and try again.`
  if (error?.code === 'ECONNABORTED') return 'The backend request timed out. Check the server and try again.'
  if (!error?.response) return 'Cannot reach the backend. Verify Spring Boot is running and the frontend API URL is configured correctly.'
  return 'Something went wrong. Please try again.'
}

export function initials(name = '') {
  return name.trim().split(/\s+/).slice(0, 2).map((part) => part[0]).join('').toUpperCase() || 'P'
}
