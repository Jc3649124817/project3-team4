// import api from "./api";
import type {APIResponse, LoginRequest, TransactionList, User} from "../models/models.ts";
import {UserService} from "../service/UserService.ts";
import axios from 'axios'
import { tokenHeader } from "../hooks/api";

const BASE = "/transactions";

const cleanParams = (params) =>
    Object.fromEntries(
        Object.entries(params).filter(([, v]) => v !== undefined && v !== null && v !== "")
    );

const transactionService = {

    getTransactions: async (filters: { numTransactions?: number } = {}): Promise<TransactionList> => {
        const params = new URLSearchParams();
        if (filters.numTransactions !== undefined) {
            params.set("numTransactions", String(filters.numTransactions));
        }

        const response = await fetch(`http://localhost:8080/transactions?${params}`, {
            headers: tokenHeader(),
        });
        if (!response.ok) throw new Error(`Failed to load transactions: ${response.status}`);
        return response.json();
    },


}

export default transactionService;