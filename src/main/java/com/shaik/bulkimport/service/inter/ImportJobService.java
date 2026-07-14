package com.shaik.bulkimport.service.inter;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.shaik.bulkimport.dto.ImportRecordResponseDto;
import com.shaik.bulkimport.dto.ImportResponseDto;

public interface ImportJobService {
	
	ImportResponseDto uploadFile(MultipartFile file);
	
	ImportResponseDto getJob(Long id);
	
	Page<ImportResponseDto> getAllJob(int page,
										int size, String sortBy, String direction);
	
	Page<ImportRecordResponseDto> getImportRecords(Long jobId, int page, int size, String sortBy, String direction);

}
