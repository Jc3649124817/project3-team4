import { useEffect, useState } from 'react'
import { Alert, Col, Container, Row } from 'react-bootstrap'
import { StatCard } from '../components/StatCard'
import { TransactionList } from '../components/TransactionList'
import { LoadingSpinner } from '../components/LoadingSpinner'
import { AccountApi } from '../service/AccountApi'
import { useAuth } from '../hooks/useAuth'
import { useTransactions } from '../hooks/useTransactions'
import type { Account } from '../models/models'

export function DashboardPage() {
  const { token } = useAuth()
  const [accounts, setAccounts] = useState<Account[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const {
    transactions,
    loading: transactionsLoading,
    error: transactionsError,
  } = useTransactions(5)

  // Load account balances.
  useEffect(() => {
    let active = true
    setAccounts([])
    setError(null)
    setLoading(true)

    if (!token) {
      setError('Please log in to view your accounts.')
      setLoading(false)
      return
    }

    const requestToken = token
    async function loadAccounts() {
      try {
        const response = await AccountApi.getAccounts(requestToken)

        if (!active) return

        if (response.error || !response.data) {
          setError(response.error ?? 'Unable to load accounts.')
        } else {
          setAccounts(response.data.Accounts)
        }
      } catch {
        if (active) {
          setError('Unable to load accounts. Please try again.')
        }
      } finally {
        if (active) setLoading(false)
      }
    }

    void loadAccounts()

    return () => {
      active = false
    }
  }, [token])

  return (
    <Container className="py-4">
      <div className="content-panel">
      <h1 className="mb-4 text-dark">Your Dashboard</h1>

      {loading ? (
        <LoadingSpinner label="Loading your accounts..." />
      ) : error ? (
        <Alert variant="danger">{error}</Alert>
      ) : accounts.length === 0 ? (
        <p>No accounts found.</p>
      ) : (
        <Row className="g-3">
          {accounts.map((account) => (
            <Col xs={12} md={6} key={account.AccountNumber}>
              <StatCard
                label={account.AccountType}
                balance={account.Balance}
              />
            </Col>
          ))}
        </Row>
      )}

      {transactionsLoading ? (
        <LoadingSpinner label="Loading recent transactions..." />
      ) : transactionsError ? (
        <Alert variant="danger" className="mt-4">
          {transactionsError}
        </Alert>
      ) : (
        <TransactionList transactions={transactions} />
      )}
      </div>
    </Container>
  )
}


