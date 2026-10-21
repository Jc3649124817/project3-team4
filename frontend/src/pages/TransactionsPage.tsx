import { useEffect, useState } from 'react';
import { Alert, Card, Col, Row } from 'react-bootstrap';
import { Button } from '../components/Button';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { StatCard } from '../components/StatCard';
import { TransactionForm } from '../components/TransactionForm';
import { useToast } from '../components/ToastProvider';
import { useAuth } from '../hooks/useAuth';
import type { Account, NewTransaction } from '../models/models';
import { AccountService } from '../service/AccountService';

async function loadAccounts(): Promise<Account[]> {
  const response = await AccountService.getAccountsByUsername(1);
  if (response.error || response.status !== 200 || !response.data) {
    throw new Error(response.error || 'Unable to load your accounts.');
  }
  return response.data.Accounts.map((account) => ({ ...account }));
}

export function TransactionsPage() {
  const { user } = useAuth();
  const showToast = useToast();
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [attempt, setAttempt] = useState(0);

  useEffect(() => {
    let active = true;
    loadAccounts().then(
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
  }, [attempt]);

  async function handleTransaction(request: NewTransaction) {
    try {
      const response = await AccountService.updateAccountBalances(1, request);
      if (response.status !== 200 || !response.data) {
        throw new Error(response.error || 'Unable to complete the transaction.');
      }
      setAccounts(response.data.Accounts);
    } catch (reason) {
      const message = reason instanceof Error ? reason.message : 'Transaction failed. Please try again.';
      showToast('danger', message);
      throw new Error(message, { cause: reason });
    }

    showToast('success', `${request.TransactionType} of ${new Intl.NumberFormat('en-US', {
      style: 'currency', currency: 'USD',
    }).format(request.Amount)} completed successfully.`);
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
