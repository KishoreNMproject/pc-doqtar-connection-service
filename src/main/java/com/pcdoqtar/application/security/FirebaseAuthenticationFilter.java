package com.pcdoqtar.application.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Requires a valid Firebase ID token for every API route except the public
 * liveness endpoint (/) and servlet error dispatches.
 */
@Component
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    public static final String UID_ATTRIBUTE = "firebaseUid";
    public static final String EMAIL_ATTRIBUTE = "firebaseEmail";

    private final FirebaseAuth firebaseAuth;

    public FirebaseAuthenticationFilter(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        return "/".equals(path)
                || "/error".equals(path)
                || "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.regionMatches(true, 0, "Bearer ", 0, 7)) {
            unauthorized(response);
            return;
        }

        String idToken = authorization.substring(7).trim();
        if (idToken.isEmpty()) {
            unauthorized(response);
            return;
        }

        final FirebaseToken decodedToken;
        try {
            // checkRevoked=true also rejects revoked tokens and disabled users.
            decodedToken = firebaseAuth.verifyIdToken(idToken, true);
        } catch (FirebaseAuthException | IllegalArgumentException exception) {
            unauthorized(response);
            return;
        }

        request.setAttribute(UID_ATTRIBUTE, decodedToken.getUid());
        request.setAttribute(EMAIL_ATTRIBUTE, decodedToken.getEmail());
        filterChain.doFilter(request, response);
    }

    private void unauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
                "{\"type\":\"AUTHENTICATION_ERROR\","
                        + "\"message\":\"A valid Firebase ID token is required.\"}");
    }
}
