package com.oksys.auth.repository;

import com.oksys.auth.model.User; // Sesuaikan import User ke lokasi model/entity kamu
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Method Pencarian (Querying)
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    // Method Validasi Keberadaan (Existence Check)
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}