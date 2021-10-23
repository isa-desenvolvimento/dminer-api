package com.dminer.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
//import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtilDataHora {
    
    private static final Logger log = LoggerFactory.getLogger(UtilDataHora.class);

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
