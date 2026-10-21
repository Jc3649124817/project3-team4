import type {User, APIResponse} from '../models/models.ts';

const delay = (ms: number) => new Promise<void>((r) => setTimeout(r, ms));
const randomDelay = (min = 500, max = 800) =>
    delay(Math.floor(Math.random() * (max - min + 1)) + min);

export const MOCK_USER: User =
    {
        "FirstName": "John",
        "LastName": "Smith",
        "Email": "john.smith@example.com",
        "PhoneNumber": "201-123-4567",
        "Username": "johnsmith"
    };

export class UserService {
    static async register(input: number ): Promise<APIResponse<User>> {
        await randomDelay();
        if(input === 1){
            return {
                data: MOCK_USER,
                error: null,
                status: 201,
            }
        }else if(input === 0){
            return {
                data: null,
                error: "Failed User Registration: Passwords do not match.",
                status: 400,
            };
        }else if(input === -1){
            return {
                data: null,
                error: "Failed User Registration: Invalid email address.",
                status: 400,
            };
        }else{
            return {
                data: null,
                error: "Unimplemented error.",
                status: 501,
            };
        }

    };

    static async logIn(input: number): Promise<APIResponse<User>> {
        await randomDelay();
        if(input === 1) {
            return {
                data: MOCK_USER,
                error: null,
                status: 200
            };
        }else if(input === 0){
            return {
                data: null,
                error: "Failed Log In: Password Incorrect.",
                status: 401,
            };
        }else{
            return {
                data: null,
                error: "Unimplemented error.",
                status: 501,
            };
        }
    };

    static async getUser(input: number): Promise<APIResponse<User>> {
        await randomDelay();
        if(input === 1){
            return {
                data: MOCK_USER,
                error: null,
                status: 200,
            };
        }
        else if(input === 0){
            return {
                data: null,
                error: "User not found.",
                status: 404,
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
