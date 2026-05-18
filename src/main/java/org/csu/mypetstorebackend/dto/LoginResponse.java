package org.csu.mypetstorebackend.dto;

/**
 * 登录响应DTO
 */
public class LoginResponse {
    private String token;
    private String userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String favoriteCategory;

    public LoginResponse() {}

    public LoginResponse(String token, String userId, String username, String email, String firstName, String lastName, String favoriteCategory) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.favoriteCategory = favoriteCategory;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFavoriteCategory() {
        return favoriteCategory;
    }

    public void setFavoriteCategory(String favoriteCategory) {
        this.favoriteCategory = favoriteCategory;
    }
}

