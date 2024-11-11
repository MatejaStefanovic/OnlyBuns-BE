package org.onlybuns.repository;

import org.onlybuns.enums.UserRole;
import org.onlybuns.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    User findByEmail(String email);
    User findByUsername(String username);

    List<User> findAllByRole(UserRole role);
}
