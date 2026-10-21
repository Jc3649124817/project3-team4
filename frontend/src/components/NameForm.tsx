import { useState, type ChangeEvent, type SubmitEvent } from 'react';
import { Form } from 'react-bootstrap';
import { Button } from './Button';
import { Input } from './Input';
import { useAuth } from '../hooks/useAuth';
import { useProfile } from '../hooks/useProfile';
import type { UpdateProfileRequest } from '../models/models.ts';
import {useToast} from "./ToastProvider.tsx";

export function NameForm() {
    const { user } = useAuth();
    const { save, saving } = useProfile();
    const showToast = useToast();

    const [values, setValues] = useState({
        FirstName: user?.FirstName ?? '',
        LastName: user?.LastName ?? '',
    });

    const [errors, setErrors] = useState<{ FirstName?: string; LastName?: string }>({});

    const dirty =
        values.FirstName !== user?.FirstName || values.LastName !== user?.LastName;

    function handleChange(e: ChangeEvent<HTMLInputElement>) {
        const { name, value } = e.target;
        setValues((prev) => ({ ...prev, [name]: value }));
    }

    async function handleSubmit(e: SubmitEvent<HTMLFormElement>) {
        e.preventDefault();

        const newErrors: { FirstName?: string; LastName?: string } = {};
        if (!values.FirstName.trim()) newErrors.FirstName = 'First name is required.';
        if (!values.LastName.trim()) newErrors.LastName = 'Last name is required.';
        setErrors(newErrors);
        if (Object.keys(newErrors).length > 0) {
            showToast('warning', 'Please enter your new first and/or last name.')
            return;
        }

        const changes: UpdateProfileRequest = {};
        if (values.FirstName !== user?.FirstName) changes.FirstName = values.FirstName.trim();
        if (values.LastName !== user?.LastName) changes.LastName = values.LastName.trim();

        try {
            await save(changes);
            showToast('success', 'First and/or last name changed successfully.');
        } catch {
            showToast('danger', 'First and/or last name change failed');
        }
    }

    return (
        <Form noValidate onSubmit={handleSubmit}>
            <Input
                label="First name"
                name="FirstName"
                value={values.FirstName}
                onChange={handleChange}
                autoComplete="given-name"
                error={errors.FirstName}
            />
            <Input
                label="Last name"
                name="LastName"
                value={values.LastName}
                onChange={handleChange}
                autoComplete="family-name"
                error={errors.LastName}
            />
            <Button type="submit" loading={saving} disabled={!dirty}>
                Save name
            </Button>
        </Form>
    );
}