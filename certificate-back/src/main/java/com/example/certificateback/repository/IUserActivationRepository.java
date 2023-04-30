package com.example.certificateback.repository;

import com.example.certificateback.domain.User;
import com.example.certificateback.domain.UserActivation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserActivationRepository  extends JpaRepository<UserActivation, Long> {
}
