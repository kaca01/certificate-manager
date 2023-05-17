package com.example.certificateback.repository;

import com.example.certificateback.domain.Certificate;
import com.example.certificateback.domain.CertificateRequest;
import com.example.certificateback.domain.User;
import com.example.certificateback.enumeration.CertificateType;
import com.example.certificateback.enumeration.RequestType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ICertificateRequestRepository extends JpaRepository<CertificateRequest, Long> {

    public List<CertificateRequest> findBySubjectId(long id);

    public List<CertificateRequest> findByIssuerAndRequestType(Certificate issuer, RequestType requestType);

    List<CertificateRequest> findByRequestTypeAndIssuerSubjectId(RequestType type, long id);
}
