package com.example.certificateback.domain;

import com.example.certificateback.enumerations.CertificateType;
import com.example.certificateback.enumerations.RequestType;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class CertificateRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "status", nullable = false)
    private RequestType requestType;

    @Column(name = "issuer", nullable = true)
    private Certificate issuer;

    @Column(name = "certificate_type", nullable = false)
    private CertificateType certificateType;

    @Column(name = "subject", nullable = false)
    private User subject;

    @Column(name = "refusal_reason", nullable = true)
    private String refusalReason;
}
