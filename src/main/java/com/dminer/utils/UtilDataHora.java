package com.dminer.utils;


import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UtilDataHora {
    
    private static final Logger log = LoggerFactory.getLogger(UtilDataHora.class);


    /**
     * Recebe uma data no formato dd/mm/yyyy e verifica se faz ou fez aniversário no mês atual
     * @param String
     * @return
     */
    public static boolean isAniversariante(String dateStr) {
    	Date date = UtilDataHora.stringToDate(dateStr);    	
    	Date currentDate = new Date(currentFirstDayTimestamp().getTime());   	
    	return date.getMonth() == currentDate.getMonth();
    }
    
    
    public static Date parseDateStringToDate(String dateStr) {
    	String[] dateArr = dateStr.split("/");
    	int day = Integer.parseInt(dateArr[0]);
    	int month = Integer.parseInt(dateArr[1]);
    	int year = Integer.parseInt(dateArr[2]);
    	
    	Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		Date date = cal.getTime();
		System.out.println(date);
		return date;
    }
    
    
    public static Date alterDay(Date date, int newDay) {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date); 
    	// You can -/+ x months here to go back in history or move forward.
    	cal.add(Calendar.DAY_OF_MONTH, newDay); 
    	return cal.getTime();
    }
    
    public static Date alterMonth(Date date, int newMonth) {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date); 
    	// You can -/+ x months here to go back in history or move forward.
    	cal.add(Calendar.MONTH, newMonth); 
    	return cal.getTime();
    }
    
    public static Date alterYear(Date date, int newYear) {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date); 
    	// You can -/+ x months here to go back in history or move forward.
    	cal.add(Calendar.YEAR, newYear); 
    	return cal.getTime();
    }
    
    
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

    public static Timestamp toTimestampOrNull(String date) {
        if (date != null)
            return Timestamp.valueOf(date);
        return null;
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
    public static String timestampToStringOrNow(Timestamp date) {        
        if (date != null)
            return date.toString().substring(0, date.toString().length() -2);

        Timestamp timestamp = Timestamp.from(Instant.now());
        return timestamp.toString().substring(0, timestamp.toString().length() -2);
    }

    public static String timestampToStringOrNull(Timestamp date) {        
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");            
            return df.format(date);
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

    public static String dateToFullStringUTC(Timestamp date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
