package com.moviebooking.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moviebooking.config.JwtTokenProvider;
import com.moviebooking.entity.Customer;
import com.moviebooking.entity.RefreshToken;
import com.moviebooking.entity.User;
import com.moviebooking.exception.RefreshTokenException;
import com.moviebooking.service.ICustomerService;
import com.moviebooking.service.IRefreshTokenService;
import com.moviebooking.service.IUserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private IRefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> addNewUser(@RequestBody User user) {
        try {
            User newUser = userService.addNewUser(user);
            Map<String, Object> response = new HashMap<>();
            response.put("userId", newUser.getUserId());
            response.put("email", newUser.getEmail());
            response.put("role", newUser.getRole());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<Map<String, Object>> signIn(@RequestBody Map<String, String> loginRequest) {
        try {
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");

            if (email == null || password == null) {
                return new ResponseEntity<>(Map.of("error", "Email and password are required"), HttpStatus.BAD_REQUEST);
            }

            // Try to authenticate as customer first (most common case)
            try {
                Customer customer = customerService.findByEmailAndPassword(email, password);
                if (customer != null) {
                    Map<String, Object> response = buildAuthenticationPayload(
                            customer.getCustomerId(),
                            customer.getCustomerName(),
                            customer.getEmail(),
                            "CUSTOMER"
                    );
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            } catch (RuntimeException e) {
                // Continue to check admin/user table when customer lookup fails
            }

            // If not found as customer, try the user table for admin
            User authenticatedUser = userService.signInByCredentials(email, password);
            Map<String, Object> response = buildAuthenticationPayload(
                    authenticatedUser.getUserId(),
                    null,
                    email,
                    authenticatedUser.getRole()
            );
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("error", "Invalid email or password"), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/signin-legacy")
    public ResponseEntity<Map<String, Object>> signInLegacy(@RequestBody User user) {
        try {
            User authenticatedUser = userService.signIn(user);
            Map<String, Object> response = buildAuthenticationPayload(
                    authenticatedUser.getUserId(),
                    null,
                    authenticatedUser.getEmail(),
                    authenticatedUser.getRole()
            );

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("error", "Invalid credentials"), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/signout")
    public ResponseEntity<Map<String, Object>> signOut(@RequestBody User user) {
        try {
            User signedOutUser = userService.signOut(user);
            if (signedOutUser != null && signedOutUser.getEmail() != null) {
                refreshTokenService.revokeTokensForSubject(signedOutUser.getEmail());
            }
            return new ResponseEntity<>(Map.of(
                    "userId", signedOutUser.getUserId(),
                    "email", signedOutUser.getEmail(),
                    "role", signedOutUser.getRole(),
                    "success", true
            ), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(@RequestBody Map<String, String> request) {
        String refreshTokenValue = request != null ? request.get("refreshToken") : null;

        if (refreshTokenValue == null || refreshTokenValue.isBlank()) {
            return new ResponseEntity<>(Map.of("error", "Refresh token is required"), HttpStatus.BAD_REQUEST);
        }

        try {
            RefreshToken rotatedToken = refreshTokenService.rotateToken(refreshTokenValue);
            String accessToken = jwtTokenProvider.generateToken(rotatedToken.getSubject(), rotatedToken.getRole());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("token", accessToken);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", jwtTokenProvider.getExpirationMillis());
            response.put("expiresAt", System.currentTimeMillis() + jwtTokenProvider.getExpirationMillis());
            response.put("refreshToken", rotatedToken.getToken());
            response.put("refreshTokenExpiresIn", refreshTokenService.getRefreshTokenValidityMillis());
            response.put("refreshTokenExpiresAt", rotatedToken.getExpiresAt().toEpochMilli());
            response.put("email", rotatedToken.getSubject());
            response.put("role", rotatedToken.getRole());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RefreshTokenException ex) {
            refreshTokenService.revokeToken(refreshTokenValue);
            return new ResponseEntity<>(Map.of("error", ex.getMessage()), HttpStatus.UNAUTHORIZED);
        }
    }

    private Map<String, Object> buildAuthenticationPayload(int userId,
            String displayName,
            String email,
            String role) {
        refreshTokenService.revokeTokensForSubject(email);

        String normalizedRole = role != null ? role.toUpperCase() : "USER";
        String token = jwtTokenProvider.generateToken(email, normalizedRole);
        RefreshToken refreshToken = refreshTokenService.createToken(email, normalizedRole);

        long accessTokenTtl = jwtTokenProvider.getExpirationMillis();
        long refreshTokenTtl = refreshTokenService.getRefreshTokenValidityMillis();

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("email", email);
        response.put("role", normalizedRole);
        response.put("success", true);
        response.put("token", token);
        response.put("tokenType", "Bearer");
        response.put("expiresIn", accessTokenTtl);
        response.put("expiresAt", System.currentTimeMillis() + accessTokenTtl);
        response.put("refreshToken", refreshToken.getToken());
        response.put("refreshTokenExpiresIn", refreshTokenTtl);
        response.put("refreshTokenExpiresAt", refreshToken.getExpiresAt().toEpochMilli());

        if (displayName != null && !displayName.isBlank()) {
            response.put("name", displayName);
        }

        return response;
    }
}
