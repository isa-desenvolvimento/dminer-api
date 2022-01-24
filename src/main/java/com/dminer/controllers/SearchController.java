package com.dminer.controllers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.ws.rs.HeaderParam;

import com.dminer.components.TokenService;
import com.dminer.converters.NoticeConverter;
import com.dminer.converters.NotificationConverter;
import com.dminer.converters.ReminderConverter;
import com.dminer.converters.SurveyConverter;
import com.dminer.converters.UserConverter;
import com.dminer.dto.PostReductDTO;
import com.dminer.dto.SearchDTO;
import com.dminer.dto.SurveyDTO;
import com.dminer.dto.Token;
import com.dminer.dto.UserDTO;
import com.dminer.entities.Events;
import com.dminer.entities.Notice;
import com.dminer.entities.Notification;
import com.dminer.entities.Post;
import com.dminer.entities.Reminder;
import com.dminer.entities.Survey;
import com.dminer.entities.SurveyResponses;
import com.dminer.entities.User;
import com.dminer.repository.GenericRepositoryPostgres;
import com.dminer.repository.GenericRepositorySqlServer;
import com.dminer.repository.SurveyResponseRepository;
import com.dminer.response.Response;
import com.dminer.rest.model.users.UserRestModel;
import com.dminer.rest.model.users.Usuario;
import com.dminer.services.EventsService;
import com.dminer.services.FeedService;
import com.dminer.services.NoticeService;
import com.dminer.services.NotificationService;
import com.dminer.services.ReminderService;
import com.dminer.services.SurveyService;
import com.dminer.services.UserService;
import com.dminer.utils.UtilDataHora;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/search")
@CrossOrigin(origins = "*")
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

    @Autowired
    private SurveyResponseRepository surveyResponseRepository;
    
	// private UserRestModel userRestModel;
    
    // @Autowired
	// private TokenService tokenService;
    
    @Autowired
    private Environment env;


    
    private Response<List<UserDTO>> aniversariantes(String token, String keyword) {
    	Response<List<UserDTO>> response = new Response<>();

        if (token == null || token.isBlank()) {
            response.getErrors().add("Token precisa ser informado");    		
    		return response;
        }

        
    	UserRestModel userRestModel = userService.carregarUsuariosApi(token);

        if (userRestModel == null) {
    		response.getErrors().add("Nenhum usuario encontrado");    		
    		return response;
    	}
        
        if (userRestModel.hasError()) {
        	userRestModel.getOutput().getMessages().forEach(u -> {
    			response.getErrors().add(u);
    		});
        	return response;
        }
        List<UserDTO> aniversariantes = new ArrayList<UserDTO>();
        for (Usuario u : userRestModel.getOutput().getResult().getUsuarios()) {
            if (u.getBirthDate() != null && UtilDataHora.isAniversariante(u.getBirthDate())) {
                aniversariantes.add(u.toUserDTO());
            }            
        }
        
        aniversariantes = userService.search(keyword, aniversariantes);
        if (aniversariantes.isEmpty()) {
            response.getErrors().add("Nenhum aniversariante encontrado");
            return response;
        }

        response.setData(aniversariantes);
        return response;
    }
    


    @GetMapping(value = "/{login}/{keyword}")
    @Transactional(timeout = 50000)
    public ResponseEntity<Response<SearchDTO>> getAllEvents(@HeaderParam("x-access-token") Token token, @PathVariable String login, @PathVariable String keyword) {
        
        Response<SearchDTO> response = new Response<>();
        SearchDTO searchDTO = new SearchDTO();
        
        if (token.naoPreenchido()) { 
            response.getErrors().add("Token precisa ser informado");    		
    		return ResponseEntity.badRequest().body(response);
        }

        if (keyword.equalsIgnoreCase("null")) keyword = null;

        // usuarios
        List<UserDTO> searchUsers = userService.search(keyword, token.getToken());
        searchUsers.forEach(u -> {        	
        	String encodedString = userService.getAvatarBase64ByLogin(login);
        	u.setAvatar(encodedString);
            searchDTO.getUsersList().add(u);
        });
            
        // aniversariantes
        Response<List<UserDTO>> aniversariantes = aniversariantes(token.getToken(), keyword);
        if (aniversariantes.getData() != null && !aniversariantes.getData().isEmpty()) {
        	aniversariantes.getData().forEach(ani -> {
                String encodedString = userService.getAvatarBase64ByLogin(login);
        	    ani.setAvatar(encodedString);
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
                searchDTO.getNotificationlist().add( notificationConverter.entityToDto(u) );
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
                searchDTO.getQuizList().add(u);
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
