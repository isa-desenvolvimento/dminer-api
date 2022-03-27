package com.dminer.entities;

import java.util.Date;

import lombok.Data;

@Data
public class TaskDefinition {

    private String cronExpression;
    private String actionType;
    private String data;
    private Long delay;
    private Date date;
}
