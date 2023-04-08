package com.example.certificateback.domain;

import com.example.certificateback.dto.CertificateDTO;
import com.example.certificateback.enumeration.CertificateType;
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
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false)
    private CertificateType certificateType;

    @Column(name = "valid_from", nullable = false)
    private Date validFrom;

    @Column(name = "valid_to", nullable = false)
    private Date validTo;

    @Column(name = "issue_date", nullable = false)
    private Date issue_date;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private User subject;

    @Column(name = "is_withdrawn", nullable = false)
    private boolean isWithdrawn;

    @Column(name = "withdrawn_reason", nullable = false)
    private String withdrawnReason;

    @Column(name = "serial_number", nullable = true)
    private String serialNumber;

    public boolean isValid() {
        Date now = new Date();
        return validFrom.compareTo(now) <= 0 && validTo.compareTo(now) > 0;
    }

    public Certificate(CertificateDTO dto) {
        this.certificateType = CertificateType.valueOf(dto.getCertificateType());
        try {
            this.validTo = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(dto.getValidTo());
            this.validFrom = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(dto.getValidFrom());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        this.subject = new User(dto.getSubject());
        this.isWithdrawn = dto.isWithdrawn();
        this.withdrawnReason = dto.getWithdrawnReason();
        this.serialNumber = dto.getSerialNumber();
    }
}
