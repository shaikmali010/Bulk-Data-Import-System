package com.shaik.bulkimport.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.shaik.bulkimport.dto.ImportRecordResponseDto;
import com.shaik.bulkimport.dto.ImportResponseDto;
import com.shaik.bulkimport.dto.ParsedFileDto;
import com.shaik.bulkimport.entity.ImportJob;
import com.shaik.bulkimport.entity.ImportRecord;
import com.shaik.bulkimport.enums.ImportStatus;
import com.shaik.bulkimport.exception.DuplicateFileException;
import com.shaik.bulkimport.exception.ImportJobNotFoundException;
import com.shaik.bulkimport.factory.FileParserFactory;
import com.shaik.bulkimport.mapper.ImportJobMapper;
import com.shaik.bulkimport.mapper.ImportRecordMapper;
import com.shaik.bulkimport.parser.FileParser;
import com.shaik.bulkimport.repository.ImportJobRepository;
import com.shaik.bulkimport.repository.ImportRecordRepository;
import com.shaik.bulkimport.service.inter.ImportJobService;
import com.shaik.bulkimport.util.FileHashUtil;
import com.shaik.bulkimport.validator.FileValidator;
import com.shaik.bulkimport.validator.RecordValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImportJobServiceImpl implements ImportJobService{
	
	private final ImportJobRepository importJobRepository;
	
	private final FileParserFactory fileParserFactory;;
	
	private final ImportRecordRepository importRecordRepository;

	private final RecordValidator recordValidator;
	
	private static final Logger logger = LoggerFactory
			.getLogger(ImportJobServiceImpl.class);
	
	private ImportJob createAndSaveImportJob(MultipartFile file) {

	    String fileHash = FileHashUtil.generateHash(file);

	    if (importJobRepository.existsByFileHash(fileHash)) {
	        throw new DuplicateFileException(
	                "This file has already been imported.");
	    }

	    ImportJob job = ImportJob.builder()
	            .fileName(file.getOriginalFilename())
	            .fileHash(fileHash)
	            .status(ImportStatus.PENDING)
	            .createdAt(LocalDateTime.now())
	            .build();

	    return importJobRepository.save(job);
	}
	
	@Override
	@Transactional
	public ImportResponseDto uploadFile(MultipartFile file) {
		
		logger.info("Upload request received for file: {}",
		        file.getOriginalFilename());
		
		FileValidator.validateFile(file);
		
		ImportJob job = createAndSaveImportJob(file);
		
		logger.info("Import Job {} created successfully.",
	            job.getId());

		
		 int totalRecords = 0;
	     int successfulRecords = 0;
	     int failedRecords = 0;
		
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
			
			totalRecords = records.size();

            successfulRecords = (int) records.stream()
                    .filter(record -> record.getStatus() == ImportStatus.COMPLETED)
                    .count();

            failedRecords = totalRecords - successfulRecords;

            updateJobStatus(job, failedRecords);

            logger.info(
                    "Import Job {} completed. Total: {}, Success: {}, Failed: {}",
                    job.getId(),
                    totalRecords,
                    successfulRecords,
                    failedRecords);
			
			
		} catch (Exception ex) {
			
			job.setStatus(ImportStatus.FAILED);
			importJobRepository.save(job);
			
			  logger.error("Import Job {} failed.",
		                job.getId(),
		                ex);
			
			throw new RuntimeException("Error while importing file.", ex);
		}
		
		return ImportJobMapper.mapToResponse(
				job, totalRecords, 
				successfulRecords, failedRecords);
	}
	
	  private List<ImportRecord> processRows(
	            ImportJob job,
	            ParsedFileDto parsedFile) {

		  logger.info("Started processing Job ID: {}",
			        job.getId());
		  
	        List<ImportRecord> records = new ArrayList<>();

	        logger.info("Started validating records.");
	        
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
	  
	  private void updateJobStatus(
	            ImportJob job,
	            int failedRecords) {

	        if (failedRecords > 0) {
	            job.setStatus(ImportStatus.FAILED);
	        } else {
	            job.setStatus(ImportStatus.COMPLETED);
	        }

	        importJobRepository.save(job);
	        
	        logger.info(
	                "Job {} status updated to {}",
	                job.getId(),
	                job.getStatus());
	        
	    }
	  
	@Override
	@Transactional(readOnly = true)
	public ImportResponseDto getJob(Long id) {
		
		logger.info("Fetching Import Job with ID: {}", id);
		
		ImportJob job = importJobRepository.findById(id)
				.orElseThrow(() -> new ImportJobNotFoundException(
						"Import Job with ID "+id+" not found"));
		
		int totalRecords = (int) importRecordRepository.countByJobId(id);
		
		int successfulRecords = (int) importRecordRepository
				.countByJobIdAndStatus(id, ImportStatus.COMPLETED);
		
		int failedRecords = (int) importRecordRepository
				.countByJobIdAndStatus(id, ImportStatus.FAILED);
		
		return ImportJobMapper.mapToResponse(job, totalRecords, 
				successfulRecords, failedRecords);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Page<ImportResponseDto> getAllJob(int page, int size, String sortBy, String direction){
		
		Sort sort = direction.equalsIgnoreCase("desc")
					? Sort.by(sortBy).descending()
					: Sort.by(sortBy).ascending();
		
		Pageable pageable = PageRequest.of(page, size, sort);
		
		Page<ImportJob> jobs = importJobRepository.findAll(pageable);
		
		return jobs
				.map(job -> {
					
					int totalRecords = (int) importRecordRepository
							.countByJobId(job.getId());
					
					 int successfulRecords = (int) importRecordRepository
		                        .countByJobIdAndStatus(
		                                job.getId(),
		                                ImportStatus.COMPLETED);

		             int failedRecords = (int) importRecordRepository
		                        .countByJobIdAndStatus(
		                                job.getId(),
		                                ImportStatus.FAILED);
		             
		             logger.info(
		            	        "Fetching import jobs. Page: {}, Size: {}, SortBy: {}, Direction: {}",
		            	        page,
		            	        size,
		            	        sortBy,
		            	        direction);
		             
		             return ImportJobMapper.mapToResponse(
		                        job,
		                        totalRecords,
		                        successfulRecords,
		                        failedRecords);
				});
				
	
}
	
	@Override
	@Transactional(readOnly = true)
	public List<ImportRecordResponseDto> getImportRecords(Long jobId) {
		
		logger.info(
		        "Fetching import records for Job ID: {}",
		        jobId);

	    List<ImportRecord> records =
	            importRecordRepository.findByJobId(jobId);

	    return ImportRecordMapper.mapToResponseList(records);
	}
	
	
	
}
