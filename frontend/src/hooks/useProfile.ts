import { useState } from 'react';
import { useAuth } from './useAuth';
import { useToast } from '../components/ToastProvider';
import type { UpdateProfileRequest } from '../models/models.ts';
import * as authService from './authService.tsx';

export function useProfile() {
    const { user, updateUser } = useAuth();
    const showToast = useToast();
    const [saving, setSaving] = useState(false);

    async function save(changes: UpdateProfileRequest): Promise<boolean> {
        if (!user) {
            return false;
        }

        setSaving(true);
        try {
            const res = await authService.updateProfile(user, changes);

            if (!res.data) {
                showToast(res.status === 400 ? 'warning' : 'danger',
                    res.error ?? 'Unable to save changes.');
                return false;
            }

            updateUser(res.data);
            showToast('success', 'Profile updated successfully.');

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