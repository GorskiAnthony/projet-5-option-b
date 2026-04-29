package com.openclassrooms.mddapi.dto;

public class LoginRequest {
    private String identifier; // email ou username
    private String password;

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
