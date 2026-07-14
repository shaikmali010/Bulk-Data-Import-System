package com.shaik.bulkimport.dto;

import com.shaik.bulkimport.enums.ImportStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportResponseDto {
	
	private Long jobId;
	
	private String fileName;
	
	private ImportStatus status;
	
	private Integer totalRecords;

	private Integer successfulRecords;

	private Integer failedRecords;

}
