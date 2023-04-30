package com.example.certificateback.domain;

import com.example.certificateback.dto.CertificateRequestDTO;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status", nullable = false)
    private RequestType requestType;

    @OneToOne
    private Certificate issuer;

    @Column(name = "type", nullable = false)
    private CertificateType certificateType;

    @OneToOne
    private User subject;

    @Column(name = "refusal_reason")
    private String refusalReason;

    public CertificateRequest(CertificateRequestDTO dto) {
        this.requestType = RequestType.valueOf(dto.getRequestType());
        this.certificateType = CertificateType.valueOf(dto.getCertificateType());
        this.refusalReason = dto.getRefusalReason();
    }
}
