package com.rjproj.memberapp.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class FileService {

    @Value("${aws.s3.access.key}")
    private String awsS3AccessKey;

    @Value("${aws.s3.secret.key}")
    private String awsS3SecretKey;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    private AmazonS3 amazonS3;

    public FileService() {

    }

    public String uploadImage(String entity, UUID entityId, MultipartFile file) throws IOException {

        // Initialize AWS S3 Client
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsS3AccessKey, awsS3SecretKey);
        this.amazonS3 = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.fromName(region))
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();

        // Extract file extension
        String fileExtension = getFileExtension(file.getOriginalFilename());

        // Generate a unique random ID
        String randomId = UUID.randomUUID().toString();

        // Corrected file key (no extra `/` before id)
        String fileKey = entity + "-id-" + entityId + "-picId-" + randomId + "-" + fileExtension;

        // Get input stream
        InputStream inputStream = file.getInputStream();

        // Set metadata
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());

        // Upload file to S3
        amazonS3.putObject(bucketName, fileKey, inputStream, metadata);

        // Return the correct S3 URL
        return amazonS3.getUrl(bucketName, fileKey).toString();
    }


    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}

