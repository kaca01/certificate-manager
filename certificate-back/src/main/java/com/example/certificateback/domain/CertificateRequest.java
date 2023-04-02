package com.example.certificateback.domain;

import com.example.certificateback.enumeration.CertificateType;
import com.example.certificateback.enumeration.RequestType;
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

    @OneToOne
    private Certificate issuer;

    @Column(name = "certificate_type", nullable = false)
    private CertificateType certificateType;

    @OneToOne
    private User subject;

    @Column(name = "refusal_reason", nullable = true)
    private String refusalReason;
}
