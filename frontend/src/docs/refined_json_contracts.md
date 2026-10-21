# API Endpoints

## 1. Register User

### `RegisterUser()`

Registers a new user account. On success, the new user's information is returned.

**Request Body:**

```json
{
  "FirstName": "John",
  "LastName": "Smith",
  "Email": "john.smith@example.com",
  "PhoneNumber": "201-123-4567",
  "Username": "johnsmith",
  "Password": "apple123",
  "Password2": "apple123"
}
```

**Success Response:**
```json
{
  "FirstName": "John",
  "LastName": "Smith",
  "Email": "john.smith@example.com",
  "PhoneNumber": "201-123-4567",
  "Username": "johnsmith"
}
```

**Responses:**

| Status            | Description                                                |
|-------------------|------------------------------------------------------------|
| `201 Created`     | User successfully registered. User information returned.   |
| `400 Bad Request` | Invalid email syntax or passwords do not match.            |

---

## 2. Log In User

### `LogInUser()`

Authenticates a user using their username and password.

**Request Body:**

```json
{
  "Username": "johnsmith",
  "Password": "apple123"
}
```
**Success Response:**

```json
{
  "FirstName": "John",
  "LastName": "Smith",
  "Email": "john.smith@example.com",
  "PhoneNumber": "201-123-4567",
  "Username": "johnsmith"
}
```

**Responses:**

| Status | Description                      |
|---|----------------------------------|
| `200 OK` | User successfully authenticated. |
| `401 Unauthorized` | Incorrect password.              |

---

## 3. Get User

### `GetUser()`

Retrieves information about the currently authenticated user.

**Request Body:**

No request body is required. The server identifies the user based on their authenticated session.

**Success Response:**

```json
{
  "FirstName": "John",
  "LastName": "Smith",
  "Email": "john.smith@example.com",
  "PhoneNumber": "201-123-4567",
  "Username": "johnsmith"
}
```


**Responses:**

| Status               | Description                              |
|----------------------|------------------------------------------|
| `200 OK`             | User information successfully retrieved. |
| `404 User Not Found` | User not found.                          |

---

## 4. Put Accounts

### `PutAccounts()`

Opens the accounts for the current user (a checking and a savings account) and returns the resulting account list.

**Request Body:**

No request body is required. The server identifies the user on registration and opens accounts automatically.

**Success Response:**

```json
{
  "Accounts": [
    {
      "AccountNumber": 1123456789,
      "AccountType": "Checking",
      "Balance": 1000.00
    },
    {
      "AccountNumber": 9876543211,
      "AccountType": "Savings",
      "Balance": 20000.00
    }
  ]
}
```

**Responses:**

| Status             | Description                                                   |
|--------------------|---------------------------------------------------------------|
| `201 Created`      | Account is successfully created. Account list returned.       |
| `401 Unauthorized` | User did not register successfully, account creation failed.  |


---

## 5. Get Accounts

### `GetAccountsByUsername()`

Retrieves all accounts belonging to the currently authenticated user.

**Request Body:**

No request body is required. The server identifies the user based on their authenticated session.

**Success Response:**

```json
{
  "Accounts": [
    {
      "AccountNumber": 1123456789,
      "AccountType": "Checking",
      "Balance": 1000.00
    },
    {
      "AccountNumber": 9876543211,
      "AccountType": "Savings",
      "Balance": 20000.00
    }
  ]
}
```

**Responses:**

| Status             | Description                      |
|--------------------|----------------------------------|
| `200 OK`           | Accounts successfully retrieved. |
| `401 Unauthorized` | User is not logged in.           |
| `403 Forbidden `   | Account doesn't belong to user.  |

---

## 6. Get Account By Account ID

### `GetAccountByAccountId()`

Retrieves information about a specific account. The user must be logged in.

**Request Body:**

```json

{
  "AccountNumber": 1123456789
}

```

**Success Response:**

```json
{
  "AccountNumber": 1123456789,
  "AccountType": "Checking",
  "Balance": 1000.00
}
```

**Responses:**

| Status             | Description                                  |
|--------------------|----------------------------------------------|
| `200 OK`           | Account information successfully retrieved.  |
| `401 Unauthorized` | User is not logged in.                       |
| `403 Forbidden `   | Account doesn't belong to user.              |

---

## 7. Update Account Balances
### `UpdateAccountBalances()`

Updates the accounts' balances based on a new transaction. The user must be logged in.

**Request Body:**

```json

{
  "TransactionType":  "Transfer",
  "Amount": 100.00,
  "OriginID": 1123456789,
  "DestinationID": 9876543211
}

```

**Success Response:**

```json
{
  "Accounts": [
    {
      "AccountNumber": 1123456789,
      "AccountType": "Checking",
      "Balance": 900.00
    },
    {
      "AccountNumber": 9876543211,
      "AccountType": "Savings",
      "Balance": 20100.00
    }
  ]
}
```

**Responses:**

| Status             | Description                                                    |
|--------------------|----------------------------------------------------------------|
| `200 OK`           | Accounts information successfully retrieved after transaction. |
| `400 Bad Request`  | Invalid amount or insufficient funds.                          |
| `401 Unauthorized` | User is not logged in.                                         |
| `403 Forbidden`    | Origin Account ID doesn't belong to the logged-in user.        |
---

## 8. Put Transaction

### `PutTransaction()`

Creates a new transaction for one account or between two accounts.

**Request Body:**

```json
{
  "TransactionType": "Transfer",
  "Amount": 500.00,
  "OriginID": 9876543211,
  "DestinationID": 1123456789
}
```
**Success Response:**

```json
{
  "Date": "10-05-2026",
  "TransactionType": "Transfer",
  "Amount": 500.00,
  "OriginID": 9876543211,
  "DestinationID": 1123456789,
  "ID": 6
}
```

**Responses:**

| Status             | Description |
|--------------------|---|
| `201 Created`      | Transaction successfully created. |
| `400 Bad Request`  | Invalid amount or insufficient funds. |
| `401 Unauthorized` | User is not logged in. |
| `403 Forbidden` | Origin Account ID doesn't belong to the logged-in user. |

---

## 9. Get Transactions

### `GetTransactions()`

Retrieves a specified number of recent transactions. Default value is 5. If the input is higher than number of transactions stored, all transactions are returned.
The user must be logged in.

**Input:**
Just a numerical parameter, ie. numTransactions = 5

**Success Response:**

```json
{
  "Transactions": [
  
        {"Date": "10-05-2026", "TransactionType": "Transfer", "Amount": 500.00, "OriginID": 9876543211, "DestinationID": 1123456789, "ID" : 5},
        {"Date": "10-04-2026", "TransactionType": "Transfer", "Amount": 500.00, "OriginID": 9876543211, "DestinationID": 1123456789, "ID" : 4},
        {"Date": "10-03-2026", "TransactionType": "Withdraw", "Amount": 1000.00, "OriginID": 1123456789, "DestinationID": null, "ID" : 3},
        {"Date": "10-02-2026", "TransactionType": "Deposit", "Amount": 1000.00, "OriginID": null, "DestinationID": 1123456789, "ID" : 2},
        {"Date": "10-01-2026", "TransactionType": "Deposit", "Amount": 21000.00, "OriginID": null, "DestinationID": 9876543211, "ID" : 1}
        
    ]
}
```

**Responses:**

| Status | Description                          |
|---|--------------------------------------|
| `200 OK` | Transactions successfully retrieved. |
| `401 Unauthorized` | User is not logged in.               |
| `403 Forbidden `   | Transactions don't belong to user.   |


## 10. Update User

### `UpdateUser()`

Updates the information of the currently authenticated user. Users can update their first name, last name, phone number, email, and password. Only the fields provided for update are changed; omitted fields remain unchanged.

**Request Body:**

```json
{
  "FirstName": "John",
  "LastName": "Smith",
  "Email": "john.smith@example.com",
  "PhoneNumber": "201-123-4567",
  "Password": "newPassword123",
  "Password2": "newPassword123"
}
```

At least one field must be provided. `Password2` is required when changing the password and must match `Password`.

**Success Response:**

```json
{
  "FirstName": "John",
  "LastName": "Smith",
  "Email": "john.smith@example.com",
  "PhoneNumber": "201-123-4567",
  "Username": "johnsmith"
}
```

**Responses:**

| Status | Description |
|---|---|
| `200 OK` | User information successfully updated. |
| `400 Bad Request` | Invalid email syntax, passwords do not match, or no valid fields provided. |
| `401 Unauthorized` | User is not logged in. |
| `404 Not Found` | User not found. |
| `409 Conflict` | Email address is already in use by another user. |

---

---

# Page-to-API Mapping

## Register Page

**Required API Calls:**

- `RegisterUser()`

**Purpose:**

Creates a new user account.

---

## Login Page

**Required API Calls:**

- `LogInUser()`
- `GetUser()`

**Purpose:**

Authenticates the user and retrieves their user information after a successful login.

---

## Main Page

**Required API Calls:**

- `GetUser()`
- `GetAccountsByUsername()`

**Information Displayed:**

- User information
- Checking account information
- Savings account information
- Account balances

---

## Transaction Page

**Required API Calls:**

- `GetUser()`
- `GetAccountsByUsername()`
- `UpdateAccountBalances()`

**Information Displayed:**

- User information
- Checking account information
- Savings account information

**Actions:**

- Create a new transaction

---

## Transaction History Page

**Required API Calls:**

- `GetAccountsByUsername()`
- `GetTransactions()`

**Information Displayed:**

- Account transactions
- Transaction date
- Transaction type
- Transaction amount
- Origin account
- Destination account

---

# Status Code Summary

| Status Code        | Meaning                           | Usage                                                              |
|--------------------|-----------------------------------|--------------------------------------------------------------------|
| `200 OK`           | Request succeeded                 | Successful login and data retrieval                                |
| `201 Created`      | Resource successfully created     | User, account, and transaction creation                            |
| `400 Bad Request`  | Invalid request                   | Invalid email, password mismatch, or invalid amount                |
| `401 Unauthorized` | Authentication required or failed | Incorrect password, or user not logged in                          |
| `403 Forbidden`    | Authenticated but not allowed     | Account, transactions, or origin account belong to another user    |
| `404 Not Found`   | Resource does not exist           | User not found                                                     |
| `501 Not Implemented` | Scenario not handled           | Returned by the mock layer for any unrecognized input              |

## Edit Profile Page

**Required API Calls:**

- `GetUser()`
- `UpdateUser()`

**Purpose:**

Retrieves the current user's information and allows the user to update their profile information and password.

**Information Displayed:**

- First name
- Last name
- Email
- Phone number

**Actions:**

- Update first name
- Update last name
- Update phone number
- Update email
- Change password

---

# API Endpoint Summary

| Endpoint                  | Mock Service Method                    | Purpose                                                     |
|---------------------------|----------------------------------------|-------------------------------------------------------------|
| `RegisterUser()`          | `UserService.register`                 | Register a new user                                         |
| `LogInUser()`             | `UserService.logIn`                    | Authenticate a user                                         |
| `GetUser()`               | `UserService.getUser`                  | Retrieve the logged-in user's information                   |
| `PutAccounts()`           | `AccountService.putAccounts`           | Create the user's checking and savings accounts             |
| `GetAccountsByUsername()` | `AccountService.getAccountsByUsername` | Retrieve all accounts belonging to the logged-in user       |
| `GetAccountByAccountId()` | `AccountService.getAccountByAccountId` | Retrieve a specific account                                 |
| `UpdateAccountBalances()` | `AccountService.updateAccountBalances` | Updates balances after executing transaction                |
| `PutTransaction()`        | `TransactionService.putTransaction`    | Create a new transaction, only used internally              |
| `GetTransactions()`       | `TransactionService.getTransactions`   | Retrieve the logged-in user's transactions                  |
| `UpdateUser()`            | `UserService.updateUser`               | Update the logged-in user's profile information or password |
---

# Mock Scenario Codes

The mock service layer takes a numeric `input` that selects which response to return. Any value not listed returns `501` with `"Unimplemented error."`.

| Method                  | Input | Status | Result                                         |
|-------------------------|-------|--------|------------------------------------------------|
| `register`              | `1`   | 201    | User returned                                  |
|                         | `0`   | 400    | Passwords do not match                         |
|                         | `-1`  | 400    | Invalid email address                          |
| `logIn`                 | `1`   | 200    | User returned                                  |
|                         | `0`   | 401    | Password incorrect                             |
| `getUser`               | `1`   | 200    | User returned                                  |
|                         | `0`   | 404    | User not found                                 |
| `putAccounts`           | `1`   | 201    | Account list returned                          |
|                         | `0`   | 401    | User did not register successfully             |
| `getAccountsByUsername` | `1`   | 200    | Account list returned                          |
|                         | `0`   | 401    | User not logged in                             |
|                         | `-1`  | 403    | Account does not belong to user                |
| `getAccountByAccountId` | `1`   | 200    | Account returned                               |
|                         | `0`   | 401    | User not logged in                             |
|                         | `-1`  | 403    | Account does not belong to user                |
| `UpdateAccountBalances` | `1`   | 201    | Transaction returned                           |
|                         | `0`   | 400    | Invalid amount                                 |
|                         | `-1`  | 401    | User is not logged in                          |
|                         | `-2`  | 401    | Account Id doesn't belong to user              |
| `getTransactions`       | `1`   | 200    | Transaction list returned                      |
|                         | `0`   | 401    | User is not authenticated                      |
|                         | `-1`  | 403    | Transactions belong to another user or account |
| `updateUser`            | `1`   | 200    | User information successfully updated          |
|                         | `0`   | 400    | Invalid email or passwords do not match        |
|                         |       | 401    | User is not authenticated                      |
|                         |       | 404    | User not found                                 |
|                         |       | 409    | Email address already in use                   |