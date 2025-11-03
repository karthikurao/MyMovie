package com.moviebooking.config;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.moviebooking.entity.Customer;
import com.moviebooking.entity.User;
import com.moviebooking.repository.ICustomerRepository;
import com.moviebooking.repository.IUserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider tokenProvider;
    private final IUserRepository userRepository;
    private final ICustomerRepository customerRepository;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider,
            IUserRepository userRepository,
            ICustomerRepository customerRepository) {
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String jwt = resolveToken(request);

        if (jwt != null && tokenProvider.validateToken(jwt)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            String email = tokenProvider.getSubject(jwt);
            String role = tokenProvider.getRole(jwt);

            Optional<UserDetails> userDetails = loadUserDetails(email, role);

            if (userDetails.isPresent()) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails.get(),
                        null,
                        userDetails.get().getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                LOGGER.debug("JWT resolved for email {} but no matching user found", email);
            }
        }

        filterChain.doFilter(request, response);
    }

    private Optional<UserDetails> loadUserDetails(String email, String role) {
        if (email == null) {
            return Optional.empty();
        }

        if (role != null && role.equalsIgnoreCase("CUSTOMER")) {
            return customerRepository.findByEmail(email)
                    .map(this::mapCustomerToUserDetails);
        }

        return userRepository.findByEmail(email)
                .map(this::mapUserToUserDetails)
                .or(() -> customerRepository.findByEmail(email).map(this::mapCustomerToUserDetails));
    }

    private UserDetails mapUserToUserDetails(User user) {
        String role = user.getRole() != null ? user.getRole().toUpperCase() : "USER";
        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword() != null ? user.getPassword() : "")
                .authorities(buildAuthority(role))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    private UserDetails mapCustomerToUserDetails(Customer customer) {
        return org.springframework.security.core.userdetails.User.withUsername(customer.getEmail())
                .password(customer.getPassword() != null ? customer.getPassword() : "")
                .authorities(buildAuthority("CUSTOMER"))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    private Collection<? extends GrantedAuthority> buildAuthority(String role) {
        String authority = "ROLE_" + (role != null ? role.toUpperCase() : "USER");
        return Collections.singletonList(new SimpleGrantedAuthority(authority));
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
