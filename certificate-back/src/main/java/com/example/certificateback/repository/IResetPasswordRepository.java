package com.example.certificateback.repository;

import com.example.certificateback.domain.ResetPassword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IResetPasswordRepository extends JpaRepository<ResetPassword, Long> {

    ResetPassword findResetPasswordByUserId(Long id);
}
