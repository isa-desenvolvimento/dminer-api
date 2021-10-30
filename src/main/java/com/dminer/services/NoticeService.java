package com.dminer.services;

import java.util.List;
import java.util.Optional;

import com.dminer.entities.Notice;
import com.dminer.repository.NoticeRepository;
import com.dminer.services.interfaces.INoticeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoticeService implements INoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

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
        return Optional.ofNullable(noticeRepository.findAll());
    }

    @Override
    public void delete(int id) {
        noticeRepository.deleteById(id);
    }
    
}
