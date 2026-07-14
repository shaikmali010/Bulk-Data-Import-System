package com.shaik.bulkimport.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.shaik.bulkimport.dto.ParsedFileDto;
import com.shaik.bulkimport.entity.ImportJob;
import com.shaik.bulkimport.entity.ImportRecord;
import com.shaik.bulkimport.enums.ImportStatus;
import com.shaik.bulkimport.factory.FileParserFactory;
import com.shaik.bulkimport.mapper.ImportRecordMapper;
import com.shaik.bulkimport.parser.FileParser;
import com.shaik.bulkimport.repository.ImportJobRepository;
import com.shaik.bulkimport.repository.ImportRecordRepository;
import com.shaik.bulkimport.service.inter.ImportProcessingService;
import com.shaik.bulkimport.validator.RecordValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImportProcessingServiceImpl implements ImportProcessingService{
	
	private final FileParserFactory fileParserFactory;

	private final RecordValidator recordValidator;

	private final ImportRecordRepository importRecordRepository;

	private final ImportJobRepository importJobRepository;
	
	private static final Logger logger = LoggerFactory
			.getLogger(ImportProcessingServiceImpl.class);
	
	@Async
	@Override
	@Transactional
	public void processFile(
	        ImportJob job,
	        MultipartFile file) {
		
		logger.info("Started processing Import Job ID: {}",
		        job.getId());

	    try {

	        job.setStatus(ImportStatus.PROCESSING);
	        importJobRepository.save(job);

	        FileParser parser =
	                fileParserFactory.getParser(file);
	        
	        logger.info("Using parser: {}",
	                parser.getClass().getSimpleName());

	        ParsedFileDto parsedFile =
	                parser.parse(file);
	        
	        logger.info("Parsed {} records from file.",
	                parsedFile.getRows().size());

	        processRows(job, parsedFile);

	    } catch (Exception ex) {

	        job.setStatus(ImportStatus.FAILED);
	        importJobRepository.save(job);
	      
	        logger.error(
	                "Import Job {} failed while processing.",
	                job.getId(),
	                ex);
	        
	    }
	}
	
	private void processRows(
	        ImportJob job,
	        ParsedFileDto parsedFile) {

	    logger.info("Started validating records.");

	    List<ImportRecord> batch = new ArrayList<>();

	    int totalRecords = 0;
	    int successfulRecords = 0;
	    int failedRecords = 0;

	    final int BATCH_SIZE = 100;

	    for (String row : parsedFile.getRows()) {

	        totalRecords++;

	        List<String> errors =
	                recordValidator.validate(
	                        parsedFile.getHeaders(),
	                        row);

	        if (!errors.isEmpty()) {

	            logger.warn(
	                    "Invalid record: {} | Errors: {}",
	                    row,
	                    errors);
	        }

	        ImportStatus status;

	        String errorMessage;

	        if (errors.isEmpty()) {

	            status = ImportStatus.COMPLETED;
	            errorMessage = null;
	            successfulRecords++;

	        } else {

	            status = ImportStatus.FAILED;
	            errorMessage = String.join(", ", errors);
	            failedRecords++;

	        }

	        ImportRecord record =
	                ImportRecordMapper.toEntity(
	                        job,
	                        row,
	                        status,
	                        errorMessage);

	        batch.add(record);

	        // Save every 100 records
	        if (batch.size() == BATCH_SIZE) {

	            importRecordRepository.saveAll(batch);

	            logger.info("{} records saved.", batch.size());

	            batch.clear();
	        }
	    }

	    // Save remaining records
	    if (!batch.isEmpty()) {

	        importRecordRepository.saveAll(batch);

	        logger.info("{} records saved.", batch.size());

	        batch.clear();
	    }

	    // Update Job Status
	    if (failedRecords > 0) {
	        job.setStatus(ImportStatus.FAILED);
	    } else {
	        job.setStatus(ImportStatus.COMPLETED);
	    }

	    importJobRepository.save(job);

	    logger.info(
	            "Import Job {} completed. Total: {}, Success: {}, Failed: {}",
	            job.getId(),
	            totalRecords,
	            successfulRecords,
	            failedRecords);
	}

}
