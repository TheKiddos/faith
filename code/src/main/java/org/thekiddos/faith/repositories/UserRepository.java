package org.thekiddos.faith.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.thekiddos.faith.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
}
