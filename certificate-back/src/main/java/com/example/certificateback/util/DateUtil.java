package com.example.certificateback.util;

import com.example.certificateback.configuration.KeyStoreConstants;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateUtil {

    public Date generateStartTime() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        return localDateTimeToDate(startOfDay);
    }

    public Date generateEndTime(Date date){
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime endTime = localDateTime.plusYears(KeyStoreConstants.CERTIFICATE_DURATION);
        return localDateTimeToDate(endTime);
    }

    private LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
