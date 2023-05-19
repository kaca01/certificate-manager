package com.example.certificateback.repository;

import com.example.certificateback.domain.LoginVerification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ILoginVerificationRepository extends JpaRepository<LoginVerification, Long> {
}
