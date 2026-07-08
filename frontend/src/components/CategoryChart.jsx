import { Bar, BarChart, CartesianGrid, Cell, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'
import { formatCurrency } from '../utils/format.js'

// Verde da marca com variacao de tom por barra, mais o clay como destaque.
const PALETTE = ['#2f6b4e', '#3a7d5c', '#4f8f6f', '#b45309', '#6aa588', '#8bbaa4']

export default function CategoryChart({ categorias }) {
  const data = (categorias ?? []).map((c) => ({
    categoria: c.categoria,
    custo: Number(c.custoTotal ?? 0),
    quantidade: c.quantidade,
  }))

  return (
    <section className="card h-full p-6">
      <h2 className="font-display text-lg font-semibold text-ink">Custo por categoria</h2>
      <p className="mb-5 text-xs text-muted">Distribuicao do custo total carregado</p>
      {data.length === 0 ? (
        <p className="py-16 text-center text-sm text-muted">Sem dados carregados ainda.</p>
      ) : (
        <ResponsiveContainer width="100%" height={300}>
          <BarChart data={data} margin={{ top: 8, right: 8, left: 8, bottom: 8 }}>
            <CartesianGrid strokeDasharray="2 4" stroke="#e4ddcd" vertical={false} />
            <XAxis dataKey="categoria" stroke="#7c7566" fontSize={11} tickLine={false} axisLine={{ stroke: '#e4ddcd' }} />
            <YAxis stroke="#7c7566" fontSize={11} tickLine={false} axisLine={false} width={44} />
            <Tooltip
              cursor={{ fill: 'rgba(47, 107, 78, 0.06)' }}
              contentStyle={{
                background: '#fbf9f4',
                border: '1px solid #e4ddcd',
                borderRadius: 10,
                color: '#211f1a',
                fontSize: 13,
              }}
              formatter={(value) => [formatCurrency(value), 'Custo']}
            />
            <Bar dataKey="custo" radius={[6, 6, 0, 0]} animationDuration={900} animationEasing="ease-out">
              {data.map((entry, index) => (
                <Cell key={entry.categoria} fill={PALETTE[index % PALETTE.length]} />
              ))}
            </Bar>
          </BarChart>
        </ResponsiveContainer>
      )}
    </section>
  )
}
