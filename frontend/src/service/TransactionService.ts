import type {APIResponse, Transaction, TransactionList, NewTransaction} from "../models/models.ts";

const delay = (ms: number) => new Promise<void>((r) => setTimeout(r, ms));
const randomDelay = (min = 500, max = 800) =>
    delay(Math.floor(Math.random() * (max - min + 1)) + min);


export const MOCK_TRANSACTION: Transaction = {
    "Date": "10-01-2026",
    "TransactionType": "Deposit",
    "Amount": 21000.00,
    "OriginID": null,
    "DestinationID": 9876543211,
    "ID" : 1
}
export const MOCK_TRANSACTIONS: TransactionList = {

    "Transactions": [
        {"Date": "09-01-2026", "TransactionType": "Deposit",  "Amount": 10000.00, "OriginID": null,       "DestinationID": 9876543211, "ID": 1},
        {"Date": "09-02-2026", "TransactionType": "Deposit",  "Amount": 5000.00,  "OriginID": null,       "DestinationID": 1123456789, "ID": 2},
        {"Date": "09-03-2026", "TransactionType": "Transfer", "Amount": 2000.00,  "OriginID": 9876543211, "DestinationID": 1123456789, "ID": 3},
        {"Date": "09-04-2026", "TransactionType": "Withdraw", "Amount": 1000.00,  "OriginID": 1123456789, "DestinationID": null,       "ID": 4},
        {"Date": "09-05-2026", "TransactionType": "Deposit",  "Amount": 5000.00,  "OriginID": null,       "DestinationID": 9876543211, "ID": 5},
        {"Date": "09-06-2026", "TransactionType": "Transfer", "Amount": 1500.00,  "OriginID": 1123456789, "DestinationID": 9876543211, "ID": 6},
        {"Date": "09-07-2026", "TransactionType": "Withdraw", "Amount": 2500.00,  "OriginID": 9876543211, "DestinationID": null,       "ID": 7},
        {"Date": "09-08-2026", "TransactionType": "Deposit",  "Amount": 2000.00,  "OriginID": null,       "DestinationID": 1123456789, "ID": 8},
        {"Date": "09-09-2026", "TransactionType": "Transfer", "Amount": 3000.00,  "OriginID": 9876543211, "DestinationID": 1123456789, "ID": 9},
        {"Date": "09-10-2026", "TransactionType": "Withdraw", "Amount": 500.00,   "OriginID": 1123456789, "DestinationID": null,       "ID": 10},
        {"Date": "09-11-2026", "TransactionType": "Deposit",  "Amount": 10000.00, "OriginID": null,       "DestinationID": 9876543211, "ID": 11},
        {"Date": "09-12-2026", "TransactionType": "Transfer", "Amount": 4000.00,  "OriginID": 1123456789, "DestinationID": 9876543211, "ID": 12},
        {"Date": "09-13-2026", "TransactionType": "Withdraw", "Amount": 3000.00,  "OriginID": 9876543211, "DestinationID": null,       "ID": 13},
        {"Date": "09-14-2026", "TransactionType": "Deposit",  "Amount": 1500.00,  "OriginID": null,       "DestinationID": 1123456789, "ID": 14},
        {"Date": "09-15-2026", "TransactionType": "Transfer", "Amount": 500.00,   "OriginID": 9876543211, "DestinationID": 1123456789, "ID": 15},
        {"Date": "09-16-2026", "TransactionType": "Withdraw", "Amount": 2000.00,  "OriginID": 1123456789, "DestinationID": null,       "ID": 16},
        {"Date": "09-17-2026", "TransactionType": "Deposit",  "Amount": 3000.00,  "OriginID": null,       "DestinationID": 9876543211, "ID": 17},
        {"Date": "09-18-2026", "TransactionType": "Transfer", "Amount": 1000.00,  "OriginID": 1123456789, "DestinationID": 9876543211, "ID": 18},
        {"Date": "09-19-2026", "TransactionType": "Withdraw", "Amount": 1500.00,  "OriginID": 9876543211, "DestinationID": null,       "ID": 19},
        {"Date": "09-20-2026", "TransactionType": "Deposit",  "Amount": 2500.00,  "OriginID": null,       "DestinationID": 1123456789, "ID": 20},
        {"Date": "09-21-2026", "TransactionType": "Transfer", "Amount": 2500.00,  "OriginID": 9876543211, "DestinationID": 1123456789, "ID": 21},
        {"Date": "09-22-2026", "TransactionType": "Withdraw", "Amount": 4000.00,  "OriginID": 1123456789, "DestinationID": null,       "ID": 22},
        {"Date": "09-23-2026", "TransactionType": "Deposit",  "Amount": 1000.00,  "OriginID": null,       "DestinationID": 9876543211, "ID": 23},
        {"Date": "09-24-2026", "TransactionType": "Transfer", "Amount": 500.00,   "OriginID": 1123456789, "DestinationID": 9876543211, "ID": 24},
        {"Date": "09-25-2026", "TransactionType": "Withdraw", "Amount": 2000.00,  "OriginID": 9876543211, "DestinationID": null,       "ID": 25},
        {"Date": "09-26-2026", "TransactionType": "Deposit",  "Amount": 500.00,   "OriginID": null,       "DestinationID": 1123456789, "ID": 26},
        {"Date": "09-27-2026", "TransactionType": "Transfer", "Amount": 3500.00,  "OriginID": 1123456789, "DestinationID": 9876543211, "ID": 27},
        {"Date": "09-28-2026", "TransactionType": "Withdraw", "Amount": 3000.00,  "OriginID": 9876543211, "DestinationID": null,       "ID": 28},
        {"Date": "09-29-2026", "TransactionType": "Deposit",  "Amount": 500.00,   "OriginID": null,       "DestinationID": 9876543211, "ID": 29},
        {"Date": "09-30-2026", "TransactionType": "Withdraw", "Amount": 500.00,   "OriginID": 1123456789, "DestinationID": null,       "ID": 30}
    ]
};

export class TransactionService {
    static async putTransaction(input: number,newTransaction :NewTransaction ): Promise<APIResponse<Transaction>> {
        await randomDelay();
        if(input === 1){
            const newID =   Math.max(...(MOCK_TRANSACTIONS.Transactions).map(item => item.ID)) + 1;

            const newMockTransaction = {"Date": "10-09-2026", "TransactionType": newTransaction.TransactionType,
                "Amount": newTransaction.Amount, "OriginID": newTransaction.OriginID, "DestinationID": newTransaction.DestinationID, "ID": newID
            };
            MOCK_TRANSACTIONS.Transactions.push(newMockTransaction)
            return {
                data: newMockTransaction,
                error: null,
                status: 201,
            };
        }
        else if(input === 0){
            return {
                data: null,
                error: "Transaction Failed: Invalid amount input.",
                status: 400,
            };
        }else if(input === -1){
            return {
                data: null,
                error: "Transaction Failed: User is not logged in.",
                status: 401,
            };
        }else if(input === -2){
            return {
                data: null,
                error: "Origin Account ID doesn't belong to the logged-in user.",
                status: 403,
            };
        }
        else{
            return{
                data: null,
                error: "Unimplemented error.",
                status: 501,
            };
        }

    };

    static async getTransactions(input: number, numTransactions: number = 5): Promise<APIResponse<TransactionList>> {
        await randomDelay();
        if(numTransactions > MOCK_TRANSACTIONS.Transactions.length){
            numTransactions = MOCK_TRANSACTIONS.Transactions.length
        }
        const clonedTransactions = structuredClone(MOCK_TRANSACTIONS);
        clonedTransactions.Transactions.sort((a, b) => b.ID - a.ID);
        if(input === 1) {
            return {
                data: {
                    "Transactions": [...clonedTransactions.Transactions]
                        .slice(0, numTransactions)
                },
                error: null,
                status: 200,
            };
        }
        else if(input === 0){
            return {
                data: null,
                error: "Transaction History Failed: User is not authenticated.",
                status: 401,
            };
        }else if(input === -1){
            return {
                data: null,
                error: "Transaction History Failed: Transactions belong to another user or account.",
                status: 403,
            };
        }else{
            return{
                data: null,
                error: "Unimplemented error.",
                status: 501,
            };
        }
    };
}
