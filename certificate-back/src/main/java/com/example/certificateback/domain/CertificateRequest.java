package com.example.certificateback.domain;

import com.example.certificateback.dto.CertificateRequestDTO;
import com.example.certificateback.enumeration.CertificateType;
import com.example.certificateback.enumeration.RequestType;
import lombok.*;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "status", nullable = false)
    private RequestType requestType;

    @ManyToOne
    private Certificate issuer;

    @Column(name = "type", nullable = false)
    private CertificateType certificateType;

    @ManyToOne
    private User subject;

    @Column(name = "refusal_reason")
    private String refusalReason;

    public CertificateRequest(CertificateRequestDTO dto) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        try {
            this.setDate(formatter.parse(dto.getDate()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        this.requestType = RequestType.valueOf(dto.getRequestType());
        this.certificateType = CertificateType.valueOf(dto.getCertificateType());
        this.refusalReason = dto.getRefusalReason();
    }
}
