package com.mycompany.myapp.domain;

public enum EmailAccountStatus {
    ACTIVE("Đang hoạt động"),
    INACTIVE("Không hoạt động"),
    LOCKED("Đã bị khóa"),
    SUSPENDED("Tạm đình chỉ"),
    EXPIRED("Đã hết hạn");

    private final String description;

    EmailAccountStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
