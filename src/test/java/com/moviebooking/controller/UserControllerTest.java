package com.moviebooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviebooking.entity.Customer;
import com.moviebooking.entity.User;
import com.moviebooking.service.ICustomerService;
import com.moviebooking.service.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("UserController")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserService userService;

    @MockBean
    private ICustomerService customerService;

    private ObjectMapper objectMapper;
    private User sampleUser;
    private String sampleEmail;
    private String samplePassword;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        sampleEmail = "jane.doe@example.com";
        samplePassword = "secret123";

        sampleUser = new User();
        sampleUser.setUserId(1);
        sampleUser.setPassword(samplePassword);
        sampleUser.setRole("ADMIN");
    }

    @Nested
    @DisplayName("POST /api/users/register")
    class Register {

        @Test
        @DisplayName("returns 201 with persisted user")
        void returnsCreated() throws Exception {
            when(userService.addNewUser(any(User.class))).thenReturn(sampleUser);

            mockMvc.perform(post("/api/users/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sampleUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(sampleUser.getUserId()))
                .andExpect(jsonPath("$.role").value(sampleUser.getRole()));
        }

        @Test
        @DisplayName("returns 400 when service throws")
        void returnsBadRequest() throws Exception {
            when(userService.addNewUser(any(User.class))).thenThrow(new RuntimeException("exists"));

            mockMvc.perform(post("/api/users/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sampleUser)))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/users/signin-legacy")
    class SignInLegacy {

        @Test
        @DisplayName("returns 200 with user payload")
        void returnsOk() throws Exception {
            when(userService.signIn(any(User.class))).thenReturn(sampleUser);

            mockMvc.perform(post("/api/users/signin-legacy")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sampleUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(sampleUser.getUserId()));
        }

        @Test
        @DisplayName("returns 401 on failure")
        void returnsUnauthorized() throws Exception {
            when(userService.signIn(any(User.class))).thenThrow(new RuntimeException("invalid"));

            mockMvc.perform(post("/api/users/signin-legacy")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sampleUser)))
                .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("POST /api/users/signin")
    class SignIn {

        @Test
        @DisplayName("returns 200 when credentials valid")
        void returnsOk() throws Exception {
            when(customerService.findByEmailAndPassword(eq(sampleEmail), eq(samplePassword)))
                .thenReturn(null);
            when(userService.signInByCredentials(eq(sampleEmail), eq(samplePassword)))
                .thenReturn(sampleUser);

            Map<String, String> body = Map.of(
                "email", sampleEmail,
                "password", samplePassword
            );

            mockMvc.perform(post("/api/users/signin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(sampleUser.getUserId()))
                .andExpect(jsonPath("$.role").value(sampleUser.getRole()));
        }

        @Test
        @DisplayName("returns customer payload when customer matches")
        void returnsCustomerPayload() throws Exception {
            Customer customer = new Customer();
            customer.setCustomerId(42);
            customer.setCustomerName("Jane Doe");
            customer.setEmail(sampleEmail);
            customer.setPassword(samplePassword);
            customer.setAddress("123 Main St");
            customer.setMobileNumber("5550001234");

            when(customerService.findByEmailAndPassword(eq(sampleEmail), eq(samplePassword)))
                .thenReturn(customer);

            Map<String, String> body = Map.of(
                "email", sampleEmail,
                "password", samplePassword
            );

            mockMvc.perform(post("/api/users/signin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(customer.getCustomerId()))
                .andExpect(jsonPath("$.name").value(customer.getCustomerName()))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
        }

        @Test
        @DisplayName("returns 401 when credentials invalid")
        void returnsUnauthorized() throws Exception {
            when(customerService.findByEmailAndPassword(eq(sampleEmail), eq(samplePassword)))
                .thenReturn(null);
            when(userService.signInByCredentials(eq(sampleEmail), eq(samplePassword)))
                .thenThrow(new RuntimeException("invalid"));

            Map<String, String> body = Map.of(
                "email", sampleEmail,
                "password", samplePassword
            );

            mockMvc.perform(post("/api/users/signin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());
        }
    }
}
