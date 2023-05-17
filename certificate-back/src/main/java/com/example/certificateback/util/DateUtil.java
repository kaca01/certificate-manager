package com.example.certificateback.util;

import com.example.certificateback.configuration.KeyStoreConstants;
import com.example.certificateback.domain.CertificateRequest;
import com.example.certificateback.enumeration.CertificateType;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateUtil {

    public Date generateStartTime() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        return localDateTimeToDate(startOfDay);
    }

    public Date generateEndTime(Date date, CertificateRequest request){
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime endTime;
        if(request.getCertificateType().equals(CertificateType.ROOT))
            endTime = localDateTime.plusYears(KeyStoreConstants.ROOT_CERTIFICATE_DURATION);
        else endTime = localDateTime.plusYears(KeyStoreConstants.CERTIFICATE_DURATION);
        return localDateTimeToDate(endTime);
    }

    private LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
