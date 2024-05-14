package com.mustycodified.MiddlewareAuthWithGraphAPI.processor;

import com.google.gson.Gson;
import com.mustycodified.MiddlewareAuthWithGraphAPI.model.*;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.List;

import static com.mustycodified.MiddlewareAuthWithGraphAPI.constants.Constant.ACCESS_TOKEN;
import static com.mustycodified.MiddlewareAuthWithGraphAPI.constants.Constant.INTERNAL_CODE_001;

@Component
@RequiredArgsConstructor
public class OfficerLoginProcessor implements Processor {


    private static final Logger LOGGER = LoggerFactory.getLogger(OfficerLoginProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        LOGGER.info("Beginning of log - OfficerLoginProcessor");

        String responseBody = exchange.getIn().getBody(String.class);

        LoginRequest officer = exchange.getIn().getBody(LoginRequest.class); // Ensure body is Officer type
        String email = officer.getEmail();

        Gson gson = new Gson();
        UserResponse userResponse = gson.fromJson(responseBody, UserResponse.class);

        long officerId = userResponse.getUser().getId();
        String officerName = userResponse.getUser().getName();
        String officerNumber = userResponse.getUser().getPhoneNumber();
        String roleId = userResponse.getUser().getRole().getId();
        String roleName = userResponse.getUser().getRole().getName();
        boolean active = userResponse.isActive();
        String jwtToken = userResponse.getToken();
        String jwtRefreshToken = userResponse.getRefreshToken();

        if(isValidCredentials(email)){
            // Assuming you have logic to determine user role (e.g., "Officer")
            if (!roleName.equalsIgnoreCase("global administrators")) {
                throw new RuntimeException("Unauthorized access: Not an officer");
            }
            exchange.getIn().setHeader("roleId", roleId);
            exchange.getIn().setHeader(ACCESS_TOKEN, "accessToken");

            userResponse.getUser().getRole().getPermissions().forEach(permission -> {
                String roleTemplateId = permission.getId();
                String description = permission.getDescription();
                String name = permission.getName();

                UserResponse response = UserResponse.builder()
                        .token("generated-token")
                        .internalCode(INTERNAL_CODE_001)
                        .message("Login Success")
                        .success(true)
                        .active(true)
                        .user(new User(officerId, officerName, email, officerNumber,
                                new RoleDto(roleId, roleName, List.of(new PermissionDto(roleTemplateId, name, description))),
                                active, jwtToken, jwtRefreshToken))

                        .build();
                exchange.getIn().setBody(response);

            });

        } else {
            throw new RuntimeException("Invalid credentials");

        }

    }

    private boolean isValidCredentials(String email) {
        //perform validation against your authentication system (e.g., database)
        return email.equals("officer@zenpay.com") ;
    }

}