package com.shaik.bulkimport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shaik.bulkimport.entity.ImportJob;

@Repository
public interface ImportJobRepository extends 
                 JpaRepository<ImportJob, Long>{
	
	boolean existsByFileHash(String fileHash);

}
