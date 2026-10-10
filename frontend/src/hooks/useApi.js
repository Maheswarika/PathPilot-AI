import { useCallback, useEffect, useState } from 'react'
import { getErrorMessage } from '../utils/format.js'

export default function useApi(loader) {
  const [requestNumber, setRequestNumber] = useState(0)
  const [result, setResult] = useState({ loader: null, requestNumber: -1, data: null, error: '' })
  const loading = result.loader !== loader || result.requestNumber !== requestNumber

  useEffect(() => {
    let active = true
    Promise.resolve()
      .then(loader)
      .then((data) => {
        if (active) setResult({ loader, requestNumber, data, error: '' })
      })
      .catch((requestError) => {
        if (active) setResult({ loader, requestNumber, data: null, error: getErrorMessage(requestError) })
      })
    return () => { active = false }
  }, [loader, requestNumber])

  const reload = useCallback(() => setRequestNumber((current) => current + 1), [])
  const setData = useCallback((data) => {
    setResult((current) => ({ ...current, data: typeof data === 'function' ? data(current.data) : data }))
  }, [])

  return { data: result.data, loading, error: result.error, reload, setData }
}
