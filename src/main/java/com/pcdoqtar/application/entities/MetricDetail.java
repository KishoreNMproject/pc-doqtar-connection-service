package com.pcdoqtar.application.entities;

import jakarta.persistence.Embeddable;

@Embeddable
public class MetricDetail {
    private String metric;
    private Double value;
    private String unit;
    private String status;
    private String message;

    // Getters and Setters
    public String getMetric() { return metric; }
    public void setMetric(String metric) { this.metric = metric; }
    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
