package com.tracom.office_planner.Notifcations;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;


//Lombok
@ToString
@AllArgsConstructor
@Getter
@Setter

//Twilio configuration to read the properties in the application.properties file
@Configuration
@ConfigurationProperties("twilio")
@EnableConfigurationProperties
public class SmsConfiguration {
    private String accountSid;
    private String authToken;
    private String phoneNumber;

    public SmsConfiguration() {
    }
}
