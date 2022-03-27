package com.dminer.services;

import com.dminer.entities.TaskDefinition;
import com.dminer.utils.UtilDataHora;

import org.springframework.stereotype.Service;

@Service
public class TaskDefinitionBean implements Runnable {

    private TaskDefinition taskDefinition;

    @Override
    public void run() {
        System.out.println("Running action: " + taskDefinition.getActionType());
        System.out.println("With Data: " + taskDefinition.getData());
        System.out.println("In time: " + UtilDataHora.timestampToStringOrNull(taskDefinition.getDate().));
    }

    public TaskDefinition getTaskDefinition() {
        return taskDefinition;
    }

    public void setTaskDefinition(TaskDefinition taskDefinition) {
        this.taskDefinition = taskDefinition;
    }
}
