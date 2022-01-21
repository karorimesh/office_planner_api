package com.tracom.office_planner.Security;

/*
Class to carry out test on bcrypt encoding
 */

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncoderDecoder {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "11111111";
        String encrypted = "$2a$10$A3b0L5Age3rizl7.rgFJsuP3Fm9yBheFtXIaHpiYtCnUXqD7BY/lG";
        System.out.println(encoder.encode(password));
        System.out.println(encoder.matches("11111111",encrypted));
    }
}
