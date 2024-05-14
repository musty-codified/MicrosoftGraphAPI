package com.mustycodified.MiddlewareAuthWithGraphAPI.service;

import lombok.RequiredArgsConstructor;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class BackendService {

    private final ProducerTemplate producerTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(BackendService.class);


    public String authenticateOfficer(String email, String encryptedPassword) {
        LOGGER.info("Beginning of log - Backend Service");


        String requestBody = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, encryptedPassword);
        String userResponse = producerTemplate.requestBody("direct:authenticate-officer", requestBody, String.class);

        if(userResponse.contains("success")){

            String credentials = producerTemplate.requestBody("direct:queryCredentials", email, String.class);
            return credentials;

        } else {
            return userResponse;

        }
    }
}
