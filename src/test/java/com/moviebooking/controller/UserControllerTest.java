package com.moviebooking.controller;

import java.time.Instant;
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
import com.moviebooking.config.JwtTokenProvider;
import com.moviebooking.entity.Customer;
import com.moviebooking.entity.RefreshToken;
import com.moviebooking.entity.User;
import com.moviebooking.repository.ICustomerRepository;
import com.moviebooking.repository.IUserRepository;
import com.moviebooking.service.ICustomerService;
import com.moviebooking.service.IRefreshTokenService;
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

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private IRefreshTokenService refreshTokenService;

    @SuppressWarnings("unused")
    @MockBean
    private IUserRepository userRepository;

    @SuppressWarnings("unused")
    @MockBean
    private ICustomerRepository customerRepository;

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

    private RefreshToken buildRefreshToken(String subject, String role) {
        RefreshToken token = new RefreshToken();
        token.setToken("refresh-token-" + role.toLowerCase());
        token.setSubject(subject);
        token.setRole(role);
        token.setCreatedAt(Instant.now());
        token.setExpiresAt(Instant.now().plusSeconds(604800));
        token.setRevoked(false);
        return token;
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
        when(jwtTokenProvider.generateToken(eq(SAMPLE_EMAIL), eq(sampleUser.getRole()))).thenReturn("mock-token");
        when(jwtTokenProvider.getExpirationMillis()).thenReturn(3600_000L);
        when(refreshTokenService.createToken(eq(SAMPLE_EMAIL), eq(sampleUser.getRole()))).thenReturn(buildRefreshToken(SAMPLE_EMAIL, sampleUser.getRole()));
        when(refreshTokenService.getRefreshTokenValidityMillis()).thenReturn(604800_000L);

        mockMvc.perform(post("/api/users/signin-legacy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(sampleUser.getUserId()))
                .andExpect(jsonPath("$.token").value("mock-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token-admin"));
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
        when(jwtTokenProvider.generateToken(eq(SAMPLE_EMAIL), eq(sampleUser.getRole()))).thenReturn("mock-token");
        when(jwtTokenProvider.getExpirationMillis()).thenReturn(3600_000L);
        when(refreshTokenService.createToken(eq(SAMPLE_EMAIL), eq(sampleUser.getRole()))).thenReturn(buildRefreshToken(SAMPLE_EMAIL, sampleUser.getRole()));
        when(refreshTokenService.getRefreshTokenValidityMillis()).thenReturn(604800_000L);

        Map<String, String> body = Map.of(
                "email", SAMPLE_EMAIL,
                "password", SAMPLE_PASSWORD
        );

        mockMvc.perform(post("/api/users/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(sampleUser.getUserId()))
                .andExpect(jsonPath("$.role").value(sampleUser.getRole()))
                .andExpect(jsonPath("$.token").value("mock-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token-admin"));
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
        when(jwtTokenProvider.generateToken(eq(SAMPLE_EMAIL), eq("CUSTOMER"))).thenReturn("customer-token");
        when(jwtTokenProvider.getExpirationMillis()).thenReturn(3600_000L);
        when(refreshTokenService.createToken(eq(SAMPLE_EMAIL), eq("CUSTOMER"))).thenReturn(buildRefreshToken(SAMPLE_EMAIL, "CUSTOMER"));
        when(refreshTokenService.getRefreshTokenValidityMillis()).thenReturn(604800_000L);

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
                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andExpect(jsonPath("$.token").value("customer-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token-customer"));
    }

    @Test
    @DisplayName("POST /api/users/refresh returns new tokens when refresh token valid")
    void refreshReturnsNewTokens() throws Exception {
        RefreshToken rotated = buildRefreshToken(SAMPLE_EMAIL, "ADMIN");
        rotated.setToken("rotated-refresh-token");

        when(refreshTokenService.rotateToken(eq("existing-token"))).thenReturn(rotated);
        when(refreshTokenService.getRefreshTokenValidityMillis()).thenReturn(604800_000L);
        when(jwtTokenProvider.generateToken(eq(SAMPLE_EMAIL), eq("ADMIN"))).thenReturn("new-access");
        when(jwtTokenProvider.getExpirationMillis()).thenReturn(3600_000L);

        Map<String, String> body = Map.of("refreshToken", "existing-token");

        mockMvc.perform(post("/api/users/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("new-access"))
                .andExpect(jsonPath("$.refreshToken").value("rotated-refresh-token"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
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
