package com.mustycodified.MiddlewareAuthWithGraphAPI.routes;

import com.mustycodified.MiddlewareAuthWithGraphAPI.model.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


import static com.mustycodified.MiddlewareAuthWithGraphAPI.constants.Constant.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@RequiredArgsConstructor
public class MiddlewareRoute extends RouteBuilder {


    @Override
    public void configure() throws Exception {

        rest("/api")
                .post("/authentication/login")
                .description("Officer Login")
                .consumes(APPLICATION_JSON_VALUE)
                .produces(APPLICATION_JSON_VALUE)
                .responseMessage()
                .code(HttpStatus.OK.value()).message("Login Successful").endResponseMessage()
                .to("direct:authenticate-officer");

        from("direct:authenticate-officer")
                .log(LoggingLevel.INFO, "Received body (${body})")
                .unmarshal().json(JsonLibrary.Jackson, LoginRequest.class)
                .log(LoggingLevel.INFO, "Authenticating ${body.email}")
                .setHeader("CamelHttpMethod", constant("POST"))
                .to("direct:queryAzureAD")
                .log(LoggingLevel.INFO, "Querying Azure AD for ${body.email}")
                .process("officerLoginProcessor")
                .choice()
                .when(header(Exchange.HTTP_RESPONSE_CODE).isNotEqualTo(200))
                .throwException(new RuntimeException("Failed to login. HTTP Status code: ${header.CamelHttpResponseCode}"))
                .otherwise()
                .log(LoggingLevel.INFO, "Returning User information in JSON Format")
                .marshal().json(JsonLibrary.Jackson)
                .setHeader(Exchange.CONTENT_TYPE, constant(APPLICATION_JSON_VALUE))
                .choice()
                .when(simple("${exception.getClass().getName() == 'java.net.ConnectionException'}"))
                .log(LoggingLevel.ERROR, "Error Connecting to Microsoft graph API: ${exception}")
                .otherwise()
                .end()
                .end();

        from("direct:queryAzureAD")
                .routeId("queryAzureADRoute")
                .log(LoggingLevel.INFO, "Querying Azure AD with access token ${header.Authorization}")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setHeader(HEADER_STRING, simple( TOKEN_PREFIX + ACCESS_TOKEN))
                .toD("https://graph.microsoft.com/v1.0/users?$filter=userPrincipalName%20eq%20'${body.email}'&bridgeEndpoint=true")
                .convertBodyTo(String.class)
                .log("Azure AD Response: ${body}");

    }
}

//echo "# MicrosoftGraphAPI" >> README.md
//git init
//git add README.md
//git commit -m "first commit"
//git branch -M main
//git remote add origin https://github.com/musty-codified/MicrosoftGraphAPI.git
//git push -u origin main