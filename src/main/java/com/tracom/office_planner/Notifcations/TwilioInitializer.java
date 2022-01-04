package com.tracom.office_planner.Notifcations;
/*
Initializer class to enable configurations to be autowired and send the sms
 */
import com.twilio.Twilio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioInitializer {
    private final SmsConfiguration smsConfiguration;

    @Autowired
    public TwilioInitializer(SmsConfiguration smsConfiguration) {
        this.smsConfiguration = smsConfiguration;
        Twilio.init(
                smsConfiguration.getAccountSid(),
                smsConfiguration.getAuthToken()
        );
    }
}
