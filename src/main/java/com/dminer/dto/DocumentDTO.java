package com.dminer.dto;


//import io.swagger.v3.oas.annotations.Parameter;
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
public class DocumentDTO {
    private Integer id;
    private String title;
    private Integer category; 
    private Boolean permission;
    private String contentLink;
    private String contentLinkDownload;
}
