import { useEffect, useState } from 'react'
// import { TransactionService } from '../hooks/transactionService'
import type { Transaction } from '../models/models'
import transactionService from '../hooks/transactionService'

export function useTransactions(limit?: number) {
  const [transactions, setTransactions] = useState<Transaction[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    let active = true

    async function loadTransactions() {
      setLoading(true)
      setError(null)

      try {
        const result = await transactionService.getTransactions({
          numTransactions: limit,
        })

        if (!active) return
        setTransactions(result.Transactions ?? [])
      } catch (err: any) {
        if (!active) return
        setTransactions([])
        setError(
            err.response?.data?.message ??
            err.message ??
            'Unable to load transactions. Please try again.'
        )
      } finally {
        if (active) setLoading(false)
      }
    }

    void loadTransactions()

    return () => {
      active = false
    }
  }, [limit])

  return { transactions, loading, error }
}