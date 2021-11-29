package com.dminer.utils;


import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UtilDataHora {
    
    private static final Logger log = LoggerFactory.getLogger(UtilDataHora.class);


    /**
     * Recebe uma data no formato yyyy-MM-dd HH:mm:ss e retorna
     * true se for uma data igual ou menor que hoje
     * @return boolean
     */
    public static boolean ehMenorQueHoje(String date) {
        Timestamp date1 = Timestamp.valueOf(date);
        Timestamp date2 = Timestamp.from(Instant.now());
        return date2.after(date1);
    }

    /**
     * Retorna data em string no formato yyyy-MM-dd HH:mm:ss do primeiro dia do mes
     * @return String
     */
    public static String currentFirstDayFormat() {
        return LocalDate.now().getYear() + "-" + LocalDate.now().getMonthValue() + "-01 00:00:00";
    }

    /**
     * Retorna data em string no formato yyyy-MM-dd HH:mm:ss do ultimo dia do mes
     * @return String
     */
    public static String currentLastDayFormat() {
        return LocalDate.now().getYear() + "-" + LocalDate.now().getMonthValue() + "-" + LocalDate.now().lengthOfMonth() + " 00:00:00";
    }


    /**
     * Retorna data timestamp no formato yyyy-MM-dd HH:mm:ss do primeiro dia do mes
     * @return Timestamp
     */
    public static Timestamp currentFirstDayTimestamp() {
        return Timestamp.valueOf(LocalDate.now().getYear() + "-" + LocalDate.now().getMonthValue() + "-01 00:00:00.000");
    }

    /**
     * Retorna data timestamp no formato yyyy-MM-dd HH:mm:ss do ultimo dia do mes
     * @return Timestamp
     */
    public static Timestamp currentLastDayTimestamp() {
        return Timestamp.valueOf(LocalDate.now().getYear() + "-" + LocalDate.now().getMonthValue() + "-" + LocalDate.now().lengthOfMonth() + " 00:00:00.000");
    }


    /**
     * Recebe uma data no formato dd/MM/yyyy e retorna um objeto Date
     * @param date
     * @return Date
     */
    public static Date stringToDate(String date) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return df.parse(date);
        } catch (ParseException e) {
            log.error("Falha ao converter data: {}", date);
        }
        return null;
    }

    /**
     * Recebe uma data no formato yyyy-MM-dd HH:mm:ss e retorna um objeto Timestamp
     * @param date
     * @return Timestamp
     */
    public static Timestamp toTimestamp(String date) {
        if (date != null)
            return Timestamp.valueOf(date);
        return Timestamp.from(Instant.now());
    }

    public static boolean isTimestampValid(String date) {
        try {
            if (date == null || date.isEmpty())
                return false;
            Timestamp.valueOf(date);
            return true;
        } catch (IllegalArgumentException  e) {
            return false;
        }
    }

    /**
     * Recebe uma data Timestamp e retorna uma string yyyy-MM-dd HH:mm:ss
     * @param Timestamp date
     * @return String
     */
    public static String timestampToString(Timestamp date) {        
        if (date != null)
            return date.toString().substring(0, date.toString().length() -2);

        Timestamp timestamp = Timestamp.from(Instant.now());
        return timestamp.toString().substring(0, timestamp.toString().length() -2);
    }


     /**
     * Recebe uma data no formato yyyy-MM-dd HH:mm:ss e retorna um objeto Date
     * @param date
     * @return Date
     */
    public static Date stringToFullDateHour(String date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");        
        try {
            return df.parse(date);
        } catch (ParseException e) {
            log.error("Falha ao converter data: {}", date);
        }
        return null;
    }


    /**
     * Retorna uma string do objeto Date formatado em dd/MM/yyyy
     * @param date
     * @return
     */
    public static String dateToString(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(date);
    }

    /**
     * Retorna uma string do objeto Date formatado em yyyy-MM-dd
     * @param date
     * @return
     */
    public static String dateToStringUTC(Timestamp date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

    public static String hourToString(Date hora) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");        
        return sdf.format(hora);
    }

    public static Date stringToHour(String hora) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");        
        try {
            return sdf.parse(hora);
        } catch (ParseException e) {
            log.error("Falha ao converter hora: {}", hora);
        }
        return null;
    }

}
