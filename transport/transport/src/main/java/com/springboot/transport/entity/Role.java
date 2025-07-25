package com.springboot.transport.entity;
public enum Role {
    ADMIN("ROLE_ADMIN"),
    RECEPTIONIST("ROLE_RECEPTIONIST"),
    SURVEYOR("ROLE_SURVEYOR"),
    LOGISTIC_MANAGER("ROLE_LOGISTIC_MANAGER"),
    CUSTOMER("ROLE_CUSTOMER");

    private final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public static Role fromString(String text) {
        for (Role role : Role.values()) {
            if (role.name().equalsIgnoreCase(text)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
} 