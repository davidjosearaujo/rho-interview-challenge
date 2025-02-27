package com.example.rho_interview_challenge.controller;

import com.example.rho_interview_challenge.securityOAuthConfig.TokenGenerator;
import com.example.rho_interview_challenge.userDocument.User;
import com.example.rho_interview_challenge.userModel.Login;
import com.example.rho_interview_challenge.userModel.SignUp;
import com.example.rho_interview_challenge.userModel.Token;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

import java.time.Instant;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth Service", description = "Provides registration and login services")
public class AuthController {
    
    @Autowired
    UserDetailsManager userDetailsManager;
    @Autowired
    TokenGenerator tokenGenerator;
    @Autowired
    DaoAuthenticationProvider daoAuthenticationProvider;
    @Autowired
    @Qualifier("jwtRefreshTokenAuthProvider")
    JwtAuthenticationProvider refreshTokenAuthProvider;

    @RateLimiter(name = "exchangeRateLimiter")
	@Operation(summary = "Register new user", description = "Registers a new user")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "User registered successfully"),
		@ApiResponse(responseCode = "404", description = "Could not register user")
	})
	@QueryMapping
    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody SignUp signupDTO) {
        User user = new User(signupDTO.getUserName(), signupDTO.getPassword());
        userDetailsManager.createUser(user);

        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(user, signupDTO.getPassword(),
                Collections.emptyList());

        return ResponseEntity.ok(tokenGenerator.createToken(authentication));
    }

    @RateLimiter(name = "exchangeRateLimiter")
    @Operation(summary = "Login user", description = "Login a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User successfully logged in"),
            @ApiResponse(responseCode = "404", description = "Could not login user")
    })
    @QueryMapping
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Login loginDTO) {
        Authentication authentication = daoAuthenticationProvider.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(loginDTO.getUserName(), loginDTO.getPassword()));

        return ResponseEntity.ok(tokenGenerator.createToken(authentication));
    }

    @RateLimiter(name = "exchangeRateLimiter")
    @Operation(summary = "Refresh token", description = "Refresh token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "404", description = "Could not refresh token")
    })
    @QueryMapping
    @PostMapping("/token")
    public ResponseEntity<Object> token(@RequestBody Token tokenDTO) {
        Authentication authentication = refreshTokenAuthProvider
                .authenticate(new BearerTokenAuthenticationToken(tokenDTO.getRefreshToken()));
        Jwt jwt = (Jwt) authentication.getCredentials();
        if (jwt.getExpiresAt() != null && !jwt.getExpiresAt().isBefore(Instant.now())) {
            return ResponseEntity.ok(tokenDTO);
        }
        return ResponseEntity.ok(tokenGenerator.createToken(authentication));
    }
}
