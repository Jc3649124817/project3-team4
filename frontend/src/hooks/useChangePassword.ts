import { useState } from 'react';
import { useToast } from '../components/ToastProvider';
import * as authService from './authService.tsx';
import type { ChangePasswordRequest } from '../models/models.ts';

export function useChangePassword() {
    const showToast = useToast();
    const [saving, setSaving] = useState(false);

    async function save(request: ChangePasswordRequest): Promise<boolean> {
        setSaving(true);
        try {
            const res = await authService.changePassword(request);

            if (res.status !== 200) {
                showToast(res.status === 400 ? 'warning' : 'danger',
                    res.error ?? 'Unable to change password.');
                return false;
            }

            showToast('success', 'Password changed successfully.');
            return true;
        } catch {
            showToast('danger', 'Could not reach the server. Try again.');
            return false;
        } finally {
            setSaving(false);
        }
    }

    return { save, saving };
}