package org.csu.mypetstorebackend.controller.account;

import org.csu.mypetstorebackend.common.ApiResponse;
import org.csu.mypetstorebackend.dto.ChangePasswordRequest;
import org.csu.mypetstorebackend.dto.UpdateProfileRequest;
import org.csu.mypetstorebackend.entity.Account;
import org.csu.mypetstorebackend.service.AccountService;
import org.csu.mypetstorebackend.utils.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/account")
public class AccountController {
    private final AccountService accountService;
    private static final int MAX_PASSWORD_LENGTH = 25;

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

    private Map<String, Object> buildProfileResponse(Account account) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("userId", account.getUsername());
        response.put("username", account.getUsername());
        response.put("email", account.getEmail());
        response.put("firstName", account.getFirstName());
        response.put("lastName", account.getLastName());
        response.put("status", account.getStatus());
        response.put("address1", account.getAddress1());
        response.put("address2", account.getAddress2());
        response.put("city", account.getCity());
        response.put("state", account.getState());
        response.put("zip", account.getZip());
        response.put("country", account.getCountry());
        response.put("phone", account.getPhone());
        response.put("languagePreference", account.getLanguagePreference());
        response.put("favoriteCategory", account.getFavoriteCategory());
        response.put("myListOption", account.getMyListOption());
        response.put("bannerOption", account.getBannerOption());
        return response;
    }

    private String normalizeRequired(String value) {
        return value == null || value.trim().isEmpty() ? "N/A" : value.trim();
    }

    private String normalizeOptional(String value) {
        return value == null ? "" : value.trim();
    }

    private String trimToMax(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    @GetMapping("/profile")
    public ApiResponse<Map<String, Object>> getProfile(@RequestHeader(value = "Authorization", required = false) String token) {
        String username = extractUsernameFromToken(token);
        if (username == null) {
            return ApiResponse.unauthorized("Authentication required. Please sign in first.");
        }

        Account account = accountService.getAccountByUsername(username);
        if (account == null) {
            return ApiResponse.notFound("User not found");
        }

        return ApiResponse.success(buildProfileResponse(account));
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
        if (request.getFirstName() != null) account.setFirstName(normalizeRequired(request.getFirstName()));
        if (request.getLastName() != null) account.setLastName(normalizeRequired(request.getLastName()));
        if (request.getEmail() != null) account.setEmail(normalizeRequired(request.getEmail()));
        if (request.getPhone() != null) account.setPhone(normalizeRequired(request.getPhone()));
        if (request.getAddress1() != null) account.setAddress1(normalizeRequired(request.getAddress1()));
        if (request.getAddress2() != null) account.setAddress2(normalizeOptional(request.getAddress2()));
        if (request.getCity() != null) account.setCity(normalizeRequired(request.getCity()));
        if (request.getState() != null) account.setState(normalizeRequired(request.getState()));
        if (request.getZip() != null) account.setZip(normalizeRequired(request.getZip()));
        if (request.getCountry() != null) account.setCountry(normalizeRequired(request.getCountry()));
        if (request.getLanguagePreference() != null) account.setLanguagePreference(normalizeRequired(request.getLanguagePreference()));
        if (request.getFavoriteCategory() != null) account.setFavoriteCategory(normalizeOptional(request.getFavoriteCategory()));

        Account updated = accountService.updateAccount(username, account);

        return ApiResponse.success("Profile updated successfully", buildProfileResponse(updated));
    }

    @PostMapping("/change-password")
    public ApiResponse<Object> changePassword(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody ChangePasswordRequest request) {
        String username = extractUsernameFromToken(token);
        if (username == null) {
            return ApiResponse.unauthorized("Authentication required. Please sign in first.");
        }

        if (request.getOldPassword() == null || request.getOldPassword().trim().isEmpty()) {
            return ApiResponse.badRequest("Old password is required");
        }

        if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
            return ApiResponse.badRequest("New password is required");
        }

        String newPassword = request.getNewPassword().trim();
        if (!newPassword.equals(request.getConfirmPassword())) {
            return ApiResponse.badRequest("Passwords do not match");
        }

        if (newPassword.length() > MAX_PASSWORD_LENGTH) {
            return ApiResponse.badRequest("Password must be no more than 25 characters");
        }

        boolean success = accountService.changePassword(username, request.getOldPassword().trim(), newPassword);
        if (!success) {
            return ApiResponse.badRequest("Old password is incorrect");
        }

        return ApiResponse.success("Password changed successfully", null);
    }
}
