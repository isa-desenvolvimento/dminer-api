package com.dminer.components;

import java.lang.reflect.Field;

import com.dminer.entities.Reminder;

import org.junit.Test;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LembreteAgendado {
    
    private static final String TIME_ZONE = "America/Sao_Paulo";

    final String CRON_EXPRESSION = "123";

    String teste;

    private Reminder reminder;

    @Scheduled(cron = CRON_EXPRESSION, zone = TIME_ZONE)
    public void execute() {

    }


    public String getCron() {
        return CRON_EXPRESSION;
    }
}
