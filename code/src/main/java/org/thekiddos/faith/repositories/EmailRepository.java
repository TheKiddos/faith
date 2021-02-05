package org.thekiddos.faith.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thekiddos.faith.models.Email;

import java.util.List;

public interface EmailRepository extends JpaRepository<Email, Long> {
    List<Email> findAllByTo( String email );
}
