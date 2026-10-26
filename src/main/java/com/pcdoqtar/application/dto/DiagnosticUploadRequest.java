package com.pcdoqtar.application.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiagnosticUploadRequest {

    @Size(max = 64, message = "sessionId must not exceed 64 characters")
    private String sessionId;

    @Size(max = 128, message = "hostname must not exceed 128 characters")
    private String hostname;

    @Size(max = 255, message = "osInfo must not exceed 255 characters")
    private String osInfo;

    @DecimalMin(value = "0.0", message = "cpuUsagePercent must be >= 0")
    @DecimalMax(value = "100.0", message = "cpuUsagePercent must be <= 100")
    private Double cpuUsagePercent;

    @DecimalMin(value = "0.0", message = "memoryUsagePercent must be >= 0")
    @DecimalMax(value = "100.0", message = "memoryUsagePercent must be <= 100")
    private Double memoryUsagePercent;

    @PositiveOrZero(message = "memoryUsedBytes must be >= 0")
    private Long memoryUsedBytes;

    @PositiveOrZero(message = "memoryTotalBytes must be >= 0")
    private Long memoryTotalBytes;

    @DecimalMin(value = "0.0", message = "storageUsagePercent must be >= 0")
    @DecimalMax(value = "100.0", message = "storageUsagePercent must be <= 100")
    private Double storageUsagePercent;

    @PositiveOrZero(message = "storageFreeBytes must be >= 0")
    private Long storageFreeBytes;

    @PositiveOrZero(message = "storageTotalBytes must be >= 0")
    private Long storageTotalBytes;

    private Boolean networkConnected;

    @PositiveOrZero(message = "networkLatencyMs must be >= 0")
    private Double networkLatencyMs;

    @PositiveOrZero(message = "runningProcessCount must be >= 0")
    private Integer runningProcessCount;

    @Size(max = 32, message = "overallHealthStatus must not exceed 32 characters")
    private String overallHealthStatus;

    @Size(max = 1000, message = "diagnosticSummary must not exceed 1000 characters")
    private String diagnosticSummary;

    private Instant capturedAt;

    private String rawSnapshotJson;

    private final Map<String, Object> additionalPayload = new LinkedHashMap<>();

    public DiagnosticUploadRequest() {
    }

    @JsonAnySetter
    public void putAdditionalProperty(String key, Object value) {
        this.additionalPayload.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalPayload() {
        return additionalPayload;
    }
}