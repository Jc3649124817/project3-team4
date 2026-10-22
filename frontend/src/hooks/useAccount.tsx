import { useState} from "react";
import type {AccountList} from "../models/models.ts";
import {AccountService} from "../service/AccountService.ts";

export function useAccount(){

    const [accounts, setAccounts] = useState<AccountList | null>(null);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string| null>(null);

     const updateAccounts = async () => {
        setLoading(true);
        setError(null);
        try {
            const newAccounts = await AccountService.getAccountsByUsername(1);
            if (newAccounts.data == null) {
                setError(newAccounts.error)
            } else {
                setAccounts(newAccounts.data)
            }
        } catch (err) {
            console.log(err)
        } finally {
            setLoading(false)
        }
    }

    return {accounts, loading, error, updateAccounts}
}