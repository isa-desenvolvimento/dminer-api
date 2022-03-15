package com.dminer.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class SearchDTO {
    private List<Object> noticeList;
    private List<Object> notificationList;
    private List<Object> reminderList;
    private List<Object> birthdayList;
    private List<Object> eventsList;
    private List<Object> usersList;
    private List<Object> surveyList;
    private List<Object> feedList;

    public SearchDTO() {
        noticeList = new ArrayList<>();
        notificationList = new ArrayList<>();
        reminderList = new ArrayList<>();
        birthdayList = new ArrayList<>();
        eventsList = new ArrayList<>();
        usersList = new ArrayList<>();
        surveyList = new ArrayList<>();
        feedList = new ArrayList<>();
    }
}
