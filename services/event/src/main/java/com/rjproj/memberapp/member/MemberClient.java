package com.rjproj.memberapp.member;

import com.rjproj.memberapp.exception.BusinessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
@RequiredArgsConstructor
public class MemberClient {

    @Value("${application.config.member-url}")
    private String memberUrl;
    private final RestTemplate restTemplate;

    public MemberResponse getMember(UUID memberId) {
        // Append the memberId to the base URL
        String urlWithId = String.format("%s/%s", memberUrl, memberId);

        // Log the URL for debugging purposes (optional)
        System.out.println("Fetching member details from URL: " + urlWithId);

        // Set the request headers
        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);

        // Create the HTTP entity with headers (no body for GET request)
        HttpEntity<?> requestEntity = new HttpEntity<>(null, headers);

        try {
            // Send the GET request to the external service
            ResponseEntity<MemberResponse> responseEntity = restTemplate.exchange(
                    urlWithId,
                    GET,
                    requestEntity,
                    MemberResponse.class
            );

            // Check if the response status is not 2xx
            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                throw new BusinessException("An error occurred while fetching the member: " + responseEntity.getStatusCode());
            }

            // Get the response body (MemberResponse)
            MemberResponse response = responseEntity.getBody();

            if (response == null) {
                throw new BusinessException("Received null response body for member ID: " + memberId);
            }

            // Log successful fetch (optional)
            System.out.println("Successfully fetched member details for ID: " + memberId);
            return response;

        } catch (Exception ex) {
            // Log error in case of an exception
            System.err.println("Exception occurred while fetching member details for ID: " + memberId + " - " + ex.getMessage());
            throw new BusinessException("Failed to fetch member details: " + ex.getMessage());
        }
    }


}


//String urlWithId = String.format("%s/%s", memberUrl, memberId);
//        log.info("Fetching member details from URL: {}", urlWithId);
//
//HttpHeaders headers = new HttpHeaders();
//        headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);
//
//HttpEntity<?> requestEntity = new HttpEntity<>(null, headers);
//
//        try {
//ResponseEntity<MemberResponse> responseEntity = restTemplate.exchange(
//        urlWithId,
//        GET,
//        requestEntity,
//        MemberResponse.class
//);
//
//            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
//        log.error("Non-success response for member ID {}: {}", memberId, responseEntity.getStatusCode());
//        throw new BusinessException("Error processing request: " + responseEntity.getStatusCode());
//        }
//
//MemberResponse response = responseEntity.getBody();
//            if (response == null) {
//        log.error("Received null response body for member ID {}", memberId);
//                throw new BusinessException("Member response body is null for ID: " + memberId);
//            }
//
//                    log.info("Successfully fetched member details for ID: {}", memberId);
//            return response;
//
//        } catch (Exception ex) {
//        log.error("Exception occurred while fetching member details for ID {}: {}", memberId, ex.getMessage());
//        throw new BusinessException("Failed to fetch member details: " + ex.getMessage(), ex);
//        }
//        }