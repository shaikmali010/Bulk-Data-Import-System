package com.shaik.bulkimport.mapper;

import java.util.List;

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
	
	public static List<ImportRecordResponseDto> mapToResponseList(
	        List<ImportRecord> records) {

	    return records.stream()
	            .map(ImportRecordMapper::mapToResponse)
	            .toList();
	}
}
