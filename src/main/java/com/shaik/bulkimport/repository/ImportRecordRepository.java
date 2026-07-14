package com.shaik.bulkimport.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shaik.bulkimport.entity.ImportRecord;
import com.shaik.bulkimport.enums.ImportStatus;

@Repository
public interface ImportRecordRepository extends 
                 JpaRepository<ImportRecord, Long>{
	
	long countByJobId(Long jobId);

	long countByJobIdAndStatus(Long jobId, ImportStatus status);
	
	Page<ImportRecord> findByJobId(Long jobId, Pageable pageable);

}
