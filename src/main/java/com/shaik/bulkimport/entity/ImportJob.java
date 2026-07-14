package com.shaik.bulkimport.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.shaik.bulkimport.enums.ImportStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "import_job")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportJob {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String fileName;
	
	@Column(nullable = false, unique = true)
	private String fileHash;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ImportStatus status;
	
	@Column(nullable = false)
	private LocalDateTime createdAt;

	@OneToMany(mappedBy = "job",
			cascade = CascadeType.ALL)
	private List<ImportRecord> records;
}
