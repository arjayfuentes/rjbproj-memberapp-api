package com.rjproj.memberapp.repository;

import com.rjproj.memberapp.model.File;
import com.rjproj.memberapp.model.Organization;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FileRepository extends MongoRepository<File, Long> {

}
