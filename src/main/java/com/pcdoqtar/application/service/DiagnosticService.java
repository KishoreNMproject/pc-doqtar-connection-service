package com.pcdoqtar.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcdoqtar.application.dto.DiagnosticUploadRequest;
import com.pcdoqtar.application.dto.DiagnosticUploadResponse;
import com.pcdoqtar.application.dto.HistoricalComparisonDto;
import com.pcdoqtar.application.entities.Diagnosticrecord;
import com.pcdoqtar.application.repository.DiagnosticrecordRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DiagnosticService {

    private final DiagnosticrecordRepository repository;
    private final ObjectMapper objectMapper;

    public DiagnosticService(DiagnosticrecordRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public DiagnosticUploadResponse saveAndCompare(
            String firebaseUid,
            String userEmail,
            DiagnosticUploadRequest request) {

        Optional<Diagnosticrecord> previousOpt =
                repository.findFirstByFirebaseUidOrderByCapturedAtDesc(firebaseUid);

        Diagnosticrecord record = mapToentities(firebaseUid, userEmail, request);
        @SuppressWarnings("null")
		Diagnosticrecord saved = repository.save(record);

        HistoricalComparisonDto comparison = previousOpt
                .map(prev -> buildComparison(prev, saved))
                .orElseGet(() -> HistoricalComparisonDto.initial(saved.getOverallHealthStatus()));

        return toResponse(saved, comparison);
    }

    @Transactional(readOnly = true)
    public List<DiagnosticUploadResponse> getRecentHistory(String firebaseUid, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 100));
        List<Diagnosticrecord> records = repository.findByFirebaseUidOrderByCapturedAtDesc(
                firebaseUid, PageRequest.of(0, safeLimit));

        return records.stream()
                .map(r -> toResponse(r, null))
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<DiagnosticUploadResponse> getLatestRecord(String firebaseUid) {
        List<Diagnosticrecord> latestTwo = repository.findByFirebaseUidOrderByCapturedAtDesc(
                firebaseUid, PageRequest.of(0, 2));

        if (latestTwo.isEmpty()) {
            return Optional.empty();
        }

        Diagnosticrecord current = latestTwo.get(0);
        HistoricalComparisonDto comparison = latestTwo.size() > 1
                ? buildComparison(latestTwo.get(1), current)
                : HistoricalComparisonDto.initial(current.getOverallHealthStatus());

        return Optional.of(toResponse(current, comparison));
    }

    private Diagnosticrecord mapToentities(
            String firebaseUid,
            String userEmail,
            DiagnosticUploadRequest request) {

        Diagnosticrecord record = new Diagnosticrecord();
        record.setFirebaseUid(firebaseUid);
        record.setUserEmail(userEmail);
        record.setSessionId(request.getSessionId());
        record.setHostname(request.getHostname());
        record.setOsInfo(request.getOsInfo());

        // Populate direct fields or fallback to nested SystemHealthSnapshot maps if present
        Map<String, Object> extra = request.getAdditionalPayload();

        Double cpu = request.getCpuUsagePercent() != null
                ? request.getCpuUsagePercent()
                : extractNestedDouble(extra, "cpu", "usagePercent", "cpuLoad", "systemCpuLoad");
        Double mem = request.getMemoryUsagePercent() != null
                ? request.getMemoryUsagePercent()
                : extractNestedDouble(extra, "memory", "usagePercent", "memoryUsagePercent");
        Double storage = request.getStorageUsagePercent() != null
                ? request.getStorageUsagePercent()
                : extractNestedDouble(extra, "storage", "usagePercent", "diskUsagePercent");

        record.setCpuUsagePercent(cpu);
        record.setMemoryUsagePercent(mem);
        record.setMemoryUsedBytes(request.getMemoryUsedBytes());
        record.setMemoryTotalBytes(request.getMemoryTotalBytes());
        record.setStorageUsagePercent(storage);
        record.setStorageFreeBytes(request.getStorageFreeBytes());
        record.setStorageTotalBytes(request.getStorageTotalBytes());
        record.setNetworkConnected(request.getNetworkConnected());
        record.setNetworkLatencyMs(request.getNetworkLatencyMs());
        record.setRunningProcessCount(request.getRunningProcessCount());

        String status = request.getOverallHealthStatus();
        if (status == null || status.isBlank()) {
            status = evaluateHealthStatus(cpu, mem, storage);
        }
        record.setOverallHealthStatus(status);
        record.setDiagnosticSummary(request.getDiagnosticSummary());
        record.setCapturedAt(request.getCapturedAt() != null ? request.getCapturedAt() : Instant.now());

        if (request.getRawSnapshotJson() != null && !request.getRawSnapshotJson().isBlank()) {
            record.setRawSnapshotJson(request.getRawSnapshotJson());
        } else {
            try {
                record.setRawSnapshotJson(objectMapper.writeValueAsString(request));
            } catch (JsonProcessingException e) {
                record.setRawSnapshotJson("{}");
            }
        }

        return record;
    }

    private String evaluateHealthStatus(Double cpu, Double mem, Double storage) {
        double maxMetric = 0.0;
        if (cpu != null) maxMetric = Math.max(maxMetric, cpu);
        if (mem != null) maxMetric = Math.max(maxMetric, mem);
        if (storage != null) maxMetric = Math.max(maxMetric, storage);

        if (maxMetric >= 90.0) {
            return "CRITICAL";
        } else if (maxMetric >= 80.0) {
            return "WARNING";
        }
        return "NORMAL";
    }

    private Double extractNestedDouble(Map<String, Object> extra, String sectionKey, String... candidateKeys) {
        if (extra == null || extra.isEmpty()) {
            return null;
        }
        for (String key : candidateKeys) {
            Object topVal = extra.get(key);
            if (topVal instanceof Number num) {
                return num.doubleValue();
            }
        }
        Object section = extra.get(sectionKey);
        if (section instanceof Map<?, ?> sectionMap) {
            for (String key : candidateKeys) {
                Object nestedVal = sectionMap.get(key);
                if (nestedVal instanceof Number num) {
                    return num.doubleValue();
                }
            }
        }
        return null;
    }

    private HistoricalComparisonDto buildComparison(Diagnosticrecord previous, Diagnosticrecord current) {
        double cpuDelta = computeDelta(previous.getCpuUsagePercent(), current.getCpuUsagePercent());
        double memDelta = computeDelta(previous.getMemoryUsagePercent(), current.getMemoryUsagePercent());
        double storageDelta = computeDelta(previous.getStorageUsagePercent(), current.getStorageUsagePercent());

        StringBuilder trend = new StringBuilder();
        if (Math.abs(cpuDelta) >= 10.0) {
            trend.append(String.format("CPU usage %s by %.1f%%. ",
                    cpuDelta > 0 ? "increased" : "decreased", Math.abs(cpuDelta)));
        }
        if (Math.abs(memDelta) >= 10.0) {
            trend.append(String.format("Memory usage %s by %.1f%%. ",
                    memDelta > 0 ? "increased" : "decreased", Math.abs(memDelta)));
        }
        if (Math.abs(storageDelta) >= 5.0) {
            trend.append(String.format("Storage usage %s by %.1f%%. ",
                    storageDelta > 0 ? "increased" : "decreased", Math.abs(storageDelta)));
        }
        if (trend.length() == 0) {
            trend.append("System resource usage remains stable compared to previous diagnostic snapshot.");
        }

        return new HistoricalComparisonDto(
                true,
                previous.getId(),
                previous.getCapturedAt(),
                roundTwoDecimals(cpuDelta),
                roundTwoDecimals(memDelta),
                roundTwoDecimals(storageDelta),
                previous.getOverallHealthStatus(),
                current.getOverallHealthStatus(),
                trend.toString().trim()
        );
    }

    private double computeDelta(Double previousVal, Double currentVal) {
        if (previousVal == null || currentVal == null) {
            return 0.0;
        }
        return currentVal - previousVal;
    }

    private double roundTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private DiagnosticUploadResponse toResponse(
            Diagnosticrecord record,
            HistoricalComparisonDto comparison) {

        return new DiagnosticUploadResponse(
                record.getId(),
                record.getFirebaseUid(),
                record.getSessionId(),
                record.getOverallHealthStatus(),
                record.getCpuUsagePercent(),
                record.getMemoryUsagePercent(),
                record.getStorageUsagePercent(),
                record.getNetworkConnected(),
                record.getRunningProcessCount(),
                record.getCapturedAt(),
                record.getCreatedAt(),
                comparison
        );
    }
}