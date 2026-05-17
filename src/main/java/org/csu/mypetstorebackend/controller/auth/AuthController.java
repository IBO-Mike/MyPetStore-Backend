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
    private static final int MAX_PASSWORD_LENGTH = 25;

    public AuthController(AccountService accountService) {
        this.accountService = accountService;
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private ApiResponse<Object> validateRequired(String label, String value) {
        if (isBlank(value)) {
            return ApiResponse.badRequest(label + " is required");
        }
        return null;
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
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()
                || request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return ApiResponse.badRequest("Username and password are required");
        }

        if (request.getConfirmPassword() == null || !request.getPassword().equals(request.getConfirmPassword())) {
            return ApiResponse.badRequest("Passwords do not match");
        }

        String password = request.getPassword().trim();
        if (password.length() > MAX_PASSWORD_LENGTH) {
            return ApiResponse.badRequest("Password must be no more than 25 characters");
        }

        ApiResponse<Object> requiredError;
        requiredError = validateRequired("Email", request.getEmail());
        if (requiredError != null) return requiredError;
        requiredError = validateRequired("First name", request.getFirstName());
        if (requiredError != null) return requiredError;
        requiredError = validateRequired("Last name", request.getLastName());
        if (requiredError != null) return requiredError;
        requiredError = validateRequired("Phone", request.getPhone());
        if (requiredError != null) return requiredError;
        requiredError = validateRequired("Address", request.getAddress1());
        if (requiredError != null) return requiredError;
        requiredError = validateRequired("City", request.getCity());
        if (requiredError != null) return requiredError;
        requiredError = validateRequired("State", request.getState());
        if (requiredError != null) return requiredError;
        requiredError = validateRequired("Zip", request.getZip());
        if (requiredError != null) return requiredError;
        requiredError = validateRequired("Country", request.getCountry());
        if (requiredError != null) return requiredError;

        Account newAccount = new Account();
        newAccount.setUsername(request.getUsername().trim());
        newAccount.setPassword(password);
        newAccount.setEmail(trim(request.getEmail()));
        newAccount.setFirstName(trim(request.getFirstName()));
        newAccount.setLastName(trim(request.getLastName()));
        newAccount.setPhone(trim(request.getPhone()));
        newAccount.setAddress1(trim(request.getAddress1()));
        newAccount.setAddress2(trim(request.getAddress2()) == null ? "" : trim(request.getAddress2()));
        newAccount.setCity(trim(request.getCity()));
        newAccount.setState(trim(request.getState()));
        newAccount.setZip(trim(request.getZip()));
        newAccount.setCountry(trim(request.getCountry()));
        newAccount.setLanguagePrefer(isBlank(request.getLanguagePreference()) ? "English" : trim(request.getLanguagePreference()));
        newAccount.setFavoriteCategory(isBlank(request.getFavoriteCategory()) ? "DOGS" : trim(request.getFavoriteCategory()));

        Account account = accountService.register(newAccount);
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
