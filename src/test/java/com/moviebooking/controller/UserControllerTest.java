package com.moviebooking.controller;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviebooking.entity.Customer;
import com.moviebooking.entity.User;
import com.moviebooking.service.ICustomerService;
import com.moviebooking.service.IUserService;

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

    private static final String SAMPLE_EMAIL = "jane.doe@example.com";
    private static final String SAMPLE_PASSWORD = "secret123";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User buildSampleAdmin() {
        User admin = new User();
        admin.setUserId(1);
        admin.setPassword(SAMPLE_PASSWORD);
        admin.setRole("ADMIN");
        admin.setEmail(SAMPLE_EMAIL);
        return admin;
    }

    @Test
    @DisplayName("POST /api/users/register returns 201 with persisted user")
    void registerReturnsCreated() throws Exception {
        User sampleUser = buildSampleAdmin();
        when(userService.addNewUser(any(User.class))).thenReturn(sampleUser);

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(sampleUser.getUserId()))
                .andExpect(jsonPath("$.role").value(sampleUser.getRole()))
                .andExpect(jsonPath("$.email").value(sampleUser.getEmail()));
    }

    @Test
    @DisplayName("POST /api/users/register returns 400 when service throws")
    void registerReturnsBadRequest() throws Exception {
        User sampleUser = buildSampleAdmin();
        when(userService.addNewUser(any(User.class))).thenThrow(new RuntimeException("exists"));

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/users/signin-legacy returns 200 with user payload")
    void signInLegacyReturnsOk() throws Exception {
        User sampleUser = buildSampleAdmin();
        when(userService.signIn(any(User.class))).thenReturn(sampleUser);

        mockMvc.perform(post("/api/users/signin-legacy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(sampleUser.getUserId()));
    }

    @Test
    @DisplayName("POST /api/users/signin-legacy returns 401 on failure")
    void signInLegacyReturnsUnauthorized() throws Exception {
        User sampleUser = buildSampleAdmin();
        when(userService.signIn(any(User.class))).thenThrow(new RuntimeException("invalid"));

        mockMvc.perform(post("/api/users/signin-legacy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleUser)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/users/signin returns 200 when credentials valid")
    void signInReturnsOk() throws Exception {
        User sampleUser = buildSampleAdmin();
        when(customerService.findByEmailAndPassword(eq(SAMPLE_EMAIL), eq(SAMPLE_PASSWORD)))
                .thenReturn(null);
        when(userService.signInByCredentials(eq(SAMPLE_EMAIL), eq(SAMPLE_PASSWORD)))
                .thenReturn(sampleUser);

        Map<String, String> body = Map.of(
                "email", SAMPLE_EMAIL,
                "password", SAMPLE_PASSWORD
        );

        mockMvc.perform(post("/api/users/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(sampleUser.getUserId()))
                .andExpect(jsonPath("$.role").value(sampleUser.getRole()));
    }

    @Test
    @DisplayName("POST /api/users/signin returns customer payload when customer matches")
    void signInReturnsCustomerPayload() throws Exception {
        Customer customer = new Customer();
        customer.setCustomerId(42);
        customer.setCustomerName("Jane Doe");
        customer.setEmail(SAMPLE_EMAIL);
        customer.setPassword(SAMPLE_PASSWORD);
        customer.setAddress("123 Main St");
        customer.setMobileNumber("5550001234");

        when(customerService.findByEmailAndPassword(eq(SAMPLE_EMAIL), eq(SAMPLE_PASSWORD)))
                .thenReturn(customer);

        Map<String, String> body = Map.of(
                "email", SAMPLE_EMAIL,
                "password", SAMPLE_PASSWORD
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
    @DisplayName("POST /api/users/signin returns 401 when credentials invalid")
    void signInReturnsUnauthorized() throws Exception {
        when(customerService.findByEmailAndPassword(eq(SAMPLE_EMAIL), eq(SAMPLE_PASSWORD)))
                .thenReturn(null);
        when(userService.signInByCredentials(eq(SAMPLE_EMAIL), eq(SAMPLE_PASSWORD)))
                .thenThrow(new RuntimeException("invalid"));

        Map<String, String> body = Map.of(
                "email", SAMPLE_EMAIL,
                "password", SAMPLE_PASSWORD
        );

        mockMvc.perform(post("/api/users/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());
    }
}
