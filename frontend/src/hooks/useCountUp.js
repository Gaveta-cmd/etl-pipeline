import { useEffect, useRef, useState } from 'react'

/**
 * Anima um numero de forma incremental (ease-out) sempre que o alvo muda.
 * Usa requestAnimationFrame e interpola apenas um valor numerico -> 60fps,
 * sem layout thrashing.
 *
 * @param {number} target valor final
 * @param {number} duration duracao em ms
 * @returns {number} valor corrente da animacao
 */
export function useCountUp(target, duration = 700) {
  const [value, setValue] = useState(target)
  const fromRef = useRef(target)
  const rafRef = useRef(0)

  useEffect(() => {
    const from = fromRef.current
    const delta = target - from
    if (delta === 0) return

    const start = performance.now()
    const easeOut = (t) => 1 - Math.pow(1 - t, 3)

    const tick = (now) => {
      const progress = Math.min((now - start) / duration, 1)
      const current = from + delta * easeOut(progress)
      setValue(current)
      if (progress < 1) {
        rafRef.current = requestAnimationFrame(tick)
      } else {
        fromRef.current = target
      }
    }

    rafRef.current = requestAnimationFrame(tick)
    return () => cancelAnimationFrame(rafRef.current)
  }, [target, duration])

  return value
}
