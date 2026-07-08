const BASE = '/api/pipeline'

async function handle(response) {
  if (!response.ok) {
    let detail = `HTTP ${response.status}`
    try {
      const body = await response.json()
      detail = body.detail || body.title || detail
    } catch {
      // resposta sem corpo JSON
    }
    throw new Error(detail)
  }
  return response.json()
}

export function getStats() {
  return fetch(`${BASE}/stats`).then(handle)
}

export function getRuns(limit = 20) {
  return fetch(`${BASE}/runs?limit=${limit}`).then(handle)
}

export function triggerRun() {
  return fetch(`${BASE}/run`, { method: 'POST' }).then(handle)
}
