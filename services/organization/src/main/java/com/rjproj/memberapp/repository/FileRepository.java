package com.rjproj.memberapp.repository;

import com.rjproj.memberapp.model.File;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileRepository extends MongoRepository<File, Long> {

}
