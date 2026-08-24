package com.mycompany.myapp.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Table(name = "email_account")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @Column(name = "email_address", nullable = false, length = 255)
    private String emailAddress;

    @Column(name = "password_encrypted", nullable = false, columnDefinition = "TEXT")
    private String passwordEncrypted;

    @Column(name = "two_factor_secret_encrypted", columnDefinition = "TEXT")
    private String twoFactorSecretEncrypted;

    @Column(name = "recovery_phone", length = 30)
    private String recoveryPhone;

    @Column(name = "recovery_email", length = 255)
    private String recoveryEmail;

    @Column(name = "country_code", length = 2)
    private String countryCode;

    @Column(name = "account_year")
    private Integer year;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private EmailAccountStatus status = EmailAccountStatus.ACTIVE;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id")
    private User assignedUser;

    @Column(name = "purchase_price", precision = 19, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "last_checked_at")
    private Instant lastCheckedAt;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;
}
