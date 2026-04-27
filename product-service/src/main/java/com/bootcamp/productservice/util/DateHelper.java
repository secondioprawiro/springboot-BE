package com.bootcamp.productservice.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateHelper {
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static String now(){
        return LocalDateTime.now().format(DEFAULT_FORMATTER);
    }

    public static String format(LocalDateTime date){
        return date.format(DEFAULT_FORMATTER);
    }
}
