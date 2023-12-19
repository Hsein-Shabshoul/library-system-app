package com.project.library.security.auth;

import com.project.library.exception.BadCredentialsE;
import com.project.library.security.otp.OTPRequest;
import com.project.library.security.otp.GenericResponse;
import com.project.library.security.user.ResetPasswordRequest;
import com.project.library.security.user.VerifyPasswordRequest;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) throws BadCredentialsE, MessagingException, UnsupportedEncodingException {
        return ResponseEntity.ok(service.register(request));
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(service.authenticate(request));
    }
    @PostMapping("/forgot")
    public ResponseEntity<String> authenticate(@Valid @RequestBody ResetPasswordRequest request) throws BadCredentialsE, MessagingException, UnsupportedEncodingException {
        return ResponseEntity.ok(service.resetPasswordRequest(request));
    }
    @PostMapping("/verify-reset")
    public ResponseEntity<GenericResponse> authenticate(@Valid @RequestBody VerifyPasswordRequest request) {
        return ResponseEntity.ok(service.resetPassword(request));
    }
    @PostMapping("/verify")
    public ResponseEntity<GenericResponse> verify(@Valid @RequestBody OTPRequest request){
        return ResponseEntity.ok(service.verifyOtp(request));
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/register_role")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestWithRole request) throws BadCredentialsE {
        return ResponseEntity.ok(service.registerWithRole(request));
    }

}
