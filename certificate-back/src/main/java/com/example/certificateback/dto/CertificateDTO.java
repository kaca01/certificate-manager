package com.example.certificateback.dto;

import com.example.certificateback.domain.Certificate;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class CertificateDTO {

    private Long id;

    private String certificateType;

    private String validTo;

    private String validFrom;

    private UserDTO subject;

    private boolean isWithdrawn;

    private String withdrawnReason;

    private long serialNumber;

    public CertificateDTO(Certificate certificate) {
        this.certificateType = certificate.getCertificateType().toString();
        this.validTo = certificate.getValidTo().toString();
        this.validFrom = certificate.getValidFrom().toString();
        this.subject = new UserDTO(certificate.getSubject());
        this.isWithdrawn = certificate.isWithdrawn();
        this.withdrawnReason = certificate.getWithdrawnReason();
        this.serialNumber = certificate.getSerialNumber();
    }
}
