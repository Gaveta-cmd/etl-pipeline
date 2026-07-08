import { useCallback, useEffect, useRef, useState } from 'react'
import { AnimatePresence, motion } from 'framer-motion'
import { AlertTriangle } from 'lucide-react'
import Header from './components/Header.jsx'
import StatsCards from './components/StatsCards.jsx'
import CategoryChart from './components/CategoryChart.jsx'
import ExecutionsTable from './components/ExecutionsTable.jsx'
import { getRuns, getStats, triggerRun } from './api/client.js'

const POLL_INTERVAL_MS = 2000

export default function App() {
  const [stats, setStats] = useState(null)
  const [runs, setRuns] = useState([])
  const [live, setLive] = useState(false)
  const [running, setRunning] = useState(false)
  const [error, setError] = useState(null)
  const runningRef = useRef(false)
  // Cache das ultimas respostas para so re-renderizar quando algo de fato mudar.
  const lastStatsRef = useRef('')
  const lastRunsRef = useRef('')

  const refresh = useCallback(async () => {
    try {
      const [nextStats, nextRuns] = await Promise.all([getStats(), getRuns(20)])

      const statsKey = JSON.stringify(nextStats)
      if (statsKey !== lastStatsRef.current) {
        lastStatsRef.current = statsKey
        setStats(nextStats)
      }
      const runsKey = JSON.stringify(nextRuns)
      if (runsKey !== lastRunsRef.current) {
        lastRunsRef.current = runsKey
        setRuns(nextRuns)
      }

      setLive(true)
      if (runningRef.current && !nextRuns.some((r) => r.status === 'STARTED' || r.status === 'STARTING')) {
        runningRef.current = false
        setRunning(false)
      }
    } catch {
      setLive(false)
    }
  }, [])

  useEffect(() => {
    refresh()
    const id = setInterval(refresh, POLL_INTERVAL_MS)
    return () => clearInterval(id)
  }, [refresh])

  const handleRun = async () => {
    setError(null)
    setRunning(true)
    runningRef.current = true
    try {
      await triggerRun()
      await refresh()
    } catch (err) {
      setError(err.message)
      setRunning(false)
      runningRef.current = false
    }
  }

  return (
    <div className="relative min-h-full overflow-hidden">
      <div className="aurora" />
      <div className="relative mx-auto max-w-6xl px-5 py-10">
        <Header onRun={handleRun} running={running} live={live} />

        <AnimatePresence>
          {error && (
            <motion.div
              initial={{ opacity: 0, height: 0 }}
              animate={{ opacity: 1, height: 'auto' }}
              exit={{ opacity: 0, height: 0 }}
              className="mb-6 flex items-center gap-2 rounded-xl border border-danger/30 bg-danger-soft px-4 py-3 text-sm text-danger"
            >
              <AlertTriangle className="h-4 w-4" />
              {error}
            </motion.div>
          )}
        </AnimatePresence>

        <main className="flex flex-col gap-5">
          <StatsCards stats={stats} />
          <div className="grid gap-5 lg:grid-cols-5">
            <div className="lg:col-span-2">
              <CategoryChart categorias={stats?.categorias} />
            </div>
            <div className="lg:col-span-3">
              <ExecutionsTable runs={runs} />
            </div>
          </div>
        </main>

        <footer className="mt-12 flex items-center justify-between border-t border-line pt-6 text-xs text-muted">
          <span>Spring Batch + MySQL relacional (escola / produto / entrega)</span>
          <span className="font-display">Davi Augusto</span>
        </footer>
      </div>
    </div>
  )
}
