import { useState, type ChangeEvent, type SubmitEvent } from 'react';
import { Form } from 'react-bootstrap';
import { Button } from './Button';
import { Input } from './Input';
import { useAuth } from '../hooks/useAuth';
import { useProfile } from '../hooks/useProfile';
import type { UpdateProfileRequest } from '../models/models.ts';
import { useToast} from "./ToastProvider.tsx";

export function PasswordForm() {
    const { user } = useAuth();
    const { save, saving } = useProfile();
    const showToast = useToast();

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
        if (!values.NewPassword) {
            newErrors.NewPassword = 'New password is required.';
        }
        if (!values.ConfirmPassword) {
            newErrors.ConfirmPassword = 'Please confirm your new password.';
        }

        if (
            values.CurrentPassword &&
            values.NewPassword &&
            values.NewPassword === values.CurrentPassword
        ) {
            newErrors.NewPassword =
                'New password must differ from your current password.';
        }

        if (
            values.CurrentPassword &&
            user?.Password &&
            values.CurrentPassword !== user.Password
        ) {
            newErrors.CurrentPassword = 'Current password is incorrect.';
        }

        setErrors(newErrors);

        if (Object.keys(newErrors).length > 0) {
            showToast('warning', "Please make sure your new password is different from your current password.")
            return;
        }

        const changes: UpdateProfileRequest = {};
        changes.Password = values.CurrentPassword.trim();
        try {
            await save(changes);
            showToast('success', 'Password changed successfully.');
        } catch {
            showToast('danger', 'Password change failed');
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