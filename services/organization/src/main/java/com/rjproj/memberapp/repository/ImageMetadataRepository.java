package com.rjproj.memberapp.repository;

import com.rjproj.memberapp.model.ImageMetadata;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ImageMetadataRepository extends MongoRepository<ImageMetadata, String> {
    Optional<ImageMetadata> findByFilename(String filename);

}