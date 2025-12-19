package com.enotes.auth.service;

import com.enotes.auth.enums.EmailPurpose;
import com.enotes.auth.dto.LoginRequest;
import com.enotes.auth.dto.LoginResponse;
import com.enotes.auth.dto.RegistrationDto;
import com.enotes.entity.RoleEntity;
import com.enotes.entity.UserEntity;
import com.enotes.exceptions.EmailException;
import com.enotes.exceptions.MobileNoException;
import com.enotes.exceptions.UserNotFoundException;
import com.enotes.repo.RoleRepo;
import com.enotes.repo.UserDetailRepo;
import com.enotes.security.CustomUserDetails;
import com.enotes.security.JWTService;
import com.enotes.service.impl.EmailServiceImpl;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@AllArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UserDetailRepo userDetailRepo;

    private final RoleRepo roleRepo;

    private final EmailServiceImpl emailService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JWTService jwtService;

    @Override
    public UserEntity registerUser(RegistrationDto dto) {
        if (userDetailRepo.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailException("user with this email is already present, please try new mail id");
        }

        if (userDetailRepo.findByMobileNo(dto.getMobileNo()).isPresent()) {
            throw new MobileNoException("user with this mobileNo is already present, please try new mobile no");
        }

        try {
            // Create new user entity
            UserEntity user = new UserEntity();
            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            user.setEmail(dto.getEmail());
            user.setMobileNo(dto.getMobileNo());
//       user.setPassword(dto.getPassword());// for testing
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
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
            sendVerificationLink(savedUser, EmailPurpose.EMAIL_VERIFICATION);

            return savedUser;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, Object> registerAdmin(RegistrationDto dto) {

        Map<String, Object> result = new HashMap<>();

        //check mail is present ot not
        Optional<UserEntity> existing = userDetailRepo.findByEmail(dto.getEmail());

        if (existing.isPresent()) {

            UserEntity user = existing.get();

            // User already exists → upgrade role
            RoleEntity adminRole = roleRepo.findById(2)
                    .orElseThrow(() -> new RuntimeException("Role ADMIN not found"));

            user.getRoles().add(adminRole);

            userDetailRepo.save(user);
            result.put("user", user);
            result.put("message", "Existing user upgraded to admin successfully.");

            return result;
        }

        // NEW ADMIN (never registered before)
        UserEntity admin = new UserEntity();

        admin.setFirstName(dto.getFirstName());
        admin.setLastName(dto.getLastName());
        admin.setEmail(dto.getEmail());
        admin.setMobileNo(dto.getMobileNo());
//        admin.setPassword(dto.getPassword());
        admin.setPassword(passwordEncoder.encode(dto.getPassword()));

        // Must verify
        String token = UUID.randomUUID().toString();
        admin.setVerificationCode(token);
        admin.setIsActive(false);

        // Assign admin role
        RoleEntity adminRole = roleRepo.findById(2)
                .orElseThrow(() -> new RuntimeException("Role ADMIN not found"));

        admin.getRoles().add(adminRole);

        UserEntity savedAdmin = userDetailRepo.save(admin);

        // Send verification email
        sendVerificationLink(savedAdmin, EmailPurpose.EMAIL_VERIFICATION);

        result.put("user", savedAdmin);
        result.put("message", "Admin registered successfully! Please check your email for verification link.");


        return result;
    }

    @Override
    public String verifyLink(String email, String code) {
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

    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        // 1. Authenticate user (Spring Security handles validation)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUserName(),
                        loginRequest.getPassword()
                )
        );

        // 2. Get authenticated principal
        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        // 3. Fetch full user entity (needed for JWT claims)
        UserEntity userEntity = userDetailRepo.findByEmail(userDetails.getUsername())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with email: " + userDetails.getUsername())
                );

        // 4. Generate JWT token
        String token = jwtService.generateToken(userEntity);

        // 5. Extract roles
        List<String> roleList = userEntity.getRoles()
                .stream()
                .map(RoleEntity::getRoleName)
                .toList();

        // 6. Build response
        LoginResponse response = new LoginResponse();
        response.setEmail(userEntity.getEmail());
        response.setToken(token);
        response.setRoles(roleList);

        return response;
    }

    @Override
    public void forgetPassword(String email) {

        System.out.println("User mail:- " + email);
        UserEntity userEntity = userDetailRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("mail not found, enter valid mail id"));

        try {
            String token = UUID.randomUUID().toString();
            System.out.println(token);

            userEntity.setVerificationCode(token);

            userDetailRepo.save(userEntity);

            sendVerificationLink(userEntity, EmailPurpose.PASSWORD_RESET);

        } catch (Exception e) {
            e.printStackTrace(); // This will show the root cause
            throw new  RuntimeException(e);
        }

    }

    @Override
    public String forgetPasswordReset(String code, String newPassword) {
        try {

            UserEntity user = userDetailRepo
                    .findByVerificationCode(code)
                    .orElseThrow(() -> new RuntimeException("Invalid or expired reset link"));

            user.setPassword(passwordEncoder.encode(newPassword));
            user.setVerificationCode(null);

            userDetailRepo.save(user);

            return "Password reset successfully. You can login now.";
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void sendVerificationLink(UserEntity user, EmailPurpose emailPurpose) {

        switch (emailPurpose) {
            case EMAIL_VERIFICATION:
                String link1 = "http://localhost:8085/api/auth/verify?email="
                        + user.getEmail()
                        + "&code="
                        + user.getVerificationCode();

                String subject1 = "Verify your email address";
                /*

                String body = "<h2>Welcome " + user.getFirstName() + "!</h2>"
                + "<p>Please click the link below to verify your email:</p>"
                + "<a href=\"" + link + "\">VERIFY EMAIL</a>"
                + "<br/><br/>"
                + "<p>If you did not register, ignore this email.</p>";

                */
                String body1 = verificationMailBody(user.getFirstName(), link1);

                try {
                    emailService.mimeEmailForm(user.getEmail(), subject1, body1);

                } catch (MessagingException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Failed to send verification email");
                }
                break;

            case PASSWORD_RESET:
                String link2 = "http://localhost:8085/api/auth/forget-pwd?code="
                        + user.getVerificationCode();
                String subject2 = "Password Reset For E-Notes Management";

                String body2 = resetPasswordMailBody(user.getFirstName(), link2);

                try {
                    emailService.mimeEmailForm(user.getEmail(), subject2, body2);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                break;

            default:
                System.out.println("Enter valid case!");
        }

    }

    // for mail verification user and admin
    private String verificationMailBody(String userName, String link){

        return "<!DOCTYPE html>" +
                "<html>" +
                "<body style=\"font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;\">" +

                "<div style=\"max-width: 600px; margin: auto; background: #ffffff; padding: 25px; " +
                "border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);\">" +

                "<h2 style=\"color: #333;\">Welcome " + userName + " 👋</h2>" +
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

    }

    //reset password mail for user and admin
    private String resetPasswordMailBody(String userName, String link){
        return  "<!DOCTYPE html>" +
                "<html>" +
                "<body style=\"font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;\">" +

                "<div style=\"max-width: 600px; margin: auto; background: #ffffff; padding: 25px; " +
                "border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);\">" +

                "<h2 style=\"color: #333;\">Hello " + userName + " 👋</h2>" +

                "<p style=\"font-size: 15px; color: #555;\">" +
                "We received a request to reset the password for your account." +
                "</p>" +

                "<p style=\"font-size: 15px; color: #555;\">" +
                "Click the button below to reset your password:" +
                "</p>" +

                // Button
                "<a href=\"" + link + "\" " +
                "style=\"display: inline-block; background-color: #4CAF50; color: white; padding: 12px 20px; " +
                "margin: 20px 0; text-decoration: none; font-weight: bold; border-radius: 5px;\">" +
                "Reset Password</a>" +

                "<p style=\"font-size: 14px; color: #888;\">" +
                "If the button doesn’t work, copy and paste the following link in your browser:<br>" +
                "<a href=\"" + link + "\" style=\"color: #4CAF50;\">" + link + "</a>" +
                "</p>" +

                "<p style=\"font-size: 15px; color: #555;\">" +
                "⏳ This link is valid for 15 minutes." +
                "</p>" +

                "<br/>" +

                "<p style=\"font-size: 15px; color: #555;\">" +
                "If you did not request this password reset, please ignore this email." +
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
    }
}
