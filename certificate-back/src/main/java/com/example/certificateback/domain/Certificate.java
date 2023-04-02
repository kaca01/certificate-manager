package com.example.certificateback.domain;

import com.example.certificateback.enumeration.CertificateType;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "certificate_type", nullable = false)
    private CertificateType certificateType;

    @Column(name = "valid_to", nullable = false)
    private Date validTo;

    @Column(name = "valid_from", nullable = false)
    private String validFrom;

    @Column(name = "subject", nullable = false)
    private User user;

    @Column(name = "is_withdrawn", nullable = false)
    private boolean isWithdrawn;

    @Column(name = "withdrawn_reason", nullable = false)
    private String withdrawnReason;

    @Column(name = "serial_number", nullable = true)
    private long serialNumber;

    public boolean isValid() {
        Date now = new Date();
        if (validFrom.compareTo(now) <= 0 && validTo.compareTo(now) > 0) return True;
        return False;
    }
}
