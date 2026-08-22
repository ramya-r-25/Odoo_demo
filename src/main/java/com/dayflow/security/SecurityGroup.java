package com.dayflow.security;

public enum SecurityGroup {
    EMPLOYEE("Employee", "Standard employee profile access group"),
    HR_ADMIN("HR/Admin", "Full HR Administrator access group");

    private final String name;
    private final String description;

    SecurityGroup(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
