const currencyFormatter = new Intl.NumberFormat('pt-BR', {
  style: 'currency',
  currency: 'BRL',
})

const numberFormatter = new Intl.NumberFormat('pt-BR')

export function formatCurrency(value) {
  return currencyFormatter.format(Number(value ?? 0))
}

export function formatNumber(value) {
  return numberFormatter.format(Number(value ?? 0))
}

export function formatDuration(ms) {
  if (ms == null) return '-'
  if (ms < 1000) return `${ms} ms`
  return `${(ms / 1000).toFixed(2)} s`
}

export function formatDateTime(value) {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return '-'
  return date.toLocaleString('pt-BR')
}

export function statusStyle(status) {
  switch (status) {
    case 'COMPLETED':
      return { label: 'Concluido', className: 'bg-brand-soft text-brand-deep border-brand/30' }
    case 'FAILED':
      return { label: 'Falhou', className: 'bg-danger-soft text-danger border-danger/30' }
    case 'STARTED':
    case 'STARTING':
      return { label: 'Em execucao', className: 'bg-clay-soft text-clay border-clay/30' }
    default:
      return { label: status, className: 'bg-paper-deep text-muted border-line' }
  }
}
