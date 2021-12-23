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

import com.dminer.components.TokenService;
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
import com.dminer.entities.SurveyResponses;
import com.dminer.entities.User;
import com.dminer.repository.GenericRepositoryPostgres;
import com.dminer.repository.GenericRepositorySqlServer;
import com.dminer.repository.SurveyResponseRepository;
import com.dminer.response.Response;
import com.dminer.rest.model.users.UserRestModel;
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



    // @PostConstruct
    // private void init() {
    //     token = userService.getToken();
	// 	userRestModel = userService.carregarUsuariosApi(token);
    // }

    
    private Response<List<UserDTO>> aniversariantes() {
    	Response<List<UserDTO>> response = new Response<>();

    	UserRestModel userRestModel = userService.carregarUsuariosApi(TokenService.getToken());

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
        userRestModel.getOutput().getResult().getUsuarios().forEach(u -> {
        	if (u.getBirthDate() != null && UtilDataHora.isAniversariante(u.getBirthDate())) {
        		aniversariantes.add(u.toUserDTO());
        	}
        });
        
        if (aniversariantes.isEmpty()) {
            response.getErrors().add("Nenhum aniversariante encontrado");
            return response;
        }

        response.setData(aniversariantes);
        return response;
    }
    
    @GetMapping(value = "/{login}/{keyword}")
    @Transactional(timeout = 50000)
    public ResponseEntity<Response<SearchDTO>> getAllEvents(@PathVariable String login, @PathVariable String keyword) {
        
        Response<SearchDTO> response = new Response<>();
        SearchDTO searchDTO = new SearchDTO();
        
        if (keyword.equalsIgnoreCase("null")) keyword = null;

        List<UserDTO> searchUsers = userService.search(keyword);           
        searchUsers.forEach(u -> {        	
        	String encodedString = userService.getAvatarBase64ByLogin(login);
        	u.setAvatar(encodedString);
            searchDTO.getUsersList().add(u);
        });
                
        Response<List<UserDTO>> aniversariantes = aniversariantes();
        if (aniversariantes.getData() != null && !aniversariantes.getData().isEmpty()) {
        	aniversariantes.getData().forEach(ani -> {
                String encodedString = userService.getAvatarBase64ByLogin(login);
        	    ani.setAvatar(encodedString);
        		searchDTO.getBirthdayList().add(ani);
        	});
        }
        
        if (isProd()) {
            
            // reminder
            List<Reminder> searchReminder = genericRepositoryPostgres.searchReminder(keyword, login);
            if (searchReminder != null && !searchReminder.isEmpty()) {
                searchReminder.forEach(u -> {
                    searchDTO.getReminderList().add(reminderConverter.entityToDto(u));
                });
            } else {
                Optional<List<Reminder>> searchReminder2 = reminderService.findAll();
                if (searchReminder2.isPresent() &&  !searchReminder2.get().isEmpty()) {
                    searchReminder2.get().forEach(u -> {
                        if (u.getUser().getLogin().equals(login))
                            searchDTO.getReminderList().add( reminderConverter.entityToDto(u) );
                    });
                }
            }

            // Notification
            List<Notification> searchNotification = genericRepositoryPostgres.searchNotification(keyword, login);
            if (searchNotification != null &&  !searchNotification.isEmpty()) {
                searchNotification.forEach(u -> {
                    searchDTO.getNotificationlist().add( notificationConverter.entityToDto(u) );
                });
            } else {
                Optional<List<Notification>> all = notificationService.findAll();
                if (all.isPresent()) {
                    searchNotification = all.get();
                    searchNotification.forEach(u -> {
                        if (u.getUser().getLogin().equals(login))
                            searchDTO.getNotificationlist().add( notificationConverter.entityToDto(u) );
                    }); 
                }                
            }

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
            if (!searchSurvey.isPresent() || searchSurvey.get().isEmpty()) {
                searchSurvey = surveyService.findAll();
            }

            if (searchSurvey.isPresent() && !searchSurvey.get().isEmpty()) {
                List<Survey> surveys = searchSurvey.get();
                
                surveys = surveys.stream()
                .sorted(Comparator.comparing(Survey::getDate))
                .collect(Collectors.toList());
                
                surveys.forEach(u -> {
                    
                    SurveyResponses responseDto = surveyResponseRepository.findByIdSurvey(u.getId());
                    SurveyDTO dto = surveyConverter.entityToDTO(u);

                    if (responseDto != null) {
                        User user = responseDto.getUsers().stream().
                        filter(f -> f.getLogin().equalsIgnoreCase(login)).
                        findAny().
                        orElse(null);
            
                        if (user != null) {
                            dto.setVoted(true);
                        }
                    }

                    searchDTO.getQuizList().add(dto);
                });
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
