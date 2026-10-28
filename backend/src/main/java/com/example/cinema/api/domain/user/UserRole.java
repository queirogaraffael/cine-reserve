package com.example.cinema.api.domain.user;

public enum UserRole {
    SUPER_ADMIN("super_admin"),
    CINEMA_ADMIN("cinema_admin"),
    USER("user");

    private String role;

    UserRole(String role){
        this.role = role;
    }

    public String getRole(){
        return role;
    }
}