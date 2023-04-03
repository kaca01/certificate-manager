package com.example.certificateback.domain.ksData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sun.security.x509.X500Name;

import java.security.PublicKey;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectData {
    private PublicKey publicKey;
    private X500Name x500name;
    private String serialNumber;
    private Date startDate;
    private Date endDate;
}
