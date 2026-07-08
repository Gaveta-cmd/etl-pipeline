import { AnimatePresence, motion } from 'framer-motion'
import { formatDateTime, formatDuration, formatNumber, statusStyle } from '../utils/format.js'

function StatusBadge({ status }) {
  const { label, className } = statusStyle(status)
  return (
    <span className={`inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-medium ${className}`}>
      {label}
    </span>
  )
}

export default function ExecutionsTable({ runs }) {
  return (
    <section className="card h-full overflow-hidden">
      <div className="flex items-baseline justify-between border-b border-line px-6 py-5">
        <h2 className="font-display text-lg font-semibold text-ink">Execucoes recentes</h2>
        <span className="text-xs text-muted">{runs.length} run(s)</span>
      </div>
      <div className="overflow-x-auto">
        <table className="w-full min-w-[640px] text-sm">
          <thead>
            <tr className="text-left text-[11px] uppercase tracking-[0.1em] text-muted">
              <th className="px-6 py-3 font-medium">Run</th>
              <th className="px-6 py-3 font-medium">Status</th>
              <th className="px-6 py-3 font-medium">Inicio</th>
              <th className="px-6 py-3 text-right font-medium">Duracao</th>
              <th className="px-6 py-3 text-right font-medium">Lidos</th>
              <th className="px-6 py-3 text-right font-medium">Carregados</th>
              <th className="px-6 py-3 text-right font-medium">Descartados</th>
            </tr>
          </thead>
          <tbody>
            <AnimatePresence initial={false}>
              {runs.length === 0 ? (
                <tr>
                  <td colSpan={7} className="px-6 py-12 text-center text-muted">
                    Nenhuma execucao registrada. Dispare o pipeline para comecar.
                  </td>
                </tr>
              ) : (
                runs.map((run) => (
                  <motion.tr
                    key={run.runId}
                    initial={{ opacity: 0, y: -6 }}
                    animate={{ opacity: 1, y: 0 }}
                    exit={{ opacity: 0 }}
                    className="border-t border-line/70 transition-colors hover:bg-paper-deep/50"
                  >
                    <td className="px-6 py-3.5 font-display font-semibold text-brand">#{run.runId}</td>
                    <td className="px-6 py-3.5"><StatusBadge status={run.status} /></td>
                    <td className="px-6 py-3.5 text-muted">{formatDateTime(run.startTime)}</td>
                    <td className="px-6 py-3.5 text-right text-muted tabular-nums">{formatDuration(run.durationMs)}</td>
                    <td className="px-6 py-3.5 text-right tabular-nums text-ink">{formatNumber(run.recordsRead)}</td>
                    <td className="px-6 py-3.5 text-right font-medium tabular-nums text-brand">{formatNumber(run.recordsLoaded)}</td>
                    <td className="px-6 py-3.5 text-right tabular-nums text-clay">{formatNumber(run.recordsSkipped)}</td>
                  </motion.tr>
                ))
              )}
            </AnimatePresence>
          </tbody>
        </table>
      </div>
    </section>
  )
}
