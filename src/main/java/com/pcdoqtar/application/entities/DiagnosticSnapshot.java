package com.pcdoqtar.application.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
@Data
@Entity
@Table(name = "diagnostic_snapshots")
public class DiagnosticSnapshot {

    @Id
    private String snapshotId; // Using UUID String as Primary Key

    private Integer schemaVersion;
    private String application;
    private String recordType;
    private String timestamp; // Can also use LocalDateTime
    private String overallStatus;
    private Integer warningCount;
    private Boolean containsUnavailableResults;

    // Embeds CPU columns (prefixed in DB as cpu_metric, cpu_value, etc.)
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "metric", column = @Column(name = "cpu_metric")),
        @AttributeOverride(name = "value", column = @Column(name = "cpu_value")),
        @AttributeOverride(name = "unit", column = @Column(name = "cpu_unit")),
        @AttributeOverride(name = "status", column = @Column(name = "cpu_status")),
        @AttributeOverride(name = "message", column = @Column(name = "cpu_message"))
    })
    private MetricDetail cpu;

    // Embeds Memory columns (prefixed in DB as memory_metric, memory_value, etc.)
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "metric", column = @Column(name = "memory_metric")),
        @AttributeOverride(name = "value", column = @Column(name = "memory_value")),
        @AttributeOverride(name = "unit", column = @Column(name = "memory_unit")),
        @AttributeOverride(name = "status", column = @Column(name = "memory_status")),
        @AttributeOverride(name = "message", column = @Column(name = "memory_message"))
    })
    private MetricDetail memory;

    // One-to-Many relationship for storage drives array
    @OneToMany(mappedBy = "snapshot", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<StorageMetric> storage;

    
}
