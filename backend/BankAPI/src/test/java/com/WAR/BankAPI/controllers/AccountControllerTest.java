package com.WAR.BankAPI.controllers;

import com.WAR.BankAPI.models.Account;
import com.WAR.BankAPI.models.AccountType;
import com.WAR.BankAPI.models.User;
import com.WAR.BankAPI.services.AccountService;
import com.WAR.BankAPI.services.UserService;
import org.junit.jupiter.api.Test;
import com.WAR.BankAPI.jwt.JwtFilter;
import com.WAR.BankAPI.jwt.JwtUtil;
import com.WAR.BankAPI.jwt.SecurityConfig;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@Import({SecurityConfig.class, JwtFilter.class, JwtUtil.class})
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void acceptsASignedJwtAndLooksUpItsUsername() throws Exception {
        User owner = signedInUser();
        when(accountService.getAccountsByUser(owner)).thenReturn(List.of());
        String token = jwtUtil.generateToken("alice", "user", 1);

        mockMvc.perform(get("/accounts").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.Accounts").isEmpty());
        verify(accountService).getAccountsByUser(owner);
    }

    private User signedInUser() {
        User owner = new User();
        owner.setUsername("alice");
        when(userService.findByUsername("alice")).thenReturn(Optional.of(owner));
        return owner;
    }

    private Account checkingAccount() {
        Account account = mock(Account.class);
        when(account.getAccountNumber()).thenReturn(10L);
        when(account.getAccountType()).thenReturn(AccountType.CHECKING);
        when(account.getBalance()).thenReturn(new BigDecimal("125.50"));
        return account;
    }

    @Test
    void rejectsUnauthenticatedRequests() throws Exception {
        mockMvc.perform(get("/accounts").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError());
        verifyNoInteractions(accountService);
    }

    @Test
    void returnsOnlyTheSignedInUsersAccounts() throws Exception {
        User owner = signedInUser();
        Account account = checkingAccount();
        when(accountService.getAccountsByUser(owner))
            .thenReturn(List.of(account));

        mockMvc.perform(get("/accounts").with(user("alice")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.Accounts.length()").value(1))
            .andExpect(jsonPath("$.Accounts[0].AccountNumber").value(10))
            .andExpect(jsonPath("$.Accounts[0].AccountType").value("Checking"))
            .andExpect(jsonPath("$.Accounts[0].Balance").value(125.50))
            .andExpect(jsonPath("$.Accounts[0].user").doesNotExist());

        verify(accountService).getAccountsByUser(owner);
    }

    @Test
    void returnsAnEmptyAccountList() throws Exception {
        User owner = signedInUser();
        when(accountService.getAccountsByUser(owner)).thenReturn(List.of());

        mockMvc.perform(get("/accounts").with(user("alice")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.Accounts").isEmpty());
    }

    @Test
    void rejectsAnAuthenticatedNameMissingFromTheDatabase() throws Exception {
        when(userService.findByUsername("alice")).thenReturn(Optional.empty());

        mockMvc.perform(get("/accounts").with(user("alice")))
            .andExpect(status().is4xxClientError());
        verifyNoInteractions(accountService);
    }

    @Test
    void returnsAnOwnedAccount() throws Exception {
        User owner = signedInUser();
        Account account = checkingAccount();
        when(accountService.getAccountForUser(10L, owner))
            .thenReturn(account);

        mockMvc.perform(get("/accounts/10").with(user("alice")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.AccountNumber").value(10));

        verify(accountService).getAccountForUser(10L, owner);
        verify(accountService, never()).getAccountByAccountNumber(anyLong());
    }

    @Test
    void rejectsAnUnavailableAccount() throws Exception {
        User owner = signedInUser();
        when(accountService.getAccountForUser(99L, owner))
            .thenThrow(new IllegalArgumentException("Account not found for this user."));

        mockMvc.perform(get("/accounts/99").with(user("alice")))
            .andExpect(status().isForbidden());
    }

    @Test
    void rejectsANonpositiveAccountNumber() throws Exception {
        signedInUser();

        mockMvc.perform(get("/accounts/0").with(user("alice")))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(accountService);
    }

    @Test
    void rejectsANonnumericAccountNumber() throws Exception {
        mockMvc.perform(get("/accounts/abc").with(user("alice")))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(accountService);
    }
}
