package com.dminer.components;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.dminer.entities.Reminder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// @Component
public class LembreteAgendado {
    
    // private static final String TIME_ZONE = "America/Sao_Paulo";
    // private static final String CRON_EXPRESSION = "";
    // private Reminder reminder;
    
    
    // public LembreteAgendado(String cronExpression, Reminder reminder) {	
	// 	this.changePrivateConstant(cronExpression);
	// 	this.reminder = reminder;
	// }
    
    
    // @Scheduled(cron = CRON_EXPRESSION, zone = TIME_ZONE)
    // public void execute() {

    // }

    
    // private void changePrivateConstant(Object newValue) {
    // 	try {
    // 		Field field = this.getClass().getDeclaredField("CRON_EXPRESSION");
    // 		field.setAccessible(true);
    // 		Field modifiers = field.getClass().getDeclaredField("modifiers");
    // 		modifiers.setAccessible(true);
    // 		modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
    // 		field.set(this, newValue);    		
    // 	} catch (Exception e) {
    // 		System.out.println(e.getMessage());
    // 	}
    // }
    
    // public String toString() {
    // 	return "\nCRON_EXPRESSION: " + CRON_EXPRESSION + "\n" + reminder.toString();
    // }
    
    // public String getCron() {
    //     return CRON_EXPRESSION;
    // }
    
    // public Reminder getReminder() {
	// 	return reminder;
	// }
    
    // public void setReminder(Reminder reminder) {
	// 	this.reminder = reminder;
	// }
}
