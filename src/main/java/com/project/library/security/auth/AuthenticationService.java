package com.project.library.security.auth;


import com.project.library.exception.BadCredentialsE;
import com.project.library.exception.BadRequestException;
import com.project.library.exception.ResourceNotFoundException;
import com.project.library.security.config.ExtractToken;
import com.project.library.security.config.JwtService;

import com.project.library.security.otp.*;
import com.project.library.security.user.*;
import jakarta.mail.MessagingException;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final OTPInfoRepository otpInfoRepository;
    private  final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ExtractToken extractToken;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    public String register(RegisterRequest request) throws BadCredentialsE, MessagingException, UnsupportedEncodingException {
        if(repository.findByEmail(request.getEmail()).isPresent())
            throw new BadCredentialsE("User with this email already exists.");
        //check if registered too, if not, resend otp

        String reason = "complete your registration by verifying your email";
        OTPInfo otpInfo = new OTPInfo();
        otpInfo.setSecretKey(OTPService.generateRandomSecretKey());
        //otpInfo.setSecretKey("654321");
        emailService.sendOTP(request.getEmail(),otpInfo.getSecretKey(),reason);
        OTPInfo savedOtp = otpInfoRepository.save(otpInfo);
        User user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .status("pending")
                .otpInfo(savedOtp)
                .remainingBooks(1)
                .planExpiry(LocalDateTime.now().plusDays(1))
                .build();

        repository.save(user);
        return "OTP sent. Please check your Email.";
    }

    public @NotBlank(message = "Name can not be blank") String registerWithRole(RegisterRequestWithRole request) throws BadCredentialsE {
        if(repository.findByEmail(request.getEmail()).isPresent())
            throw new BadCredentialsE("Email already exists.");
        OTPInfo otpInfo = new OTPInfo();
        //otpInfo.setSecretKey(OTPService.generateRandomSecretKey());
        otpInfo.setSecretKey("654321");
        OTPInfo savedOtp = otpInfoRepository.save(otpInfo);
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf(request.getRole()))
                .status("pending")
                .otpInfo(savedOtp)
                .remainingBooks(1)
                .planExpiry(LocalDateTime.now().plusDays(1))
                .build();

        repository.save(user);
        return "New user added: \n"+user.getUsername();
    }
    public String resetPasswordRequest(ResetPasswordRequest request) throws BadCredentialsE, MessagingException, UnsupportedEncodingException {
        Optional<User> userOptional = repository.findByEmail(request.getEmail());
        if(userOptional.isEmpty())
            throw new BadCredentialsE("User with this email does not exist.");
        else {
            String reason = "verify your password reset request";
            OTPInfo otpInfo = new OTPInfo();
            otpInfo.setSecretKey(OTPService.generateRandomSecretKey());
            emailService.sendOTP(request.getEmail(),otpInfo.getSecretKey(),reason);
            User user = userOptional.get();
            OTPInfo savedOtp = otpInfoRepository.save(otpInfo);
            user.setOtpInfo(savedOtp);
            repository.save(user);
        }

        return "OTP sent. Please check your Email.";
    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = null;
        try {
            user = repository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new BadCredentialsE("User not found."));
        } catch (BadCredentialsE e) {
            throw new RuntimeException(e);
        }
        if(!user.getStatus().equals("registered")){
            throw new ResourceNotFoundException("User has not verified email with OTP yet");
        }
        else {
            var jwtToken = jwtService.generateToken(user, user.getRole().name());
            return AuthenticationResponse.builder().token(jwtToken).build();
        }
    }

    public GenericResponse verifyOtp(OTPRequest request) throws ResourceNotFoundException {
        Optional<User> userOptional = repository.findByEmail(request.getEmail());
        if (userOptional.isEmpty()){
            throw new BadRequestException("User not found");
        }
        User user = userOptional.get();

        if (user.getOtpInfo() != null) {
            OTPInfo otpInfo = user.getOtpInfo();
            String secretKey = otpInfo.getSecretKey();
            long otp_id = user.getOtpInfo().getId();
            if (secretKey.equals(request.getOtp())) {
                user.setStatus("registered");
                user.setOtpInfo(null);
                repository.save(user);
                Optional<OTPInfo> otpToDelete = otpInfoRepository.findById(otp_id);
                if(otpToDelete.isEmpty()){
                    throw new BadRequestException("OTP not found");
                }
                otpInfoRepository.delete(otpToDelete.get());
                var jwtToken = jwtService.generateToken(user, user.getRole().name());
                return GenericResponse.builder().response(jwtToken).build();
            }
        }
        else {
            throw new BadRequestException("No OTP found for user, expired");
        }
        return GenericResponse.builder().response("Wrong OTP").build();
    }

    public GenericResponse resetPassword(VerifyPasswordRequest request) throws ResourceNotFoundException {
        Optional<User> userOptional = repository.findByEmail(request.getEmail());
        if (userOptional.isEmpty()){
            throw new BadRequestException("User not found");
        }
        User user = userOptional.get();
        if (user.getOtpInfo() != null) {
            OTPInfo otpInfo = user.getOtpInfo();
            String secretKey = otpInfo.getSecretKey();
            if (secretKey.equals(request.getOtp())) {
                if (request.getPassword().equals(request.getRepeat())) {
                    user.setStatus("registered");
                    user.setOtpInfo(null);
                    user.setPassword(passwordEncoder.encode(request.getPassword()));
                    repository.save(user);
                    otpInfoRepository.delete(otpInfo);
                    var jwtToken = jwtService.generateToken(user, user.getRole().name());
                    return GenericResponse.builder().response(jwtToken).build();
                }
                else {
                    throw new BadRequestException("Passwords must match");
                }
            }
        }
        else {
            throw new BadRequestException("No OTP found for user, expired");
        }
        return GenericResponse.builder().response("Wrong OTP").build();
    }
    public String changePassword(ChangePasswordRequest request){
        Optional<User> userOptional = repository.findByEmail(request.getEmail());
        if (userOptional.isEmpty()){
            throw new BadRequestException("User email not found");
        }
        if (!request.getEmail().equals(jwtService.extractUsername(extractToken.get()))){
            throw new BadRequestException("Provided Email does not match signed in Email");
        }
        User user = userOptional.get();
        if (request.getPassword().equals(request.getRepeat())){
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            repository.save(user);
            return "Password changed";
        }
        else {
            throw new BadRequestException("Passwords do not match");
        }
    }
}
