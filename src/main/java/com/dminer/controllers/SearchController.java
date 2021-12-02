package com.dminer.controllers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import com.dminer.converters.NoticeConverter;
import com.dminer.converters.NotificationConverter;
import com.dminer.converters.ReminderConverter;
import com.dminer.converters.SurveyConverter;
import com.dminer.converters.UserConverter;
import com.dminer.dto.PostReductDTO;
import com.dminer.dto.SearchDTO;
import com.dminer.dto.SurveyDTO;
import com.dminer.dto.UserDTO;
import com.dminer.entities.Events;
import com.dminer.entities.Notice;
import com.dminer.entities.Notification;
import com.dminer.entities.Post;
import com.dminer.entities.Reminder;
import com.dminer.entities.Survey;
import com.dminer.entities.User;
import com.dminer.repository.GenericRepositoryPostgres;
import com.dminer.repository.GenericRepositorySqlServer;
import com.dminer.response.Response;
import com.dminer.services.EventsService;
import com.dminer.services.FeedService;
import com.dminer.services.NoticeService;
import com.dminer.services.NotificationService;
import com.dminer.services.ReminderService;
import com.dminer.services.SurveyService;
import com.dminer.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/search")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SearchController {
    

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ReminderService reminderService;

    @Autowired
    private EventsService eventsService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private GenericRepositoryPostgres genericRepositoryPostgres;

    @Autowired
    private GenericRepositorySqlServer genericRepositorySqlServer;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private NotificationConverter notificationConverter;
    
    @Autowired
    private NoticeConverter noticeConverter;
    
    @Autowired
    private ReminderConverter reminderConverter;
    
    @Autowired
    private SurveyService surveyService;

    @Autowired
    private SurveyConverter surveyConverter;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private FeedService feedService;
    
    
    
    private String token = null;
    
    @Autowired
    private Environment env;

    
    
    //@GetMapping()
    public ResponseEntity<Response<List<UserDTO>>> getAllEventTeste() throws IOException { 
    	// users
        if (token == null) {
        	token = userService.getToken();
        }
        
        //userService.carregarUsuariosApi(token);
        Response<List<UserDTO>> users = userService.carregarUsuariosApi2(token);

        return ResponseEntity.ok(users);
    }
    
    
    @GetMapping(value = "/{keyword}")
    public ResponseEntity<Response<SearchDTO>> getAllEvents2(@PathVariable String keyword) {
        
        Response<SearchDTO> response = new Response<>();
        SearchDTO searchDTO = new SearchDTO();
        
        if (keyword.equalsIgnoreCase("null")) keyword = null;

        // notification
        Optional<List<Notification>> searchNotification = notificationService.search(keyword);
        if (searchNotification.isPresent() &&  !searchNotification.get().isEmpty()) {
            searchNotification.get().forEach(u -> {
                searchDTO.getNotificationlist().add( notificationConverter.entityToDto(u) );
            });
        }

        // reminder
        Optional<List<Reminder>> searchReminder = reminderService.search(keyword);
        if (searchReminder.isPresent() && !searchReminder.get().isEmpty()) {
            searchReminder.get().forEach(u -> {
                searchDTO.getReminderList().add(reminderConverter.entityToDto(u));
            });
        }

        // users
//        if (token == null) {
//        	token = userService.getToken();
//        }
//        List<UserDTO> searchUsers = userService.search(keyword, token);            
//        searchUsers.forEach(u -> {            
//            searchDTO.getUsersList().add(u);
//        });
        

        if (isProd()) {
            
            // notice
            List<Notice> notices = genericRepositoryPostgres.searchNotice(keyword);
            if (!notices.isEmpty()) {
                notices.forEach(u -> {
                	searchDTO.getNoticeList().add(noticeConverter.entityToDTO(u));
                });
            } else {
                Optional<List<Notice>> result = noticeService.findAll();
                if (result.isPresent() &&  !result.get().isEmpty()) {
                    result.get().forEach(u -> {
                    	searchDTO.getNoticeList().add(noticeConverter.entityToDTO(u));
                    });
                }    
            }

            // events
            Optional<List<Events>> searchEvents = eventsService.searchPostgres(keyword);
            if (searchEvents.isPresent() &&  !searchEvents.get().isEmpty()) {
                searchEvents.get().forEach(u -> {
                    searchDTO.getEventsList().add(u);
                });
            }
            
            // surveys
            Optional<List<Survey>> searchSurvey = surveyService.searchPostgres(keyword);
            if (searchSurvey.isPresent() && !searchSurvey.get().isEmpty()) {
                searchSurvey.get().forEach(u -> {
                    searchDTO.getQuizList().add(surveyConverter.entityToDTO(u));
                });
            } else {
                searchSurvey = surveyService.findAll();
                if (searchSurvey.isPresent() && !searchSurvey.get().isEmpty()) {
                    searchSurvey.get().forEach(u -> {
                        searchDTO.getQuizList().add(surveyConverter.entityToDTO(u));
                    });
                }
            }

            // feed (post)
            List<PostReductDTO> searchFeed = feedService.searchPostgres(keyword);
            if (!searchFeed.isEmpty()) {
            	searchFeed.forEach(u -> {
            		searchDTO.getFeedList().add(u);
                });
            } else {
            	searchFeed = feedService.getReductAll();
            	searchFeed.forEach(u -> {
                    searchDTO.getFeedList().add(u);
                });
            }
            
        } else {

        	// notice
            List<Notice> notices = genericRepositorySqlServer.searchNotice(keyword);
            if (!notices.isEmpty()) {
                notices.forEach(u -> {
                	searchDTO.getNoticeList().add(noticeConverter.entityToDTO(u));
                });
            } else {
                Optional<List<Notice>> result = noticeService.findAll();
                if (result.isPresent() &&  !result.get().isEmpty()) {
                    result.get().forEach(u -> {
                        searchDTO.getNoticeList().add(noticeConverter.entityToDTO(u));
                    });
                }    
            }
            
            // events
            Optional<List<Events>> searchEvents = eventsService.search(keyword);
            if (searchEvents.isPresent() && !searchEvents.get().isEmpty()) {
                searchEvents.get().forEach(u -> {
                    searchDTO.getEventsList().add(u);
                });
            }
            
            // surveys
            Optional<List<Survey>> searchSurvey = surveyService.searchSqlServer(keyword);
            if (searchSurvey.isPresent() && !searchSurvey.get().isEmpty()) {
                searchSurvey.get().forEach(u -> {                    
                    searchDTO.getQuizList().add(surveyConverter.entityToDTO(u));
                });
            } else {
                searchSurvey = surveyService.findAll();
                if (searchSurvey.isPresent() && !searchSurvey.get().isEmpty()) {
                    searchSurvey.get().forEach(u -> {
                        searchDTO.getQuizList().add(surveyConverter.entityToDTO(u));
                    });
                }
            }
            
            // feed (post)
            List<PostReductDTO> searchFeed = feedService.searchSqlServer(keyword);
            if (!searchFeed.isEmpty()) {
            	searchFeed.forEach(u -> {
                    searchDTO.getFeedList().add(u);
                });
            } else {            	
            	searchFeed = feedService.getReductAll();
            	searchFeed.forEach(u -> {
                    searchDTO.getFeedList().add(u);
                });            
            }
        }

        response.setData(searchDTO);
        return ResponseEntity.ok().body(response);
    }


    public boolean isProd() {
        return Arrays.asList(env.getActiveProfiles()).contains("prod");
    }
}
