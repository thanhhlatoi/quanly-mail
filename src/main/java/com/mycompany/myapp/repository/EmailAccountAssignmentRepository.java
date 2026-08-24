package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.EmailAccountAssignment;
import com.mycompany.myapp.domain.EmailAccountServiceType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailAccountAssignmentRepository extends JpaRepository<EmailAccountAssignment, Long> {
    List<EmailAccountAssignment> findByEmailAccountId(String emailAccountId);

    @Query("select a from EmailAccountAssignment a join fetch a.emailAccount where a.id = :id")
    Optional<EmailAccountAssignment> findDetailedById(@Param("id") Long id);

    @Query(
        value = "select a from EmailAccountAssignment a join fetch a.emailAccount where a.user.id = :userId and a.status = com.mycompany.myapp.domain.AssignmentStatus.ACTIVE and a.serviceType <> com.mycompany.myapp.domain.EmailAccountServiceType.GOOGLE_ADSENSE",
        countQuery = "select count(a) from EmailAccountAssignment a where a.user.id = :userId and a.status = com.mycompany.myapp.domain.AssignmentStatus.ACTIVE and a.serviceType <> com.mycompany.myapp.domain.EmailAccountServiceType.GOOGLE_ADSENSE"
    )
    List<EmailAccountAssignment> findActiveByUserId(@Param("userId") Long userId);

    @Query(
        value = "select a from EmailAccountAssignment a join fetch a.emailAccount where a.serviceType = :serviceType and a.status = com.mycompany.myapp.domain.AssignmentStatus.ACTIVE",
        countQuery = "select count(a) from EmailAccountAssignment a where a.serviceType = :serviceType and a.status = com.mycompany.myapp.domain.AssignmentStatus.ACTIVE"
    )
    List<EmailAccountAssignment> findActiveByServiceType(@Param("serviceType") EmailAccountServiceType serviceType);

    @Query(
        "select a from EmailAccountAssignment a join fetch a.emailAccount where a.user.id = :userId and a.status = com.mycompany.myapp.domain.AssignmentStatus.ACTIVE and a.serviceType <> com.mycompany.myapp.domain.EmailAccountServiceType.GOOGLE_ADSENSE"
    )
    Page<EmailAccountAssignment> findActivePageByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query(
        "select a from EmailAccountAssignment a join fetch a.emailAccount where a.serviceType = :serviceType and a.status = com.mycompany.myapp.domain.AssignmentStatus.ACTIVE"
    )
    Page<EmailAccountAssignment> findActivePageByServiceType(@Param("serviceType") EmailAccountServiceType serviceType, Pageable pageable);

    @Query(
        value = "select a from EmailAccountAssignment a join fetch a.emailAccount where a.status = com.mycompany.myapp.domain.AssignmentStatus.ACTIVE",
        countQuery = "select count(a) from EmailAccountAssignment a where a.status = com.mycompany.myapp.domain.AssignmentStatus.ACTIVE"
    )
    Page<EmailAccountAssignment> findActivePage(Pageable pageable);

    boolean existsByEmailAccountIdAndServiceTypeAndStatus(
        String emailAccountId,
        EmailAccountServiceType serviceType,
        com.mycompany.myapp.domain.AssignmentStatus status
    );
}
