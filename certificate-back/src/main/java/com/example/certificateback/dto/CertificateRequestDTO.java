package com.example.certificateback.dto;

import com.example.certificateback.domain.CertificateRequest;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class CertificateRequestDTO {

    private Long id;

    private String requestType;

    private CertificateDTO issuer;

    private String certificateType;

    private UserDTO subject;

    private String refusalReason;

    public CertificateRequestDTO(CertificateRequest request) {
        this.id = request.getId();
        this.requestType = request.getRequestType().toString();
        this.issuer = new CertificateDTO(request.getIssuer());
        this.certificateType = request.getCertificateType().toString();
        this.subject = new UserDTO(request.getSubject());
        this.refusalReason = request.getRefusalReason();
    }
}
