package com.pcdoqtar.application.dto;

import java.time.Instant;

public record DiagnosticUploadResponse(
    Long recordId,
    String firebaseUid,
    String sessionId,
    String overallHealthStatus,
    Double cpuUsagePercent,
    Double memoryUsagePercent,
    Double storageUsagePercent,
    Boolean networkConnected,
    Integer runningProcessCount,
    Instant capturedAt,
    Instant storedAt,
    HistoricalComparisonDto historicalComparison
) {
}