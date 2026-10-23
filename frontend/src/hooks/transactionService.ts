import type { TransactionList } from '../models/models'
import { tokenHeader } from './api'

const baseUrl = (import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080').replace(/\/$/, '')

const transactionService = {
  async getTransactions(
    filters: { numTransactions?: number } = {},
  ): Promise<TransactionList> {
    const params = new URLSearchParams()
    if (filters.numTransactions !== undefined) {
      params.set('numTransactions', String(filters.numTransactions))
    }
    const response = await fetch(`${baseUrl}/transactions?${params}`, {
      headers: tokenHeader(),
    })
    if (!response.ok) {
      throw new Error(`Unable to load transactions (${response.status}).`)
    }
    return response.json()
  },
}

export default transactionService
