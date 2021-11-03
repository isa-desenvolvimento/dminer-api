package com.dminer.repository.elasticsearch;

import java.util.List;

import com.dminer.entities.elasticsearch.NotificationES;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface NotificationESRepository extends ElasticsearchRepository<NotificationES, String> {
    
    List<NotificationES> findByNotificationDescrible(String notificationDescrible);

}
