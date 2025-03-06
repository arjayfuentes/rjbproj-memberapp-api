package com.rjproj.memberapp.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigPrinter {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${server.port}")
    private int serverPort;

    @Value("${eureka.client.service-url.defaultZone}")
    private String eurekaUrl;

    @Value("${spring.data.mongodb.host}")
    private String mongoHost;

    @Value("${spring.data.mongodb.port}")
    private int mongoPort;

    @Value("${spring.data.mongodb.database}")
    private String mongoDatabase;


    @PostConstruct
    public void printConfig() {
        System.out.println("Application Name: " + appName);
        System.out.println("Server Port: " + serverPort);
        System.out.println("Eureka URL: " + eurekaUrl);
        System.out.println("MongoDB Host: " + mongoHost);
        System.out.println("MongoDB Port: " + mongoPort);
        System.out.println("MongoDB Database: " + mongoDatabase);
    }
}
