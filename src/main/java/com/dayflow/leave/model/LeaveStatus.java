package com.dayflow.leave.model;

/**
 * Enum representing the lifecycle of a leave request.
 * PENDING → APPROVED or REJECTED by HR Admin.
 */
public enum LeaveStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected");

    private final String displayName;

    LeaveStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
