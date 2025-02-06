package com.rjproj.memberapp.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.rjproj.memberapp.model.File;
import com.rjproj.memberapp.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;



@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private Environment env;

    @Value("${aws.s3.access.key}")
    private String awsS3AccessKey;

    @Value("${aws.s3.secret.key}")
    private String awsS3SecretKey;

    public File saveFile(MultipartFile file, String name) {

        String saveFileURl = saveFileToAWSS3Bucket(file);

        File fileToSave = File.builder()
                .fileUrl(saveFileURl)
                .name(name)
                .build();
        return fileRepository.save(fileToSave);
    }

    public List<File> getAllFiles() {
        return fileRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }


    private String saveFileToAWSS3Bucket(MultipartFile file) {
        try {
            String s3FileName = file.getOriginalFilename();

            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsS3AccessKey, awsS3SecretKey);

            AmazonS3 amazonS3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(Regions.EU_NORTH_1)
                    .build();

            InputStream inputStream = file.getInputStream();
            ObjectMetadata objectMetadata = new ObjectMetadata();

            objectMetadata.setContentType("image/jpeg");
            String bucketName = "arjay-fileupload";
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3FileName, inputStream, objectMetadata);
            amazonS3Client.putObject(putObjectRequest);
            return "https://" + bucketName + ".s3.amazonaws.com/" + s3FileName;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

}

