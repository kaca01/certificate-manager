package com.example.certificateback.repository;

import com.example.certificateback.domain.CertificateRequest;
import com.example.certificateback.domain.Certificate;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ICertificateRepository extends JpaRepository<Certificate, Long> {

    public Certificate findBySerialNumber(long number);

    public List<Certificate> findBySubjectId(long id);
}
