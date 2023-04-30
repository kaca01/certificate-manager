package com.example.certificateback.domain.ksData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bouncycastle.asn1.x500.X500Name;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectData {
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private X500Name x500name;
    private Long serialNumber;
    private Date startDate;
    private Date endDate;
}
