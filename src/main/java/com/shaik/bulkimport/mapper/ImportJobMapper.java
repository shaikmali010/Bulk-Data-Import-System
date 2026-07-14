package com.shaik.bulkimport.mapper;

import java.util.List;

import com.shaik.bulkimport.dto.ImportResponseDto;
import com.shaik.bulkimport.entity.ImportJob;

public class ImportJobMapper {
	
	private ImportJobMapper() {}

	public static ImportResponseDto mapToResponse(ImportJob importJob,
						int totalRecords,
						int successfulRecords,
						int failedRecords) {
		
		return ImportResponseDto.builder()
				.jobId(importJob.getId())
				.fileName(importJob.getFileName())
				.status(importJob.getStatus())
				.totalRecords(totalRecords)
				.successfulRecords(successfulRecords)
				.failedRecords(failedRecords)
				.build();
	}
	
	public static ImportResponseDto mapToResponse(ImportJob job) {

	    return ImportResponseDto.builder()
	            .jobId(job.getId())
	            .fileName(job.getFileName())
	            .status(job.getStatus())
	            .build();
	}
	
	public static List<ImportResponseDto> mapToResponseList(List<ImportJob> jobs){
		return jobs.stream()
				.map(ImportJobMapper::mapToResponse)
				.toList();
	}
}
