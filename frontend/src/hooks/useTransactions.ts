import { useEffect, useState } from 'react'
import { TransactionService } from '../hooks/transactionService'
import type { Transaction } from '../models/models'

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
        // The mock service caps large limits to the available records.
        const requestedLimit = limit ?? Number.MAX_SAFE_INTEGER

        // 1 selects the successful mock response.
        const response = await TransactionService.getTransactions(
          1,
          requestedLimit
        )

        if (!active) return

        if (response.error || !response.data) {
          setTransactions([])
          setError(response.error ?? 'Unable to load transactions.')
        } else {
          // The service already returns transactions ordered by newest ID.
          setTransactions(response.data.Transactions)
        }
      } catch {
        if (active) {
          setTransactions([])
          setError('Unable to load transactions. Please try again.')
        }
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