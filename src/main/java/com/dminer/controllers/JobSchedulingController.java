package com.dminer.controllers;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.UUID;

import com.dminer.entities.TaskDefinition;
import com.dminer.services.TaskDefinitionRunnable;
import com.dminer.services.TaskSchedulingService;
import com.dminer.sse.SseEmitterEvents;
import com.dminer.utils.UtilDataHora;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/schedule")
public class JobSchedulingController {

    @Autowired
    private TaskSchedulingService taskSchedulingService;

    @Autowired
    private TaskDefinitionRunnable taskDefinitionBean;

    @Autowired
    private SseEmitterEvents sseEmitterEvents;

    @PostMapping(path="/task-def", consumes = "application/json", produces="application/json")
    public void scheduleATask(@RequestBody TaskDefinition taskDefinition) {

        taskDefinitionBean.setSseEmitterEvent(sseEmitterEvents, taskDefinition.getData());
        Timestamp initTimestamp = UtilDataHora.toTimestamp(taskDefinition.getDateTime());

        taskSchedulingService.scheduleATask(
            UUID.randomUUID().toString(), taskDefinitionBean, initTimestamp, taskDefinition.getMinutesReverse(), ZoneId.of("America/Sao_Paulo")
        );
    }

    @GetMapping(path="/remove/{jobid}")
    public void removeJob(@PathVariable String jobid) {
        taskSchedulingService.removeScheduledTask(jobid);
    }
}
