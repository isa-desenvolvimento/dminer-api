package com.dminer.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.dminer.converters.SurveyConverter;
import com.dminer.converters.UserConverter;
import com.dminer.dto.SearchDTO;
import com.dminer.dto.SurveyDTO;
import com.dminer.dto.UserDTO;
import com.dminer.entities.Events;
import com.dminer.entities.Notification;
import com.dminer.entities.Reminder;
import com.dminer.entities.Survey;
import com.dminer.entities.User;
import com.dminer.repository.GenericRepositoryPostgres;
import com.dminer.repository.GenericRepositorySqlServer;
import com.dminer.response.Response;
import com.dminer.services.EventsService;
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
    private SurveyService surveyService;

    @Autowired
    private Environment env;


    @GetMapping(value = "/{keyword}")
    public ResponseEntity<Response<SearchDTO>> getAllEvents(@PathVariable String keyword) {
        
        Response<SearchDTO> response = new Response<>();        
        //List<Object> dados = new ArrayList<>();
        SearchDTO searchDTO = new SearchDTO();
        
        if (keyword.equalsIgnoreCase("null")) keyword = null;

        // notification
        Optional<List<Notification>> searchNotification = notificationService.search(keyword);
        if (searchNotification.isPresent() &&  !searchNotification.get().isEmpty()) {
            searchNotification.get().forEach(u -> {
                searchDTO.getNotificationlist().add(u);
            });
        }

        // reminder
        Optional<List<Reminder>> searchReminder = reminderService.search(keyword);
        if (searchReminder.isPresent() && !searchReminder.get().isEmpty()) {
            searchReminder.get().forEach(u -> {
                //dados.add(u);
                searchDTO.getReminderList().add(u);
            });
        }

        if (isProd()) {
            
            // events
            Optional<List<Events>> searchEvents = eventsService.searchPostgres(keyword);
            if (searchEvents.isPresent() &&  !searchEvents.get().isEmpty()) {
                searchEvents.get().forEach(u -> {
                    //dados.add(u);
                    searchDTO.getEventsList().add(u);
                });
            }

            // users
            List<UserDTO> searchUsers = genericRepositoryPostgres.searchUsers(keyword);
            if (! searchUsers.isEmpty()) {
                searchUsers.forEach(u -> {
                    //dados.add(u);
                    searchDTO.getUsersList().add(u);
                });
            } else {
                Optional<List<User>> users = userService.findAll();
                if (users.isPresent() && !users.get().isEmpty()) {
                    users.get().forEach(user -> {
                        //dados.add(userConverter.entityToDto(user));
                        searchDTO.getUsersList().add(userConverter.entityToDto(user));
                    });
                }
            }
     
            // surveys
            Optional<List<Survey>> searchSurvey = surveyService.searchPostgres(keyword);
            if (searchSurvey.isPresent() && !searchSurvey.get().isEmpty()) {
                searchSurvey.get().forEach(u -> {
                    searchDTO.getQuizList().add(new SurveyConverter().entityToDTO(u));
                });
            } else {
                searchSurvey = surveyService.findAll();
                if (searchSurvey.isPresent() && !searchSurvey.get().isEmpty()) {
                    searchSurvey.get().forEach(u -> {
                        searchDTO.getQuizList().add(new SurveyConverter().entityToDTO(u));
                    });
                }
            }

        } else {

            // events
            Optional<List<Events>> searchEvents = eventsService.search(keyword);
            if (searchEvents.isPresent() && !searchEvents.get().isEmpty()) {
                searchEvents.get().forEach(u -> {
                    //dados.add(u);
                    searchDTO.getEventsList().add(u);
                });
            }

            // users
            List<UserDTO> searchUsers = genericRepositorySqlServer.searchUsers(keyword);
            if (! searchUsers.isEmpty()) {
                searchUsers.forEach(u -> {
                    //dados.add(u);
                    searchDTO.getUsersList().add(u);
                });
            } else {
                Optional<List<User>> users = userService.findAll();
                if (users.isPresent() && !users.get().isEmpty()) {
                    users.get().forEach(user -> {
                        //dados.add(userConverter.entityToDto(user));
                        searchDTO.getUsersList().add(userConverter.entityToDto(user));
                    });
                }
            }

            // surveys
            Optional<List<Survey>> searchSurvey = surveyService.searchSqlServer(keyword);
            if (searchSurvey.isPresent() && !searchSurvey.get().isEmpty()) {
                searchSurvey.get().forEach(u -> {
                    searchDTO.getQuizList().add(new SurveyConverter().entityToDTO(u));
                });
            } else {
                searchSurvey = surveyService.findAll();
                if (searchSurvey.isPresent() && !searchSurvey.get().isEmpty()) {
                    searchSurvey.get().forEach(u -> {
                        searchDTO.getQuizList().add(new SurveyConverter().entityToDTO(u));
                    });
                }
            }
        }

        response.setData(searchDTO);
        return ResponseEntity.ok().body(response);
    }


    public boolean isProd() {
        return Arrays.asList(env.getActiveProfiles()).contains("prod");
    }
}
