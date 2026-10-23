import { LoadingSpinner} from "../components/LoadingSpinner.tsx";
import { Col, Container, Row, Card } from "react-bootstrap";
import { useAuth } from '../hooks/useAuth.tsx';
import { NameForm } from '../components/NameForm';
import { ContactForm } from '../components/ContactForm';
import {PasswordForm} from "../components/PasswordForm.tsx";

export function UserProfilePage() {
    const { isAuthenticated, isLoading } = useAuth();

    if (isLoading) {
        return <LoadingSpinner label="Loading profile..." />;
    }

    if (!isAuthenticated) {
        return null;
    }

    return (
        <Container className="py-4">
            <Row className="justify-content-center">
                <Col md={10} lg={8}>
                    <h1 className="h3 mb-4">Your Profile</h1>
                    <Card className="mb-4">
                        <Card.Header>Name</Card.Header>
                        <Card.Body><NameForm/></Card.Body>
                    </Card>
                    <Card className="mb-4">
                        <Card.Header>Contact Info</Card.Header>
                        <Card.Body><ContactForm/></Card.Body>
                    </Card>
                    <Card className="mb-4">
                        <Card.Header>Password</Card.Header>
                        <Card.Body><PasswordForm/></Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    )

}