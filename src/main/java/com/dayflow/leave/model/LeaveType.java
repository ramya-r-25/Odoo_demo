package com.dayflow.leave.model;

/**
 * Types of leave available in Dayflow HRMS.
 */
public enum LeaveType {
    ANNUAL("Annual Leave"),
    SICK("Sick Leave"),
    CASUAL("Casual Leave"),
    MATERNITY("Maternity Leave"),
    PATERNITY("Paternity Leave"),
    UNPAID("Unpaid Leave");

    private final String displayName;

    LeaveType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
