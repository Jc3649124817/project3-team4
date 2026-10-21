import { useState, type ChangeEvent, type SubmitEvent } from 'react';
import { Form } from 'react-bootstrap';
import { Button } from './Button';
import { Input } from './Input';
import { useAuth } from '../hooks/useAuth';
import { useProfile } from '../hooks/useProfile';
import type { UpdateProfileRequest } from '../models/models.ts';
import {useToast} from "./ToastProvider.tsx";

export function ContactForm() {
    const { user } = useAuth();
    const { save, saving } = useProfile();
    const showToast = useToast();

    const [values, setValues] = useState({
        Email: user?.Email ?? '',
        PhoneNumber: user?.PhoneNumber ?? '',
    });

    const [errors, setErrors] = useState<{ Email?: string, PhoneNumber?: string }>({});

    const dirty =
        values.Email !== user?.Email || values.PhoneNumber !== user?.PhoneNumber;

    function handleChange(e: ChangeEvent<HTMLInputElement>) {
        const { name, value } = e.target;
        setValues((prev) => ({ ...prev, [name]: value }));
    }

    async function handleSubmit(e: SubmitEvent<HTMLFormElement>) {
        e.preventDefault();

        const newErrors: { Email?: string ; PhoneNumber?: string } = {};
        if (!values.Email.trim()) newErrors.Email = 'Email is required.';
        if (!values.PhoneNumber) newErrors.PhoneNumber = 'Phone number is required.';
        setErrors(newErrors);
        if (Object.keys(newErrors).length > 0) {
            showToast('warning', 'Please fill in all fields.');
            return;
        }
        const changes: UpdateProfileRequest = {};
        if (values.Email !== user?.Email) changes.Email = values.Email.trim();
        if (values.PhoneNumber !== user?.PhoneNumber) changes.PhoneNumber = values.PhoneNumber.trim();

        await save(changes);
    }

    return (
        <Form noValidate onSubmit={handleSubmit}>
            <Input
                label="Email"
                name="Email"
                value={values.Email}
                onChange={handleChange}
                autoComplete="email"
                error={errors.Email}
            />
            <Input
                label="Phone number"
                name="PhoneNumber"
                value={values.PhoneNumber}
                onChange={handleChange}
                autoComplete="tel"
                error={errors.PhoneNumber}
            />
            <Button type="submit" loading={saving} disabled={!dirty}>
                Save contact information
            </Button>
        </Form>
    );
}