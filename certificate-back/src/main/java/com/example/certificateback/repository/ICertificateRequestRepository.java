package com.example.certificateback.repository;

import com.example.certificateback.domain.CertificateRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ICertificateRequestRepository extends JpaRepository<CertificateRequest, Long> {

    public List<CertificateRequest> findBySubjectId(Long id);
}
