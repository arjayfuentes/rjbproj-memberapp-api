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
        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);

        HttpEntity<MemberResponse> requestEntity = new HttpEntity<>(null, headers);

        ResponseEntity<MemberResponse> responseEntity = restTemplate.exchange(
                memberUrl,
                GET,
                requestEntity,
                MemberResponse.class
        );

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new BusinessException("An error occurred while processing the organization: " + responseEntity.getStatusCode());
        }

        return responseEntity.getBody();
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