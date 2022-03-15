package com.dminer.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.dminer.converters.NoticeConverter;
import com.dminer.converters.NotificationConverter;
import com.dminer.converters.ReminderConverter;
import com.dminer.dto.PostReductDTO;
import com.dminer.dto.SearchDTO;
import com.dminer.dto.SurveyDTO;
import com.dminer.dto.Token;
import com.dminer.dto.UserDTO;
import com.dminer.entities.Events;
import com.dminer.entities.Notice;
import com.dminer.entities.Notification;
import com.dminer.entities.Reminder;
import com.dminer.response.Response;
import com.dminer.services.EventsService;
import com.dminer.services.FeedService;
import com.dminer.services.NoticeService;
import com.dminer.services.NotificationService;
import com.dminer.services.ReminderService;
import com.dminer.services.SurveyService;
import com.dminer.services.UserService;
import com.dminer.utils.UtilDataHora;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
@CrossOrigin(origins = "*")
public class SearchController {
    
    private static final Logger log = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ReminderService reminderService;

    @Autowired
    private EventsService eventsService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private NotificationConverter notificationConverter;
    
    @Autowired
    private NoticeConverter noticeConverter;

    @Autowired
    private ReminderConverter reminderConverter;
    
    @Autowired
    private SurveyService surveyService;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private FeedService feedService;

    @Autowired
    private Environment env;



    private Response<List<UserDTO>> aniversariantes(List<UserDTO> usuarios) {
        Response<List<UserDTO>> response = new Response<>();
        List<UserDTO> aniversariantes = new ArrayList<UserDTO>();
        for (UserDTO u : usuarios) {
            if (u.getBirthDate() != null && UtilDataHora.isAniversariante(u.getBirthDate())) {
                aniversariantes.add(u);
            }            
        }
        response.setData(aniversariantes);
        return response;
    }

    private Response<List<UserDTO>> aniversariantes(String token, String keyword) {
    	Response<List<UserDTO>> response = new Response<>();

        if (token == null || token.isBlank()) {
            response.getErrors().add("Token precisa ser informado");    		
    		return response;
        }

        List<UserDTO> aniversariantes = userService.getAniversariantes(token, true);
        
        if (keyword != null) {
            aniversariantes = userService.search(aniversariantes, keyword);
            if (aniversariantes.isEmpty()) {
                response.getErrors().add("Nenhum aniversariante encontrado");
                return response;
            }
        }

        response.setData(aniversariantes);
        return response;
    }
    


    @GetMapping(value = "/{login}/{keyword}")
    @Transactional(timeout = 90000)
    public ResponseEntity<Response<SearchDTO>> getAllEvents(@RequestHeader("x-access-token") Token token, @PathVariable String login, @PathVariable String keyword) {
        
        Response<SearchDTO> response = new Response<>();
        SearchDTO searchDTO = new SearchDTO();
        
        if (token.naoPreenchido()) { 
            response.getErrors().add("Token precisa ser informado");    		
    		return ResponseEntity.badRequest().body(response);
        }

        if (keyword.equalsIgnoreCase("null")) keyword = null;

        // usuarios
        List<UserDTO> searchUsers = userService.search(keyword, token.getToken());
        searchUsers.parallelStream().forEach(u -> {        	
            searchDTO.getUsersList().add(u);
        });
        

        // aniversariantes
        Response<List<UserDTO>> aniversariantes = null;
        if (keyword == null) {
            aniversariantes = aniversariantes(searchUsers);
        } else {
            aniversariantes = aniversariantes(token.getToken(), keyword);
        }

        if (aniversariantes.getData() != null && !aniversariantes.getData().isEmpty()) {
            aniversariantes.getData().parallelStream().forEach(ani -> {
                searchDTO.getBirthdayList().add(ani);
            });
        }

        // reminder
        List<Reminder> searchReminder = reminderService.search(keyword, login, isProd());
        if (!searchReminder.isEmpty()) {
            searchReminder.forEach(u -> {
                searchDTO.getReminderList().add(reminderConverter.entityToDto(u));
            });
        }

        // Notification
        List<Notification> notifications = notificationService.search(keyword, login, isProd());
        if (!notifications.isEmpty()) {
            notifications.forEach(u -> {            
                searchDTO.getNotificationList().add( notificationConverter.entityToDto(u) );
            }); 
        }        

        // notice
        List<Notice> notices = noticeService.search(keyword, isProd());
        if (!notices.isEmpty()) {
            notices.forEach(u -> {
                searchDTO.getNoticeList().add(noticeConverter.entityToDTO(u));
            });
        }

        // events
        List<Events> events = eventsService.search(keyword, isProd());
        if (!events.isEmpty()) {
            events.forEach(u -> {
                searchDTO.getEventsList().add(u);
            });
        }

        // surveys
        List<SurveyDTO> searchSurvey = surveyService.search(keyword, login, isProd());
        if (!searchSurvey.isEmpty()) {
            searchSurvey.forEach(u -> {
                searchDTO.getSurveyList().add(u);
            });
        }


        // feed (post)
        List<PostReductDTO> searchFeed = feedService.search(keyword, login, isProd());
        if (!searchFeed.isEmpty()) {
            searchFeed.forEach(u -> {
                searchDTO.getFeedList().add(u);
            });
        }

        response.setData(searchDTO);
        return ResponseEntity.ok().body(response);
    }


    public boolean isProd() {
        return Arrays.asList(env.getActiveProfiles()).contains("prod");
    }

}
