package com.dminer.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.dminer.entities.Notice;
import com.dminer.repository.GenericRepositoryPostgres;
import com.dminer.repository.GenericRepositorySqlServer;
import com.dminer.repository.NoticeRepository;
import com.dminer.services.interfaces.INoticeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoticeService implements INoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private GenericRepositoryPostgres genericRepositoryPostgres;

    @Autowired
    private GenericRepositorySqlServer genericRepositorySqlServer;


    @Override
    public Notice persist(Notice comment) {
        return noticeRepository.save(comment);
    }

    @Override
    public Optional<Notice> findById(int id) {
        return noticeRepository.findById(id);
    }

    @Override
    public Optional<List<Notice>> findAll() {
        return Optional.ofNullable(noticeRepository.findAllByOrderByDateDesc());
    }

    @Override
    public void delete(int id) {
        noticeRepository.deleteById(id);
    }
    

    public List<Notice> search(String keyword, String login, boolean prod) {
        List<Notice> notices = new ArrayList<>();
        if (keyword != null) {
            if (prod) {
                notices = genericRepositorySqlServer.searchNotice(keyword, login);
            } else {
                notices = genericRepositoryPostgres.searchNotice(keyword);
            }
            
        } else {
            notices = noticeRepository.findAllByOrderByDateDesc();
        }

        notices = notices.stream()
        .filter(u -> u.getCreator().equals(login))
        .collect(Collectors.toList());
        
        return notices;
    }
    
}
