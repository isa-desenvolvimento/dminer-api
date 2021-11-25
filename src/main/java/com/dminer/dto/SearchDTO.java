package com.dminer.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SearchDTO {
    private List<Object> noticeList;
    private List<Object> notificationlist;
    private List<Object> reminderList;
    private List<Object> birthdayList;
    private List<Object> eventsList;
    private List<Object> usersList;
    private List<Object> quizList;
}
