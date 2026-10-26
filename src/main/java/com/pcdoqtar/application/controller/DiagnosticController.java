package com.pcdoqtar.application.controller;

import com.pcdoqtar.application.dto.DiagnosticUploadRequest;
import com.pcdoqtar.application.dto.DiagnosticUploadResponse;
import com.pcdoqtar.application.security.FirebaseAuthenticationFilter;
import com.pcdoqtar.application.service.DiagnosticService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/diagnostics")
public class DiagnosticController {

    private final DiagnosticService diagnosticService;

    public DiagnosticController(DiagnosticService diagnosticService) {
        this.diagnosticService = diagnosticService;
    }

    /**
     * Uploads a single diagnostic snapshot from the PC DOQTAR desktop client,
     * persists it in MySQL via JPA-Hibernate, and compares it against the most
     * recent historical record for the authenticated user.
     */
    @PostMapping("/upload")
    public ResponseEntity<DiagnosticUploadResponse> uploadDiagnostics(
            @Valid @RequestBody DiagnosticUploadRequest uploadRequest,
            HttpServletRequest httpRequest) {

        String uid = extractUid(httpRequest);
        String email = extractEmail(httpRequest);

        DiagnosticUploadResponse response =
                diagnosticService.saveAndCompare(uid, email, uploadRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Uploads a batch of diagnostic snapshots (for example, cached offline JSON snapshots).
     */
    @PostMapping("/upload-batch")
    public ResponseEntity<List<DiagnosticUploadResponse>> uploadDiagnosticsBatch(
            @Valid @RequestBody List<@Valid DiagnosticUploadRequest> batchRequests,
            HttpServletRequest httpRequest) {

        String uid = extractUid(httpRequest);
        String email = extractEmail(httpRequest);

        List<DiagnosticUploadResponse> responses = batchRequests.stream()
                .map(req -> diagnosticService.saveAndCompare(uid, email, req))
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    /**
     * Retrieves the most recent diagnostic snapshot and its historical comparison.
     */
    @GetMapping("/latest")
    public ResponseEntity<DiagnosticUploadResponse> getLatestDiagnostics(
            HttpServletRequest httpRequest) {

        String uid = extractUid(httpRequest);
        return diagnosticService.getLatestRecord(uid)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    /**
     * Retrieves historical diagnostic records for the authenticated user.
     */
    @GetMapping("/history")
    public ResponseEntity<List<DiagnosticUploadResponse>> getDiagnosticHistory(
            @RequestParam(name = "limit", defaultValue = "20") int limit,
            HttpServletRequest httpRequest) {

        String uid = extractUid(httpRequest);
        List<DiagnosticUploadResponse> history = diagnosticService.getRecentHistory(uid, limit);
        return ResponseEntity.ok(history);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "VALIDATION_FAILED");
        body.put("message", "Invalid diagnostic payload fields");
        body.put("fieldErrors", fieldErrors);

        return ResponseEntity.badRequest().body(body);
    }

    private String extractUid(HttpServletRequest request) {
        Object uidAttr = request.getAttribute(FirebaseAuthenticationFilter.UID_ATTRIBUTE);
        if (uidAttr instanceof String uid && !uid.isBlank()) {
            return uid;
        }
        Object fallback = request.getAttribute("firebaseUid");
        if (fallback instanceof String uid && !uid.isBlank()) {
            return uid;
        }
        throw new IllegalStateException("Authenticated Firebase UID attribute is missing from request");
    }

    private String extractEmail(HttpServletRequest request) {
        Object emailAttr = request.getAttribute(FirebaseAuthenticationFilter.EMAIL_ATTRIBUTE);
        if (emailAttr instanceof String email && !email.isBlank()) {
            return email;
        }
        Object fallback = request.getAttribute("firebaseEmail");
        if (fallback instanceof String email && !email.isBlank()) {
            return email;
        }
        return null;
    }
}