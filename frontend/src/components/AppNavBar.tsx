import { useState } from 'react'
import { Container, Nav, Navbar } from 'react-bootstrap'
import { NavLink, useNavigate } from 'react-router-dom'
import { Button } from './Button'
import { useToast } from './ToastProvider'
import { useAuth } from '../hooks/useAuth'

export function AppNavBar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const showToast = useToast()
  const [loggingOut, setLoggingOut] = useState(false)

  async function handleLogout() {
    setLoggingOut(true)

    try {
      await logout()
      showToast('info', 'You have logged out.')
      navigate('/login', { replace: true })
    } catch {
      showToast('danger', 'Unable to log out. Please try again.')
    } finally {
      setLoggingOut(false)
    }
  }

  return (
    <Navbar bg="light" expand="md" collapseOnSelect>
      <Container>
        <Navbar.Brand as={NavLink} to="/dashboard">
          Reactive Banking
        </Navbar.Brand>

        <Navbar.Toggle aria-controls="bank-navigation" />

        <Navbar.Collapse id="bank-navigation">
          <Nav className="me-auto">
            <Nav.Link
              as={NavLink}
              to="/dashboard"
              eventKey="dashboard"
            >
              Dashboard
            </Nav.Link>

            <Nav.Link
              as={NavLink}
              to="/transactions"
              eventKey="transactions"
            >
              Transactions
            </Nav.Link>

            <Nav.Link
              as={NavLink}
              to="/transaction-history"
              eventKey="history"
            >
              History
            </Nav.Link>

            <Nav.Link
                as={NavLink}
                to="/user-profile"
                eventKey="profile"
            >
              Profile
            </Nav.Link>
          </Nav>

          {user && (
            <Navbar.Text className="me-3">
              Hello, {user.FirstName}
            </Navbar.Text>
          )}

          <Button
            variant="outline-secondary"
            loading={loggingOut}
            onClick={handleLogout}
          >
            Log out
          </Button>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  )
}