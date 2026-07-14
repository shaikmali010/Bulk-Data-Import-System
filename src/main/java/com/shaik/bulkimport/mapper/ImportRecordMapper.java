package com.shaik.bulkimport.mapper;

import org.springframework.data.domain.Page;

import com.shaik.bulkimport.dto.ImportRecordResponseDto;
import com.shaik.bulkimport.entity.ImportJob;
import com.shaik.bulkimport.entity.ImportRecord;
import com.shaik.bulkimport.enums.ImportStatus;

public class ImportRecordMapper {

	private ImportRecordMapper() {}
	
	public static ImportRecord toEntity(ImportJob job, String rawData,
					ImportStatus status,
					String errorMessage) {
		
		return ImportRecord.builder()
				.job(job)
				.data(rawData)
				.status(status)
				.errorMessage(errorMessage)
				.build();
	}
	
	public static ImportRecordResponseDto mapToResponse(
	        ImportRecord record) {

	    return ImportRecordResponseDto.builder()
	            .id(record.getId())
	            .data(record.getData())
	            .status(record.getStatus())
	            .errorMessage(record.getErrorMessage())
	            .build();
	}
	
	public static Page<ImportRecordResponseDto> mapToResponseList(
	        Page<ImportRecord> records) {

	    return records
	            .map(ImportRecordMapper::mapToResponse);
	}
}
