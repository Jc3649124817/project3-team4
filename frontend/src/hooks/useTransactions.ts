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

    //   try {
    //     // The mock service caps large limits to the available records.
    //     const requestedLimit = limit ?? Number.MAX_SAFE_INTEGER
    //
    //     // 1 selects the successful mock response.
    //     const response = await transactionService.getTransactions(
    //       5,
    //
    //     )
    //
    //     if (!active) return
    //
    //     if (response.error || !response.data) {
    //       setTransactions([])
    //       setError(response.error ?? 'Unable to load transactions.')
    //     } else {
    //       // The service already returns transactions ordered by newest ID.
    //       setTransactions(response.data.Transactions)
    //     }
    //   } catch {
    //     if (active) {
    //       setTransactions([])
    //       setError('Unable to load transactions. Please try again.')
    //     }
    //   } finally {
    //     if (active) setLoading(false)
    //   }
    // }

      try {
        // If limit is undefined, the param is dropped and the backend default (5) applies.
        // The backend caps it at 100.
        const result = await transactionService.getTransactions({
          numTransactions: limit,
        })

        if (!active) return
        setTransactions(result.Transactions ?? [])
      } catch (err: any) {
        if (!active) return
        setTransactions([])
        setError(
            err.response?.data?.message ??   // Spring error message (server.error.include-message=always)
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