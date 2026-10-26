package com.pcdoqtar.application.entities;

import jakarta.persistence.*;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;
@Data
@Entity
@Table(name = "StorageMetric")
public class StorageMetric {
	
	
	@Id
	private Long id;
	private String metric;
	private Double value;
	private String unit;
	private String status;
	private String message;
	
	@ManyToOne
	@JoinColumn(name = "snapshot_id")
	@JsonIgnore
	private DiagnosticSnapshot snapshot;

}
