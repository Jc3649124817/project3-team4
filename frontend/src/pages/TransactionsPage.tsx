import { useEffect, useState } from 'react';
import { Alert, Card, Col, Row } from 'react-bootstrap';
import { Button } from '../components/Button';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { StatCard } from '../components/StatCard';
import { TransactionForm } from '../components/TransactionForm';
import { useToast } from '../components/ToastProvider';
import { useAuth } from '../hooks/useAuth';
import type { Account, NewTransaction } from '../models/models';
import { AccountApi } from '../service/AccountApi';

async function loadAccounts(token: string): Promise<Account[]> {
  const response = await AccountApi.getAccounts(token);
  if (response.error || response.status !== 200 || !response.data) {
    throw new Error(response.error || 'Unable to load your accounts.');
  }
  return response.data.Accounts.map((account) => ({ ...account }));
}

export function TransactionsPage() {
  const { user, token } = useAuth();
  const showToast = useToast();
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [attempt, setAttempt] = useState(0);

  useEffect(() => {
    let active = true;
    setAccounts([]);
    setLoading(true);
    setError(null);
    if (!token) {
      setError('Please log in to view your accounts.');
      setLoading(false);
      return;
    }
    loadAccounts(token).then(
      (data) => {
        if (active) {
          setAccounts(data);
          setLoading(false);
        }
      },
      (reason: unknown) => {
        if (active) {
          setError(reason instanceof Error ? reason.message : 'Unable to load your accounts.');
          setLoading(false);
        }
      },
    );
    return () => { active = false; };
  }, [attempt, token]);

  async function handleTransaction(request: NewTransaction) {
    if (!token) throw new Error('Please log in again.');
    const response = await AccountApi.createTransaction(request, token);
    if (response.status !== 201 || !response.data) {
      const message = response.error || 'Unable to complete the transaction.';
      showToast('danger', message);
      throw new Error(message);
    }
    showToast('success', 'Transaction completed successfully.');
    try {
      setAccounts(await loadAccounts(token));
    } catch {
      // The transaction succeeded. Do not report it as failed or invite resubmission.
      showToast('warning', 'Transaction saved, but balances could not reload. Refresh the page before another transaction.');
      setError('Transaction saved. Reload your balances before continuing.');
    }
  }
  return (
    <main className="container py-4 py-md-5">
      <div className="content-panel">
      <div className="d-flex flex-wrap justify-content-between align-items-center gap-3 mb-4">
        <div>
          <h1>Transactions</h1>
          <p className="text-muted mb-0">Manage your money, {user?.Username}.</p>
        </div>
      </div>

      {loading ? <LoadingSpinner label="Loading your accounts…" /> : error ? (
        <Alert variant="danger" role="alert">
          <p>{error}</p>
          <Button variant="outline-danger" onClick={() => {
            setError(null);
            setLoading(true);
            setAttempt((previous) => previous + 1);
          }}>Try again</Button>
        </Alert>
      ) : (
        <Row className="g-4">
          <Col xs={12} lg={5}>
            <section aria-label="Account balances">
              <h2 className="h5 mb-3">Your Accounts</h2>
              <Row className="g-3">
                {accounts.map((account) => (
                  <Col xs={12} sm={6} lg={12} key={account.AccountNumber}>
                    <StatCard
                      label={`${account.AccountType} · ${account.AccountNumber}`}
                      balance={account.Balance}
                    />
                  </Col>
                ))}
              </Row>
            </section>
          </Col>
          <Col xs={12} lg={7}>
            <Card className="shadow-sm">
              <Card.Body className="p-3 p-md-4">
                <TransactionForm accounts={accounts} onSubmit={handleTransaction} />
              </Card.Body>
            </Card>
          </Col>
        </Row>
      )}
      </div>
    </main>
  );
}

