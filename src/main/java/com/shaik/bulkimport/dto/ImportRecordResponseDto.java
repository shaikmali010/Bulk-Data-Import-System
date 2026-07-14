package com.shaik.bulkimport.dto;

import com.shaik.bulkimport.enums.ImportStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportRecordResponseDto {
	
	private Long id;
	
	private String data;
	
	private ImportStatus status;
	
	private String errorMessage;

}
