package com.example.certificateback.repository;

import com.example.certificateback.domain.CertificateRequest;
import com.example.certificateback.domain.Certificate;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ICertificateRepository extends JpaRepository<Certificate, Long> {

    List<Certificate> findBySubjectId(long id);

    Optional<Certificate> findBySerialNumber(String serialNumber);

}
