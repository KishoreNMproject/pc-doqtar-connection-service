package com.pcdoqtar.application.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pcdoqtar.application.security.FirebaseAuthenticationFilter;

/**
 * Returns identity claims from a Firebase ID token already verified by the
 * FirebaseAuthenticationFilter. It does not accept a UID supplied by clients.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/me")
    public AuthenticatedUserResponse currentUser(
            @RequestAttribute(FirebaseAuthenticationFilter.UID_ATTRIBUTE) String uid,
            @RequestAttribute(value = FirebaseAuthenticationFilter.EMAIL_ATTRIBUTE, required = false)
            String email) {
        return new AuthenticatedUserResponse(uid, email);
    }

    public record AuthenticatedUserResponse(String uid, String email) {
    }
}
