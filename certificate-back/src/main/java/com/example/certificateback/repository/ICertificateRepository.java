package com.example.certificateback.repository;

import com.example.certificateback.domain.Certificate;
import com.example.certificateback.domain.CertificateRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ICertificateRepository extends JpaRepository<Certificate, Long> {

    public List<Certificate> findBySubjectId(long id);
}
