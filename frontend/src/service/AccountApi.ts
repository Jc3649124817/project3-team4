import axios from 'axios'
import type { Account, AccountList, APIResponse, NewTransaction, Transaction } from '../models/models'

const baseUrl = (import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080').replace(/\/$/, '')

async function request<T>(
  path: string, token: string, transaction?: NewTransaction,
): Promise<APIResponse<T>> {
  if (!token.trim()) {
    return { data: null, error: 'Please log in.', status: 401 }
  }
  try {
    const response = await axios.request<T>({
      url: baseUrl + path,
      method: transaction ? 'POST' : 'GET',
      headers: { Authorization: `Bearer ${token}` },
      data: transaction,
    })
    return { data: response.data, error: null, status: response.status }
  } catch (reason: unknown) {
    if (!axios.isAxiosError(reason)) {
      return { data: null, error: 'An unexpected request error occurred.', status: 0 }
    }
    const status = reason.response?.status ?? 0
    const body: unknown = reason.response?.data
    let message = 'Unable to complete the request.'
    if (status === 0) {
      message = transaction
        ? 'No server response. Check your balances and history before retrying; the transaction may have completed.'
        : 'Could not reach the backend. Check that it is running.'
    } else if (status === 401) {
      message = 'Please log in again.'
    } else if (status === 403) {
      message = 'Access denied. Check your login and account ownership.'
    } else if (status >= 500) {
      message = 'The server could not complete the request.'
    } else if (typeof body === 'string' && body.trim()) {
      message = body
    } else if (body && typeof body === 'object' && 'message' in body
        && typeof body.message === 'string') {
      message = body.message
    }
    return { data: null, error: message, status }
  }
}

export class AccountApi {
  static getAccounts(token: string): Promise<APIResponse<AccountList>> {
    return request<AccountList>('/accounts', token)
  }

  static getAccount(number: number, token: string): Promise<APIResponse<Account>> {
    if (!Number.isSafeInteger(number) || number <= 0) {
      return Promise.resolve({ data: null, error: 'Invalid account number.', status: 400 })
    }
    return request<Account>(`/accounts/${number}`, token)
  }

  static createTransaction(
    transaction: NewTransaction, token: string,
  ): Promise<APIResponse<Transaction>> {
    return request<Transaction>('/transactions', token, transaction)
  }
}
