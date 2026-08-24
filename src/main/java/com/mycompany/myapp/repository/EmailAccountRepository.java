package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.EmailAccount;
import com.mycompany.myapp.domain.EmailAccountStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailAccountRepository extends JpaRepository<EmailAccount, String> {
    List<EmailAccount> findByStatus(EmailAccountStatus status);
    Page<EmailAccount> findByStatus(EmailAccountStatus status, Pageable pageable);
    boolean existsByEmailAddressIgnoreCase(String emailAddress);
    Optional<EmailAccount> findByEmailAddressIgnoreCase(String emailAddress);
}
