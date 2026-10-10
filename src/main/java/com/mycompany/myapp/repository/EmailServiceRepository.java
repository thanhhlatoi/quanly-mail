package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.EmailAccountServiceType;
import com.mycompany.myapp.domain.EmailService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailServiceRepository extends JpaRepository<EmailService, Long> {
    @EntityGraph(attributePaths = "emailAccount")
    Page<EmailService> findByServiceType(EmailAccountServiceType serviceType, Pageable pageable);

    boolean existsByEmailAccountIdAndServiceType(String emailAccountId, EmailAccountServiceType serviceType);

    boolean existsByEmailAccountIdAndServiceTypeAndActiveTrue(String emailAccountId, EmailAccountServiceType serviceType);

    @EntityGraph(attributePaths = "emailAccount")
    java.util.Optional<EmailService> findByEmailAccountIdAndServiceTypeAndActiveTrue(
        String emailAccountId,
        EmailAccountServiceType serviceType
    );
}
