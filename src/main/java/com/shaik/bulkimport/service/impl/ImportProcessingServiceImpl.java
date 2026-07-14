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

	        List<ImportRecord> records = processRows(job, parsedFile);

	        importRecordRepository.saveAll(records);
	        
	        logger.info("{} records saved successfully.",
	                records.size());
	        
	        int totalRecords = records.size();

	        int successfulRecords = (int) records.stream()
	                .filter(record -> record.getStatus() == ImportStatus.COMPLETED)
	                .count();

	        int failedRecords = totalRecords - successfulRecords;

	        boolean hasFailedRecords = records.stream()
	                .anyMatch(record ->
	                        record.getStatus() == ImportStatus.FAILED);

	        job.setStatus(hasFailedRecords
	                ? ImportStatus.FAILED
	                : ImportStatus.COMPLETED);

	        importJobRepository.save(job);
	        
	        
	        
	        logger.info(
	                "Import Job {} completed. Total: {}, Success: {}, Failed: {}",
	                job.getId(),
	                totalRecords,
	                successfulRecords,
	                failedRecords);

	    } catch (Exception ex) {

	        job.setStatus(ImportStatus.FAILED);
	        importJobRepository.save(job);
	      
	        logger.error(
	                "Import Job {} failed while processing.",
	                job.getId(),
	                ex);
	        
	    }
	}
	
	  private List<ImportRecord> processRows(
	            ImportJob job,
	            ParsedFileDto parsedFile) {
		  
		  logger.info("Started validating records.");

	        List<ImportRecord> records = new ArrayList<>();

	        for (String row : parsedFile.getRows()) {

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

	            ImportStatus status =
	                    errors.isEmpty()
	                            ? ImportStatus.COMPLETED
	                            : ImportStatus.FAILED;

	            String errorMessage =
	                    errors.isEmpty()
	                            ? null
	                            : String.join(", ", errors);

	            ImportRecord record =
	                    ImportRecordMapper.toEntity(
	                            job,
	                            row,
	                            status,
	                            errorMessage);

	            records.add(record);
	        }

	        return records;
	    }

}
