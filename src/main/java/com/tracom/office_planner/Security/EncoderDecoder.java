package com.tracom.office_planner.Security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncoderDecoder {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "11111111";
        String encrypted = "$2a$10$Xk0Rb83DqSWGZtyhB357Gei7uGUqwyZjEbxhoVq/e9KF2kCTrdSUm";
        System.out.println(encoder.encode(password));
        System.out.println(encoder.matches("22222222",encrypted));
    }
}
