package org.csu.mypetstorebackend.controller.auth;

import org.csu.mypetstorebackend.common.ApiResponse;
import org.csu.mypetstorebackend.dto.LoginRequest;
import org.csu.mypetstorebackend.dto.LoginResponse;
import org.csu.mypetstorebackend.dto.RegisterRequest;
import org.csu.mypetstorebackend.entity.Account;
import org.csu.mypetstorebackend.service.AccountService;
import org.csu.mypetstorebackend.utils.JwtUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AccountService accountService;

    public AuthController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/sign-in")
    public ApiResponse<LoginResponse> signIn(@RequestBody LoginRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            return ApiResponse.badRequest("Username and password are required");
        }

        Account account = accountService.login(request.getUsername(), request.getPassword());
        if (account == null) {
            return ApiResponse.unauthorized("Username or password incorrect");
        }

        String token = JwtUtil.generateToken(account.getUsername(), account.getId());
        LoginResponse response = new LoginResponse(
                token,
                account.getId(),
                account.getUsername(),
                account.getEmail(),
                account.getFirstName(),
                account.getLastName(),
                account.getFavoriteCategory()
        );

        return ApiResponse.success("Login successful", response);
    }

    @PostMapping("/sign-up")
    public ApiResponse<Object> signUp(@RequestBody RegisterRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            return ApiResponse.badRequest("Username and password are required");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ApiResponse.badRequest("Passwords do not match");
        }

        Account account = accountService.register(request.getUsername(), request.getPassword());
        if (account == null) {
            return ApiResponse.badRequest("Username already exists");
        }

        return ApiResponse.created("Registration successful", new Object() {
            public int userId = account.getId();
            public String username = account.getUsername();
            public String email = account.getEmail();
            public String status = account.getStatus();
        });
    }

    @PostMapping("/sign-out")
    public ApiResponse<Object> signOut(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ApiResponse.unauthorized("Authentication required. Please sign in first.");
        }

        String actualToken = token.substring(7);
        if (!JwtUtil.validateToken(actualToken)) {
            return ApiResponse.unauthorized("Invalid token");
        }

        return ApiResponse.success("Logout successful", null);
    }
}

