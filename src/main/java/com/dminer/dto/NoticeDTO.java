package com.dminer.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class NoticeDTO {
    
    private Integer id;
    //private List<Integer> users = new ArrayList<>();
    private List<UserReductDTO> users = new ArrayList<>();
    private String date;
    private String creator;
    private String warning;
    private Integer priority;
    private Boolean active;
}
