import { useState, type ChangeEvent, type SubmitEvent } from 'react';
import { Form } from 'react-bootstrap';
import { Button } from './Button';
import { Input } from './Input';
import { useChangePassword } from '../hooks/useChangePassword';

export function PasswordForm() {
    const { save, saving } = useChangePassword();

    const [values, setValues] = useState({
        CurrentPassword: '',
        NewPassword: '',
        ConfirmPassword: '',
    });

    const [errors, setErrors] = useState<{
        CurrentPassword?: string;
        NewPassword?: string;
        ConfirmPassword?: string;
    }>({});

    const dirty =
        values.CurrentPassword !== '' &&
        values.NewPassword !== '' &&
        values.ConfirmPassword !== '';

    function handleChange(e: ChangeEvent<HTMLInputElement>) {
        const { name, value } = e.target;
        setValues((prev) => ({ ...prev, [name]: value }));
    }

    async function handleSubmit(e: SubmitEvent<HTMLFormElement>) {
        e.preventDefault();

        const newErrors: {
            CurrentPassword?: string;
            NewPassword?: string;
            ConfirmPassword?: string;
        } = {};

        if (!values.CurrentPassword) {
            newErrors.CurrentPassword = 'Current password is required.';
        }
        if (values.NewPassword === values.CurrentPassword) {
            newErrors.NewPassword = 'New password must differ from your current password.';
        }
        if (values.NewPassword !== values.ConfirmPassword) {
            newErrors.ConfirmPassword = 'Passwords do not match.';
        }

        setErrors(newErrors);
        if (Object.keys(newErrors).length > 0) return;

        const ok = await save({
            CurrentPassword: values.CurrentPassword,
            NewPassword: values.NewPassword,
        });

        // Clear the fields only on success so the user doesn't retype on failure.
        if (ok) {
            setValues({ CurrentPassword: '', NewPassword: '', ConfirmPassword: '' });
            setErrors({});
        }
    }

    return (
        <Form noValidate onSubmit={handleSubmit}>
            <Input
                label="Current Password"
                name="CurrentPassword"
                type="password"
                value={values.CurrentPassword}
                onChange={handleChange}
                autoComplete="current-password"
                error={errors.CurrentPassword}
            />
            <Input
                label="New Password"
                name="NewPassword"
                type="password"
                value={values.NewPassword}
                onChange={handleChange}
                autoComplete="new-password"
                error={errors.NewPassword}
            />
            <Input
                label="Confirm New Password"
                name="ConfirmPassword"
                type="password"
                value={values.ConfirmPassword}
                onChange={handleChange}
                autoComplete="new-password"
                error={errors.ConfirmPassword}
            />
            <Button type="submit" loading={saving} disabled={!dirty}>
                Change Password
            </Button>
        </Form>
    );
}