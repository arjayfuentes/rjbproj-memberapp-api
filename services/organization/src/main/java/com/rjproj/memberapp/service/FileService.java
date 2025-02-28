package com.rjproj.memberapp.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.rjproj.memberapp.model.File;
import com.rjproj.memberapp.model.ImageType;
import com.rjproj.memberapp.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class FileService {

    @Value("${aws.s3.access.key}")
    private String awsS3AccessKey;

    @Value("${aws.s3.secret.key}")
    private String awsS3SecretKey;

    @Autowired
    private Environment env;

    @Autowired
    private FileRepository fileRepository;

    public File saveFile(String entity, String entityId, ImageType imageType, String fileName, MultipartFile file) {

        String saveFileURl = saveFileToAWSS3Bucket(entity, entityId, imageType, file);

        File fileToSave = File.builder()
                .fileUrl(saveFileURl)
                .name(fileName)
                .build();
        return fileRepository.save(fileToSave);
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "jpg";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private String saveFileToAWSS3Bucket(String entity, String entityId, ImageType imageType, MultipartFile file) {
        try {
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsS3AccessKey, awsS3SecretKey);

            AmazonS3 amazonS3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(Regions.EU_NORTH_1)
                    .build();

            // Extract file extension
            String fileExtension = getFileExtension(file.getOriginalFilename());

            // Corrected file key (no extra `/` before id)
            String fileKey = entity + "/" + entityId + "/" + entityId + "-" + imageType.getValue() + "."  + fileExtension;

            InputStream inputStream = file.getInputStream();
            ObjectMetadata objectMetadata = new ObjectMetadata();

            objectMetadata.setContentType("image/jpeg");
            String bucketName = "arjay-fileupload";
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileKey, inputStream, objectMetadata);
            amazonS3Client.putObject(putObjectRequest);
            return amazonS3Client.getUrl(bucketName, fileKey).toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }



}

