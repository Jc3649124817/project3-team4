import type {APIResponse, Account, AccountList, NewTransaction} from "../models/models.ts";
import {TransactionService} from "./TransactionService";
import { validateAmount } from '../utils/validator';

const delay = (ms: number) => new Promise<void>((r) => setTimeout(r, ms));
const randomDelay = (min = 500, max = 800) =>
    delay(Math.floor(Math.random() * (max - min + 1)) + min);

export const MOCK_ACCOUNTS: AccountList = {
    "Accounts": [
        {"AccountNumber": 1123456789, "AccountType": "Checking", "Balance": 1000.00},
        {"AccountNumber": 9876543211, "AccountType": "Savings", "Balance": 20000.00}
    ]
};

export class AccountService {

    static async updateAccountBalances(input: number, newTransaction :NewTransaction): Promise<APIResponse<AccountList>>{
        if (input === 1){
            const transactionType = newTransaction.TransactionType
            const amount = newTransaction.Amount
            const originId = MOCK_ACCOUNTS.Accounts.findIndex(account => account.AccountNumber === newTransaction.OriginID)
            const destinationId = MOCK_ACCOUNTS.Accounts.findIndex(account => account.AccountNumber === newTransaction.DestinationID)
            const fail = (error: string, status = 400): APIResponse<AccountList> => ({ data: null, error, status })
            const amountError = validateAmount(String(amount))
            if (amountError) return fail(amountError)
            if (!['Deposit', 'Withdraw', 'Transfer'].includes(transactionType)) {
                return fail('Select a valid transaction type.')
            }
            if (transactionType !== 'Deposit' && originId === -1) {
                return fail('The source account is not available.', 403)
            }
            if (transactionType !== 'Withdraw' && destinationId === -1) {
                return fail('The destination account is not available.', 403)
            }
            if ((transactionType === 'Deposit' && newTransaction.OriginID !== null)
                || (transactionType === 'Withdraw' && newTransaction.DestinationID !== null)) {
                return fail('Invalid account fields for this transaction type.')
            }
            if (transactionType === 'Transfer' && originId === destinationId) {
                return fail('Choose a different destination account.')
            }
            if(transactionType != "Deposit" && amount > MOCK_ACCOUNTS.Accounts[originId].Balance){
                return{
                    data: null,
                    error: "Transaction failed: Insufficient funds",
                    status: 400,
                }
            }

            const transactionResponse = await TransactionService.putTransaction(input, newTransaction)
            if (transactionResponse.status !== 201 || !transactionResponse.data){
                return{
                    data: null,
                    error: transactionResponse.error || 'Unable to complete the transaction.',
                    status: transactionResponse.status,
                }
            }

            const cents = Math.round(amount * 100)
            switch(transactionType){
                case "Deposit":
                    MOCK_ACCOUNTS.Accounts[destinationId].Balance = (Math.round(MOCK_ACCOUNTS.Accounts[destinationId].Balance * 100) + cents) / 100
                    break
                case "Withdraw":
                    MOCK_ACCOUNTS.Accounts[originId].Balance = (Math.round(MOCK_ACCOUNTS.Accounts[originId].Balance * 100) - cents) / 100
                    break
                case "Transfer":
                    MOCK_ACCOUNTS.Accounts[destinationId].Balance = (Math.round(MOCK_ACCOUNTS.Accounts[destinationId].Balance * 100) + cents) / 100
                    MOCK_ACCOUNTS.Accounts[originId].Balance = (Math.round(MOCK_ACCOUNTS.Accounts[originId].Balance * 100) - cents) / 100
                    break
            }
            const clonedAccounts = structuredClone(MOCK_ACCOUNTS);
            return{
                data: clonedAccounts,
                error: "Accounts Updated: Transaction success",
                status: 200,
            }
        }else{
            return{
                data: null,
                error: "Unimplemented error",
                status: 501,
            }
        }
    }

    static async putAccounts(input : number): Promise<APIResponse<AccountList>>{
        await randomDelay();
        if (input === 1){
            return {
                data: MOCK_ACCOUNTS,
                error: null,
                status: 201,
            };
        }else if(input === 0) {
            return {
                data: null,
                error: "User did not register successfully, account creation failed.",
                status: 401,
            };
        } else{
            return {
                data: null,
                error: "Unimplemented error.",
                status: 501,
            };
        }
    }

    static async getAccountByAccountID(input: number, accountNumber: number): Promise<APIResponse<Account>> {
        await randomDelay();
        if (input === 1){
            const accountIndex = (accountNumber === MOCK_ACCOUNTS.Accounts[0].AccountNumber) ? 0 :  1
            return {
                data: MOCK_ACCOUNTS.Accounts[accountIndex],
                error: null,
                status: 200,
            };
        }
        else if(input === 0) {
            return {
                data: null,
                error: "User not logged in.",
                status: 401,
            };
        }
        else if(input === -1){
            return {
                data: null,
                error: "Account does not belong to user.",
                status: 403,
            };

        }else{
            return {
                data: null,
                error: "Unimplemented error.",
                status: 501,
            };
        }
    };

    static async getAccountsByUsername(input: number): Promise<APIResponse<AccountList>> {
        await randomDelay();
        if(input === 1){
            return {
                data: MOCK_ACCOUNTS,
                error: null,
                status: 200,
            };
        }else if(input === 0) {
            return {
                data: null,
                error: "User not logged in.",
                status: 401,
            };
        }
        else if(input === -1){
            return {
                data: null,
                error: "Account does not belong to user.",
                status: 403,
            };
        }
        else{
            return {
                data: null,
                error: "Unimplemented error.",
                status: 501,
            };
        }
    };
}
