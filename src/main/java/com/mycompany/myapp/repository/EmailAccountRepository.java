package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.EmailAccount;
import com.mycompany.myapp.domain.EmailAccountStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailAccountRepository extends JpaRepository<EmailAccount, String> {
    List<EmailAccount> findByStatus(EmailAccountStatus status);
    Page<EmailAccount> findByStatus(EmailAccountStatus status, Pageable pageable);

    @Query(
        value = "select e from EmailAccount e where not exists (select s.id from EmailService s where s.emailAccount.id = e.id and s.serviceType = com.mycompany.myapp.domain.EmailAccountServiceType.GOOGLE_ADSENSE)",
        countQuery = "select count(e) from EmailAccount e where not exists (select s.id from EmailService s where s.emailAccount.id = e.id and s.serviceType = com.mycompany.myapp.domain.EmailAccountServiceType.GOOGLE_ADSENSE)"
    )
    Page<EmailAccount> findAllVisibleInWarehouse(Pageable pageable);

    @Query(
        value = "select e from EmailAccount e where e.status = :status and not exists (select s.id from EmailService s where s.emailAccount.id = e.id and s.serviceType = com.mycompany.myapp.domain.EmailAccountServiceType.GOOGLE_ADSENSE)",
        countQuery = "select count(e) from EmailAccount e where e.status = :status and not exists (select s.id from EmailService s where s.emailAccount.id = e.id and s.serviceType = com.mycompany.myapp.domain.EmailAccountServiceType.GOOGLE_ADSENSE)"
    )
    Page<EmailAccount> findVisibleInWarehouseByStatus(@Param("status") EmailAccountStatus status, Pageable pageable);
    boolean existsByEmailAddressIgnoreCase(String emailAddress);
    Optional<EmailAccount> findByEmailAddressIgnoreCase(String emailAddress);
}
