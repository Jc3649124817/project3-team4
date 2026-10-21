// import api from "./api";
import type {APIResponse, LoginRequest, TransactionList, User} from "../models/models.ts";
import {UserService} from "../service/UserService.ts";

const BASE = "/transactions";

const cleanParams = (params) =>
    Object.fromEntries(
        Object.entries(params).filter(([, v]) => v !== undefined && v !== null && v !== "")
    );

const transactionService = {


    getTransactions: async (numTransactions):Promise<APIResponse<TransactionList>> => {
        // try {
            const response = await axios.get('https://localhost:8080/transactions/{numTransactions}');
            return response;
        // }
        // catch (error) {
        //     setError(error.message);
        // }
    }

}

