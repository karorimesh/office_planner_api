package com.tracom.office_planner.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserServiceClass implements SendMail {
    private final JavaMailSender mailSender;
    @Autowired
    private UserRepository userRepository;

    public UserServiceClass(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    @Override
    public void sendRegisterMail(User user, String reset) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("New User Registration");
        message.setTo(user.getUserEmail());
        message.setText("Below is a link to register to the office planner" +
                " click on to set up a profile and use our service, regards " +
                 reset);

        mailSender.send(message);
    }

    @Override
    public void sendForgotMail(User user, String reset) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("Forgot Password reset E-mail");
        message.setTo(user.getUserEmail());
        message.setText("This is a link to reset your password. Click to change the password " + reset);
        mailSender.send(message);
    }

    public void updateToken(String token, String email) throws UsernameNotFoundException{
        User user = userRepository.findByEmail(email);
        if(user != null){
            user.setToken(token);
            userRepository.save(user);
        }
        else{
            throw new UsernameNotFoundException("User with this email does not exist");
        }
    }

    public User getUserByToken(String token){
        return userRepository.findByToken(token);
    }

    public void updatePassword(User user, String password){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encoded = encoder.encode(password);
        user.setUserPassword(encoded);
        user.setToken(null);
        userRepository.save(user);
    }

    public void updateUser(User user, String username, String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encoded = encoder.encode(password);
        user.setUserPassword(encoded);
        user.setUserName(username);
        user.setToken(null);
        userRepository.save(user);
    }

    public Page<User> listAll(String keyword, int pageNo, String sortDir, String field){
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNo-1,pageSize,
                sortDir.equals("asc")? Sort.by(field).ascending():Sort.by(field).descending());
        if (keyword != null){
            return userRepository.search(keyword, pageable);
        }
        return userRepository.findAll(pageable);
    }
}
