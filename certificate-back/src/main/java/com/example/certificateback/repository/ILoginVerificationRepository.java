package com.example.certificateback.repository;

import com.example.certificateback.domain.Certificate;
import com.example.certificateback.domain.LoginVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ILoginVerificationRepository extends JpaRepository<LoginVerification, Long> {

    Optional<LoginVerification> findByUserEmail(String email);
}
