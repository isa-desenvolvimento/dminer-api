package com.dminer;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import com.dminer.utils.UtilDataHora;

public class Testes {

	public static void main2(String[] args) {
		
		String data = "15/11/1995";
		
		Calendar cal = Calendar.getInstance();
    	cal.setTime(UtilDataHora.parseDateStringToDate("15/08/1995"));
    	
    	Calendar cal2 = Calendar.getInstance();
    	cal2.setTime(UtilDataHora.parseDateStringToDate("20/11/1998"));
		
		if (UtilDataHora.isAniversariante(data)) {
			System.out.println("é aniversariante");
		} else {
			System.out.println("Não é aniversariante");
		}
		
	}
}
