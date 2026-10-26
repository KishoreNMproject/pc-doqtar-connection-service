package com.pcdoqtar.application.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(
    name = "diagnostic_records",
    indexes = {
        @Index(name = "idx_diag_firebase_uid_created", columnList = "firebase_uid, created_at"),
        @Index(name = "idx_diag_session_id", columnList = "session_id")
    }
)
public class Diagnosticrecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "firebase_uid", nullable = false, length = 128)
    private String firebaseUid;

    @Column(name = "user_email", length = 255)
    private String userEmail;

    @Column(name = "session_id", length = 64)
    private String sessionId;

    @Column(name = "hostname", length = 128)
    private String hostname;

    @Column(name = "os_info", length = 255)
    private String osInfo;

    @Column(name = "cpu_usage_percent")
    private Double cpuUsagePercent;

    @Column(name = "memory_usage_percent")
    private Double memoryUsagePercent;

    @Column(name = "memory_used_bytes")
    private Long memoryUsedBytes;

    @Column(name = "memory_total_bytes")
    private Long memoryTotalBytes;

    @Column(name = "storage_usage_percent")
    private Double storageUsagePercent;

    @Column(name = "storage_free_bytes")
    private Long storageFreeBytes;

    @Column(name = "storage_total_bytes")
    private Long storageTotalBytes;

    @Column(name = "network_connected")
    private Boolean networkConnected;

    @Column(name = "network_latency_ms")
    private Double networkLatencyMs;

    @Column(name = "running_process_count")
    private Integer runningProcessCount;

    @Column(name = "overall_health_status", nullable = false, length = 32)
    private String overallHealthStatus;

    @Column(name = "diagnostic_summary", length = 1000)
    private String diagnosticSummary;

    @Column(name = "raw_snapshot_json", columnDefinition = "LONGTEXT")
    private String rawSnapshotJson;

    @Column(name = "captured_at", nullable = false)
    private Instant capturedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;



    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.capturedAt == null) {
            this.capturedAt = now;
        }
        if (this.overallHealthStatus == null || this.overallHealthStatus.isBlank()) {
            this.overallHealthStatus = "NORMAL";
        }
    }

}