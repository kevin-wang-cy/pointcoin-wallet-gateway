package com.upbchain.pointcoin.wallet.api.domain.converter;


import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author kevin.wang.cy@gmail.com
 */
@Converter(autoApply = true)
public class ZonedDateTimeConverter implements AttributeConverter<java.time.ZonedDateTime, java.sql.Timestamp> {

    @Override
    public java.sql.Timestamp convertToDatabaseColumn(ZonedDateTime entityValue) {
        if (entityValue == null) {
            return null;
        }
        return Timestamp.from(entityValue.toInstant());
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(java.sql.Timestamp databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        LocalDateTime localDateTime = databaseValue.toLocalDateTime();
        return localDateTime.atZone(ZoneId.systemDefault());
    }
}