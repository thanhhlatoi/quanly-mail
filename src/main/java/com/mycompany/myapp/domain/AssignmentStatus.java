package com.mycompany.myapp.domain;

public enum AssignmentStatus {
    PENDING("Chờ nhân viên xác nhận"),
    ACTIVE("Đang được giao"),
    CANCELLED("Đã hủy phân công");

    private final String description;

    AssignmentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
