package com.busbooking.app.models.api;

public class AuthData {
    private UserData user;
    private String token;

    public UserData getUser() { return user; }
    public void setUser(UserData user) { this.user = user; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
