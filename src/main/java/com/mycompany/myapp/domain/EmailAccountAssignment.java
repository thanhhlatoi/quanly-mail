package com.mycompany.myapp.domain;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "email_account_assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailAccountAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email_account_id", nullable = false)
    private EmailAccount emailAccount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assigned_by", nullable = false)
    private User assignedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false, length = 30)
    private EmailAccountServiceType serviceType;

    @Column(name = "assignedAt")
    private Instant assignedAt;

    @Column(name = "unassignedAt")
    private Instant unassignedAt;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private AssignmentStatus status = AssignmentStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "usage_status", nullable = false, length = 30)
    private AssignmentUsageStatus usageStatus = AssignmentUsageStatus.ASSIGNED;
}
