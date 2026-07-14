package com.shaik.bulkimport.entity;

import com.shaik.bulkimport.enums.ImportStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "import_record")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "job_id", nullable = false)
	private ImportJob job;
	
	@Column(columnDefinition = "TEXT")
	private String data;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ImportStatus status;
	
	@Column(columnDefinition = "TEXT")
	private String errorMessage;
}
