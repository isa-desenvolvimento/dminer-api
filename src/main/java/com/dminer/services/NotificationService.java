package com.dminer.services;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.dminer.converters.FullCalendarConverter;
import com.dminer.dto.FullCalendarRequestDTO;
import com.dminer.entities.FullCalendar;
import com.dminer.entities.Notice;
import com.dminer.entities.Notification;
import com.dminer.entities.Post;
import com.dminer.entities.User;
import com.dminer.repository.GenericRepositoryPostgres;
import com.dminer.repository.GenericRepositorySqlServer;
import com.dminer.repository.NotificationRepository;
import com.dminer.services.interfaces.INotificationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService implements INotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private GenericRepositoryPostgres genericRepositoryPostgres;

    @Autowired
    private GenericRepositorySqlServer genericRepositorySqlServer;

    @Autowired 
    private FullCalendarService fullCalendarService;

    @Autowired 
    private FullCalendarConverter fullCalendarConverter;


    
    @Override
    public Notification persist(Notification notification) {
        log.info("Persistindo notificação: {}", notification);
        return notificationRepository.save(notification);
    }

    @Override
    public Optional<Notification> findById(int id) {
        log.info("Buscando uma notificação pelo id {}", id);
		return notificationRepository.findById(id);
    }

    @Override
    public Optional<List<Notification>> findAll() {
        log.info("Buscando todas as notificações");
		return Optional.ofNullable(notificationRepository.findAll());
    }

    @Override
    public void delete(int id) {
        log.info("Excluindo uma notificação pelo id {}", id);
		notificationRepository.deleteById(id);		        
    }
    

    public void newNotificationFromPost(Post post, String login) {
        Notification notification = new Notification();
		notification.setCreateDate(Timestamp.from(Instant.now()));
        notification.setNotification("Usuário " + login + " fez um novo post! - " + post.getTitle() + ":" + post.getContent());
		notification.setAllUsers(true);
		persist(notification);
    }


    public void newNotificationFromNotice(Notice notice, String login) {
        Notification notification = new Notification();
		notification.setCreateDate(Timestamp.from(Instant.now()));
        if (notice.getCreator().equalsIgnoreCase(login)) {
            notification.setNotification("Você criou um novo aviso! - " + notice.getWarning());
        } else {
            notification.setNotification("Usuário " + login + " te marcou em um novo aviso! - " + notice.getWarning());
        }
		notification.setAllUsers(false);
        Optional<User> userTemp = userService.findByLogin(login);
        if (userTemp.isPresent()) {
            notification.setUser(userTemp.get());
            persist(notification);
        } else {
            log.info("Usuário {} não encontrado na base de dados local", login);
        }
    }


    /**
     * Cria uma notificação para o usuário que criou o evento calendário
     * @param user
     * @param content
     */
    public void newNotificationFromCalendarEvent(String user, String message, boolean allUsers) {

        Notification notification = new Notification();
        notification.setCreateDate(Timestamp.from(Instant.now()));
        notification.setNotification(message);
        notification.setAllUsers(allUsers);
        Optional<User> userTemp = userService.findByLogin(user);
        if (userTemp.isPresent()) {
            notification.setUser(userTemp.get());
            persist(notification);
        } else {
            log.info("Usuário {} não encontrado na base de dados local", user);
        }        
    }

    /**
     * Cria um evento de calendário nos usuários marcados 
     * @param dto
     */
    public void createUserCalendarEventByUsersCalendar(FullCalendarRequestDTO dto) {
        
        List<String> users = dto.getUsers();
        dto.setUsers(new ArrayList<>());

        users.forEach(user -> {
            log.info("Criando evento calendário para o usuário: " + user + " a partir de um evento calendário: {}", dto);
            FullCalendarRequestDTO dtoTemp = dto;
            dtoTemp.setCreator(user);
            fullCalendarService.persist(fullCalendarConverter.requestDtoToEntity(dtoTemp));
        });
        
    }


    public List<Notification> search(String keyword, String login, boolean isProd) {
        System.out.println("Termo de busca do search do notification: " + keyword);

        List<Notification> result = new ArrayList<>();
        if (keyword != null) {
            if (isProd) {
                result = genericRepositorySqlServer.searchNotification(keyword, login);
            } else {
                result = genericRepositoryPostgres.searchNotification(keyword, login);
            }          
        } else {
            Optional<User> user = userService.findByLogin(login);
            if (user.isPresent()) {
                result = notificationRepository.findByUserOrAllUsersOrderByCreateDateDesc(user.get(), true);
                // result = notificationRepository.findByUserOrderByCreateDateDesc(user.get());               
                System.out.println("Notificações by user: " + result.size());
            } else {
                result = notificationRepository.findByUserOrAllUsersOrderByCreateDateDesc(user.get(), true);
                System.out.println("Notificações all users: " + result.size());
            }
        }
        return result;
    }
}
