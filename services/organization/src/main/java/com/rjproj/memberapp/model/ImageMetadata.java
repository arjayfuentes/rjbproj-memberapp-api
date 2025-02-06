package com.rjproj.memberapp.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Document
public class ImageMetadata {

    @Id
    private String id;
    private String filename;
    private String filetype;
    private String filepath;
    private String imageType;
    // Store path instead of file data

//    private byte[] imageData;
    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
