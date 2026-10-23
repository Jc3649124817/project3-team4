package com.WAR.BankAPI.controllers;

import com.WAR.BankAPI.jwt.JwtFilter;
import com.WAR.BankAPI.jwt.JwtUtil;
import com.WAR.BankAPI.jwt.SecurityConfig;
import com.WAR.BankAPI.models.User;
import com.WAR.BankAPI.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CurrentUserController.class)
@Import({SecurityConfig.class, JwtFilter.class, JwtUtil.class})
class CurrentUserControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private JwtUtil jwtUtil;
    @MockitoBean private UserService userService;

    @Test
    void rejectsMissingToken() throws Exception {
        mockMvc.perform(get("/auth/me")).andExpect(status().isUnauthorized());
        verifyNoInteractions(userService);
    }

    @Test
    void rejectsInvalidToken() throws Exception {
        mockMvc.perform(get("/auth/me").header("Authorization", "Bearer invalid"))
            .andExpect(status().isUnauthorized());
        verifyNoInteractions(userService);
    }

    @Test
    void returnsTheActualUsersPublicProfile() throws Exception {
        User owner = new User("Alice", "Test", "alice@example.com",
            "5550000000", "alice", "not-returned");
        when(userService.findByUsername("alice")).thenReturn(Optional.of(owner));
        String token = jwtUtil.generateToken("alice", "user", 1);
        mockMvc.perform(get("/auth/me").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.Username").value("alice"))
            .andExpect(jsonPath("$.FirstName").value("Alice"))
            .andExpect(jsonPath("$.Password").doesNotExist())
            .andExpect(jsonPath("$.password").doesNotExist());
    }
}
