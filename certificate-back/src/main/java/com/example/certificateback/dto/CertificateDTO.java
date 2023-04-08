package com.example.certificateback.dto;

import com.example.certificateback.domain.Certificate;

import com.example.certificateback.domain.User;
import com.example.certificateback.enumeration.CertificateType;
import lombok.*;


import java.util.Date;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificateDTO {

    private String certificateType;

    private String validTo;

    private String validFrom;

    private String issueDate;

    private UserDTO subject;

    private boolean isWithdrawn;

    private String withdrawnReason;

    private long serialNumber;

    public CertificateDTO(Certificate certificate) {
        this.certificateType = certificate.getCertificateType().toString();
        this.validTo = certificate.getValidTo().toString();
        this.validFrom = certificate.getValidFrom().toString();
        this.issueDate =  certificate.getIssue_date().toString();
        this.subject = new UserDTO(certificate.getSubject());
        this.isWithdrawn = certificate.isWithdrawn();
        this.withdrawnReason = certificate.getWithdrawnReason();
        this.serialNumber = certificate.getSerialNumber();
    }

    // response
    public CertificateDTO(Date issueDate, User user, CertificateType type, long serialNum) {
        this.issueDate =  issueDate.toString();
        this.subject = new UserDTO(user);
        this.certificateType = type.toString();
        this.serialNumber = serialNum;
    }
}
