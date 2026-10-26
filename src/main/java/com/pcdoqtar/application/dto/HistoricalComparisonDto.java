package com.pcdoqtar.application.dto;

import java.time.Instant;

public record HistoricalComparisonDto(
    boolean hasPreviousRecord,
    Long previousRecordId,
    Instant previousCapturedAt,
    Double cpuUsageDelta,
    Double memoryUsageDelta,
    Double storageUsageDelta,
    String previousHealthStatus,
    String currentHealthStatus,
    String trendAssessment
) {
    public static HistoricalComparisonDto initial(String currentHealthStatus) {
        return new HistoricalComparisonDto(
            false,
            null,
            null,
            0.0,
            0.0,
            0.0,
            null,
            currentHealthStatus,
            "Baseline record saved. Subsequent diagnostic uploads will compare against this snapshot."
        );
    }
}