import { motion } from 'framer-motion'
import { CheckCircle2, Coins, Layers, Timer, XCircle } from 'lucide-react'
import { useCountUp } from '../hooks/useCountUp.js'
import { formatCurrency, formatDuration, formatNumber } from '../utils/format.js'

const TONE = {
  brand: 'text-brand',
  clay: 'text-clay',
  danger: 'text-danger',
  muted: 'text-muted',
}

function AnimatedValue({ value, format }) {
  const animated = useCountUp(Number(value ?? 0))
  return <>{format(animated)}</>
}

function Card({ icon: Icon, label, value, format, tone, index }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 14 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ delay: index * 0.06, ease: 'easeOut' }}
      className="card p-5"
    >
      <div className="flex items-center justify-between">
        <span className="text-xs font-medium uppercase tracking-[0.12em] text-muted">{label}</span>
        <Icon className={`h-4 w-4 ${TONE[tone]}`} />
      </div>
      <p className="mt-4 font-display text-3xl font-semibold text-ink">
        <AnimatedValue value={value} format={format} />
      </p>
    </motion.div>
  )
}

export default function StatsCards({ stats }) {
  if (!stats) return null

  const cards = [
    { icon: Layers, label: 'Registros carregados', value: stats.totalRecordsLoaded, format: formatNumber, tone: 'brand' },
    { icon: Coins, label: 'Custo total', value: stats.totalCost, format: formatCurrency, tone: 'clay' },
    { icon: CheckCircle2, label: 'Runs concluidos', value: stats.completedRuns, format: formatNumber, tone: 'brand' },
    { icon: XCircle, label: 'Runs com falha', value: stats.failedRuns, format: formatNumber, tone: 'danger' },
    { icon: Timer, label: 'Duracao do ultimo run', value: stats.lastDurationMs ?? 0, format: formatDuration, tone: 'muted' },
  ]

  return (
    <div className="grid grid-cols-2 gap-4 lg:grid-cols-5">
      {cards.map((card, index) => (
        <Card key={card.label} index={index} {...card} />
      ))}
    </div>
  )
}
