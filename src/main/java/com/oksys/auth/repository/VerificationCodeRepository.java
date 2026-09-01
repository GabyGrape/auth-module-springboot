package com.oksys.auth.repository;

import com.oksys.auth.model.User;
import com.oksys.auth.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByUser(User user);
    Optional<VerificationCode> findByUserAndCode(User user, String code);
    void deleteByUser(User user);
}