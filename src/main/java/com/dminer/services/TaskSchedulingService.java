package com.dminer.services;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import com.dminer.utils.UtilDataHora;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@Service
public class TaskSchedulingService {

    @Autowired
    private TaskScheduler taskScheduler;

    Map<String, ScheduledFuture<?>> jobsMap = new HashMap<>();

    /**
     * Execute a task job initiate a timestamp less reverseMinutes param
     * @param jobId
     * @param tasklet
     * @param dateTime
     * @param reverseMinutes
     */
    public void scheduleATask(String jobId, Runnable tasklet, Timestamp dateTime, int reverseMinutes, ZoneId zoneId) {

        LocalDateTime startEvent = UtilDataHora.localDateTime(dateTime, zoneId);
        LocalDateTime now = LocalDateTime.now(zoneId);

        // ScheduledFuture<?> scheduledTask = taskScheduler.schedule(tasklet, startEvent.atZone(zoneId).toInstant());

        // se o evento for agendado para a data de hoje com diferença de apenas minutos, 
        // já pode disparar a thread
        // se não, precisa agendar na schedule
        if (isSameYearMonthDayHour(startEvent, now)) {
            tasklet.run();
        } else {
            startEvent = startEvent.minus(reverseMinutes, ChronoUnit.MINUTES);
            ScheduledFuture<?> scheduledTask = taskScheduler.schedule(tasklet, startEvent.atZone(zoneId).toInstant());
            jobsMap.put(jobId, scheduledTask);
        }
    }

    public void removeScheduledTask(String jobId) {
        ScheduledFuture<?> scheduledTask = jobsMap.get(jobId);
        if(scheduledTask != null) {
            scheduledTask.cancel(true);
            jobsMap.put(jobId, null);
        }
    }

    private boolean isSameYearMonthDayHour(LocalDateTime localDateTime1, LocalDateTime localDateTime2) {
        return 
        localDateTime1.getYear() == localDateTime2.getYear() && 
        localDateTime1.getMonth() == localDateTime2.getMonth() && 
        localDateTime1.getDayOfMonth() == localDateTime2.getDayOfMonth() && 
        localDateTime1.getHour() == localDateTime2.getHour();
    }
}
