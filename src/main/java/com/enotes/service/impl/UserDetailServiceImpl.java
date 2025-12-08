package com.enotes.service.impl;

import com.enotes.dto.RegistrationDto;
import com.enotes.entity.RoleEntity;
import com.enotes.entity.UserEntity;
import com.enotes.exceptions.EmailException;
import com.enotes.exceptions.MobileNoException;
import com.enotes.repo.RoleRepo;
import com.enotes.repo.UserDetailRepo;
import com.enotes.service.UserDetailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserDetailServiceImpl implements UserDetailService {

    @Autowired
    private UserDetailRepo userDetailRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private EmailServiceImpl emailService;

    @Override
    public UserEntity registerUser(RegistrationDto dto) {

        if(userDetailRepo.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailException("user with this email is already present, please try new mail id");
        }

        if(userDetailRepo.findByMobileNo(dto.getMobileNo()).isPresent()) {
            throw new MobileNoException("user with this mobileNo is already present, please try new mobile no");
        }

   try {
       // Create new user entity
       UserEntity user = new UserEntity();
       user.setFirstName(dto.getFirstName());
       user.setLastName(dto.getLastName());
       user.setEmail(dto.getEmail());
       user.setMobileNo(dto.getMobileNo());
       user.setPassword(dto.getPassword());// for testing
//        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        /*
         //for testing purpose
         user.setIsActive(true);
        String code = UUID.randomUUID().toString().substring(0, 6); // 6-digit code
        user.setVerificationCode(code);
         */
       // for varification link
       String token = UUID.randomUUID().toString();
       user.setVerificationCode(token);
       user.setIsActive(false);

       // Assign default role
       RoleEntity roleUser = roleRepo.findById(1)
               .orElseThrow(() -> new RuntimeException("Role USER not found"));

       user.getRoles().add(roleUser);

       UserEntity savedUser = userDetailRepo.save(user);

       //now send email with verification link
       sendVerificationLink(savedUser);

       return savedUser;
   } catch (Exception e) {
       e.printStackTrace();
       throw new RuntimeException(e);
   }
    }


    private void sendVerificationLink(UserEntity user) {

        String link = "http://localhost:8085/api/auth/verify?email="
                + user.getEmail()
                + "&code="
                + user.getVerificationCode();

        String subject = "Verify your email address";
//        String body = "<h2>Welcome " + user.getFirstName() + "!</h2>"
//                + "<p>Please click the link below to verify your email:</p>"
//                + "<a href=\"" + link + "\">VERIFY EMAIL</a>"
//                + "<br/><br/>"
//                + "<p>If you did not register, ignore this email.</p>";
        String body =
                "<!DOCTYPE html>" +
                        "<html>" +
                        "<body style=\"font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;\">" +

                        "<div style=\"max-width: 600px; margin: auto; background: #ffffff; padding: 25px; " +
                        "border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);\">" +

                        "<h2 style=\"color: #333;\">Welcome " + user.getFirstName() + " 👋</h2>" +
                        "<p style=\"font-size: 15px; color: #555;\">" +
                        "Thank you for registering with us. Please verify your email address by clicking the button below:" +
                        "</p>" +

                        // Button
                        "<a href=\"" + link + "\" " +
                        "style=\"display: inline-block; background-color: #4CAF50; color: white; padding: 12px 20px; " +
                        "margin: 20px 0; text-decoration: none; font-weight: bold; border-radius: 5px;\">" +
                        "Verify Email</a>" +

                        "<p style=\"font-size: 14px; color: #888;\">" +
                        "If the button doesn’t work, copy and paste the following link in your browser:<br>" +
                        "<a href=\"" + link + "\" style=\"color: #4CAF50;\">" + link + "</a>" +
                        "</p>" +

                        "<br/>" +

                        "<p style=\"font-size: 15px; color: #555;\">" +
                        "If you did not request this registration, please ignore this email." +
                        "</p>" +

                        "<br/>" +

                        "<p style=\"font-size: 15px; color: #333; font-weight: bold;\">Regards,</p>" +
                        "<p style=\"font-size: 15px; color: #555;\">The Support Team</p>" +

                        "<hr style=\"margin-top: 30px; border: none; border-top: 1px solid #eee;\"/>" +
                        "<p style=\"font-size: 12px; color: #aaa; text-align: center;\">" +
                        "This is an automated email. Please do not reply." +
                        "</p>" +

                        "</div>" +
                        "</body>" +
                        "</html>";


        try {

           emailService.mimeEmailForm(user.getEmail(), subject, body);

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send verification email");
        }
    }

    @Override
    public String verifyLink(String email, String code){

        UserEntity user = userDetailRepo.findByEmail(email)
                .orElseThrow(() -> new EmailException("Email not found"));

       try {
           if (user.getIsActive())
               return "Email already verified";

           if (!code.equals(user.getVerificationCode()))
               throw new RuntimeException("Invalid verification link");

           user.setIsActive(true);
           user.setVerificationCode(null);
           userDetailRepo.save(user);

           return "Email verified successfully!";
       } catch (Exception e) {
           e.printStackTrace();
           throw new RuntimeException(e);
       }
    }
}
