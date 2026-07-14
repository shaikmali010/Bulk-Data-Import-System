package com.shaik.bulkimport.service.inter;

import org.springframework.web.multipart.MultipartFile;

import com.shaik.bulkimport.entity.ImportJob;

public interface ImportProcessingService {

	void processFile(ImportJob job, MultipartFile file);
}
