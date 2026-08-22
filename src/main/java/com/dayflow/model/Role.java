package com.dayflow.model;

public enum Role {
    EMPLOYEE("ROLE_EMPLOYEE", "Standard Employee Profile Access"),
    HR_ADMIN("ROLE_HR_ADMIN", "Full HR Administrator Access");

    private final String authority;
    private final String description;

    Role(String authority, String description) {
        this.authority = authority;
        this.description = description;
    }

    public String getAuthority() {
        return authority;
    }

    public String getDescription() {
        return description;
    }
}
