// import api from "./api";
import type {APIResponse, LoginRequest, TransactionList, User} from "../models/models.ts";
import {UserService} from "../service/UserService.ts";
import axios from 'axios'


const BASE = "/transactions";

const cleanParams = (params) =>
    Object.fromEntries(
        Object.entries(params).filter(([, v]) => v !== undefined && v !== null && v !== "")
    );

const transactionService = {


    getTransactions: async (filters: { numTransactions?: number } = {}): Promise<TransactionList> => {
        const response = await axios.get<TransactionList>('http://localhost:8080/transactions', {
            params: filters,
        })
        return response.data
    },


}



export default transactionService;