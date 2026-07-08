import { motion } from 'framer-motion'
import { Loader2, Play, Sprout } from 'lucide-react'

export default function Header({ onRun, running, live }) {
  return (
    <header className="relative z-10 flex flex-col gap-6 pb-8 sm:flex-row sm:items-end sm:justify-between">
      <div className="flex items-start gap-4">
        <div className="flex h-12 w-12 items-center justify-center rounded-2xl border border-brand/25 bg-brand-soft">
          <Sprout className="h-6 w-6 text-brand" />
        </div>
        <div>
          <p className="text-xs font-medium uppercase tracking-[0.2em] text-muted">Pipeline de dados</p>
          <h1 className="font-display text-3xl font-semibold leading-tight text-ink sm:text-4xl">
            Merenda Escolar
          </h1>
          <p className="mt-1 text-sm text-muted">Ingestao, validacao e carga com monitoramento por execucao</p>
        </div>
      </div>

      <div className="flex items-center gap-4">
        <span className="flex items-center gap-2 text-xs font-medium text-muted">
          <span className={live ? 'live-dot' : 'live-dot opacity-30'} />
          {live ? 'ao vivo' : 'sem conexao'}
        </span>
        <motion.button
          type="button"
          onClick={onRun}
          disabled={running}
          whileHover={{ y: -1 }}
          whileTap={{ scale: 0.98 }}
          className="inline-flex items-center gap-2 rounded-xl bg-brand px-5 py-2.5 text-sm font-semibold text-paper shadow-sm transition hover:bg-brand-deep disabled:cursor-not-allowed disabled:opacity-60"
        >
          {running ? <Loader2 className="h-4 w-4 animate-spin" /> : <Play className="h-4 w-4" />}
          {running ? 'Executando' : 'Executar pipeline'}
        </motion.button>
      </div>
    </header>
  )
}
