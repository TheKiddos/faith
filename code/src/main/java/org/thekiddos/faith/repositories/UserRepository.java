package org.thekiddos.faith.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.thekiddos.faith.models.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    List<User> findByAdminTrue();

    List<User> findAllByEnabled( boolean isEnabled );

    Optional<User> findByNicknameIgnoreCase( String nickname );
}
