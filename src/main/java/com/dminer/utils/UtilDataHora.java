package com.dminer.utils;


import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UtilDataHora {
    
    private static final Logger log = LoggerFactory.getLogger(UtilDataHora.class);

    public static boolean isValid(String date) {
        return true;
        // SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");        
        // try {
        //     df.parse(date);
        //     return true;
        // } catch (ParseException e) {
        //     log.error("Falha ao validar data: {}", date);
        // }
        // return false;
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
     * Recebe uma data no formato dd/MM/yyyy e retorna um objeto Date
     * @param date
     * @return Date
     */
    public static Timestamp stringToTimestamp(String date) {
        if (isValid(date)) {
            return Timestamp.valueOf(date);
        }        
        return null;
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

    // public static void main(String[] args) throws ParseException {
    //     // System.out.println(
    //     //     UtilDataHora.stringToFullDateHour("2021-10-25 19:00:00")
    //     // );

    //     Timestamp t = Timestamp.valueOf("2021-10-25 19:00:00");
    //     System.out.println(t);

    // }

    /**
     * Retorna uma string do objeto Date formatado em dd/MM/yyyy
     * @param date
     * @return
     */
    public static String dateToString(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
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
