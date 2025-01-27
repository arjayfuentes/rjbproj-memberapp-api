package com.rjproj.memberapp.repository;

import com.rjproj.memberapp.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<Notification, String> {
}