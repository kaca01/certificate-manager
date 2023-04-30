package com.example.certificateback.repository;

import com.example.certificateback.domain.Password;
import com.example.certificateback.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPasswordRepository extends JpaRepository<Password, Long> {
}
