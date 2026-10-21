import { UserService } from "../service/UserService.ts";
import type {
    APIResponse, ChangePasswordRequest,
    LoginRequest,
    RegisterRequest, UpdateProfileRequest,
    User,
} from "../models/models.ts";

// The auth service is the one place that talks to UserService. It returns the
// APIResponse as-is, so callers check `data` / `error` / `status` directly.
//
// --- MOCK ADAPTERS ---------------------------------------------------------
// UserService picks its response from a number, so the credential checks below
// translate real input into that number. When UserService takes real
// arguments, delete the checks and forward the request objects instead.
// ---------------------------------------------------------------------------

export function login({ Username, Password }: LoginRequest): Promise<APIResponse<User>> {
    const valid = Username.trim() === "johnsmith" && Password === "apple123";
    return UserService.logIn(valid ? 1 : 0);
}

export function register(request: RegisterRequest): Promise<APIResponse<User>> {
    if (request.Password !== request.Password2) return UserService.register(0); // passwords differ
    if (!request.Email.includes("@")) return UserService.register(-1);          // invalid email
    return UserService.register(1);
}

export function changePassword(request: ChangePasswordRequest): Promise<APIResponse<null>> {
    if (request.CurrentPassword !== "apple123") {
        return UserService.changePassword(0);
    }
    return UserService.changePassword(1);
}

export function updateProfile (
    currentUser: User,
    changes: UpdateProfileRequest,
    ): Promise<APIResponse<User>> {
        const { FirstName, LastName, Email, PhoneNumber } = changes;

        const badFirstName = FirstName !== undefined && FirstName.trim() === "";
        const badLastName = LastName !== undefined && LastName.trim() === "";
        const badEmail = Email !== undefined && !Email.includes("@");
        const badPhoneNumber = PhoneNumber !== undefined && PhoneNumber.trim() === "";

        const invalid = badFirstName || badLastName || badEmail || badPhoneNumber;

        return UserService.updateUser(invalid ? 0 : 1, currentUser, changes);
}

/** Checks whether the previously signed-in user still exists. */
export function getCurrentUser(storedUsername: string): Promise<APIResponse<User>> {
    return UserService.getUser(storedUsername ? 1 : 0);
}

export async function logout(): Promise<void> {
    // No UserService.logout yet. A real backend would invalidate the session here.
}