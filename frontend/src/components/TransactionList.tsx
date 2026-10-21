import { Table } from 'react-bootstrap'
import type { Transaction } from '../models/models'
import { formatCurrency } from '../utils/formatCurrency'
import {formatDate} from '../utils/formatDate'


interface TransactionListProps {
  transactions: Transaction[]
  title?: string
  showAccountIds?: boolean
  colorByType?: boolean
}

const ROW_CLASS_BY_TYPE: Record<string, string> = {
  Deposit: 'table-success',
  Withdraw: 'table-danger',
  Transfer: 'table-info',
}

function rowClassFor(type: Transaction['TransactionType']): string {
  return ROW_CLASS_BY_TYPE[String(type)] ?? ''
}

export function TransactionList({
  transactions,
  title = 'Recent Transactions',
  showAccountIds = false,
    colorByType = false
}: TransactionListProps) {

  return (
    <section className="mt-4 text-start">
      <h2 className="fs-4 text-dark">{title}</h2>

      {transactions.length === 0 ? (
        <p>No transactions yet.</p>
      ) : (
        <Table striped bordered hover responsive>
          <caption className="visually-hidden">{title}</caption>

          <thead>
            <tr>
              <th scope="col">Date</th>
              <th scope="col">Type</th>
              <th scope="col" className="text-end" >
                Amount
              </th>
              {showAccountIds && (
                  <>
                    <th scope="col">Origin ID</th>
                    <th scope="col">Destination ID</th>
                  </>
              )}
            </tr>
          </thead>

          <tbody>
          {transactions.map((transaction) => (
              <tr
                  key={transaction.ID}
                  className={colorByType ? rowClassFor(transaction.TransactionType) : undefined}
              >

                <td>{formatDate(transaction.Date)}</td>
                <td>{transaction.TransactionType}</td>
                <td className="text-end">
                  {formatCurrency(transaction.Amount)}
                </td>
                {showAccountIds && (
                    <>
                      <td>{transaction.OriginID ?? '---'}</td>
                      <td>{transaction.DestinationID ?? '---'}</td>
                    </>
                )}
              </tr>
            ))}
          </tbody>
        </Table>
      )}
    </section>
  )
}