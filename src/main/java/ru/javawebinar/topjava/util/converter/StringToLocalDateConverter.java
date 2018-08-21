package ru.javawebinar.topjava.util.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StringToLocalDateConverter implements Converter<String, LocalDate> {

//    private DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public LocalDate convert(String source) {
        if (source == null || source.isEmpty())
            return LocalDate.now();
        return LocalDate.parse(source, formatter);
    }
}
