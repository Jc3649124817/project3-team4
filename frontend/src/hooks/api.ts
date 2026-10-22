import { getToken } from "./authService";

const API_URL = "http://localhost:8080";

export const tokenHeader = () => ({
    "Content-Type": "application/json",
    Authorization: `Bearer ${getToken()}`,
});

