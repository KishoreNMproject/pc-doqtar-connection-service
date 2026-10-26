package com.pcdoqtar.application.entities;

import jakarta.persistence.Embeddable;
import lombok.Data;
@Data
@Embeddable
public class MetricDetail {
    private String metric;
    private Double value;
    private String unit;
    private String status;
    private String message;

}
