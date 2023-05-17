package com.example.certificateback.dto;

import com.example.certificateback.domain.CertificateRequest;
import com.example.certificateback.domain.User;
import com.example.certificateback.enumeration.CertificateType;
import lombok.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificateRequestDTO {

    private Long id;
  
    private String date;

    private String requestType;

    private String issuer;  // serial number of certificate

    private String certificateType;

    private UserDTO subject;

    private String refusalReason;

    public CertificateRequestDTO(CertificateRequest request) {
        this.id = request.getId();
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        this.date = format.format(request.getDate());
        this.requestType = request.getRequestType().toString();
        if (request.getCertificateType() != CertificateType.ROOT)
            this.issuer = request.getIssuer().getSerialNumber();
        this.certificateType = request.getCertificateType().toString();
        this.refusalReason = request.getRefusalReason();
        this.subject = new UserDTO(request.getSubject());
    }
}
