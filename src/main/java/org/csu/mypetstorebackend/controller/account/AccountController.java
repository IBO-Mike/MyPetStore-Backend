package org.csu.mypetstorebackend.controller.account;

import org.csu.mypetstorebackend.common.ApiResponse;
import org.csu.mypetstorebackend.dto.ChangePasswordRequest;
import org.csu.mypetstorebackend.dto.UpdateProfileRequest;
import org.csu.mypetstorebackend.entity.Account;
import org.csu.mypetstorebackend.service.AccountService;
import org.csu.mypetstorebackend.utils.JwtUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    private String extractUsernameFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        String actualToken = token.substring(7);
        if (!JwtUtil.validateToken(actualToken)) {
            return null;
        }
        return JwtUtil.extractUsername(actualToken);
    }

    @GetMapping("/profile")
    public ApiResponse<Account> getProfile(@RequestHeader(value = "Authorization", required = false) String token) {
        String username = extractUsernameFromToken(token);
        if (username == null) {
            return ApiResponse.unauthorized("Authentication required. Please sign in first.");
        }

        Account account = accountService.getAccountByUsername(username);
        if (account == null) {
            return ApiResponse.notFound("User not found");
        }

        return ApiResponse.success(account);
    }

    @PutMapping("/profile")
    public ApiResponse<Object> updateProfile(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody UpdateProfileRequest request) {
        String username = extractUsernameFromToken(token);
        if (username == null) {
            return ApiResponse.unauthorized("Authentication required. Please sign in first.");
        }

        Account account = accountService.getAccountByUsername(username);
        if (account == null) {
            return ApiResponse.notFound("User not found");
        }

        // Update fields
        if (request.getFirstName() != null) account.setFirstName(request.getFirstName());
        if (request.getLastName() != null) account.setLastName(request.getLastName());
        if (request.getEmail() != null) account.setEmail(request.getEmail());
        if (request.getPhone() != null) account.setPhone(request.getPhone());
        if (request.getAddress1() != null) account.setAddress1(request.getAddress1());
        if (request.getAddress2() != null) account.setAddress2(request.getAddress2());
        if (request.getCity() != null) account.setCity(request.getCity());
        if (request.getState() != null) account.setState(request.getState());
        if (request.getZip() != null) account.setZip(request.getZip());
        if (request.getCountry() != null) account.setCountry(request.getCountry());
        if (request.getLanguagePreference() != null) account.setLanguagePrefer(request.getLanguagePreference());
        if (request.getFavoriteCategory() != null) account.setFavoriteCategory(request.getFavoriteCategory());

        Account updated = accountService.updateAccount(username, account);

        return ApiResponse.success("Profile updated successfully", new Object() {
            public int userId = updated.getId();
            public String updatedUsername = updated.getUsername();
            public String email = updated.getEmail();
            public String firstName = updated.getFirstName();
            public String lastName = updated.getLastName();
        });
    }

    @PostMapping("/change-password")
    public ApiResponse<Object> changePassword(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody ChangePasswordRequest request) {
        String username = extractUsernameFromToken(token);
        if (username == null) {
            return ApiResponse.unauthorized("Authentication required. Please sign in first.");
        }

        if (request.getNewPassword() == null || !request.getNewPassword().equals(request.getConfirmPassword())) {
            return ApiResponse.badRequest("Passwords do not match");
        }

        boolean success = accountService.changePassword(username, request.getOldPassword(), request.getNewPassword());
        if (!success) {
            return ApiResponse.badRequest("Old password is incorrect");
        }

        return ApiResponse.success("Password changed successfully", null);
    }
}

