package com.shaik.bulkimport.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.shaik.bulkimport.dto.ImportRecordResponseDto;
import com.shaik.bulkimport.dto.ImportResponseDto;
import com.shaik.bulkimport.service.inter.ImportJobService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
@Tag(name = "Bulk Import APIs",
description = "APIs for uploading and managing import jobs")
public class ImportJobController {

	private final ImportJobService importJobService;
	
	@Operation(summary = "Upload CSV or Excel File")
	@PostMapping("/upload")
	public ResponseEntity<ImportResponseDto> uploadFile(
			@RequestParam("file") MultipartFile file){
		return ResponseEntity.ok(importJobService.uploadFile(file));
	}
	
	@Operation(summary = "Get Import Job by ID")
	@GetMapping("/{id}")
	public ResponseEntity<ImportResponseDto> getJob (
			@PathVariable Long id)
	{
		return ResponseEntity.ok(importJobService.getJob(id));
	}
	
	@Operation(summary = "Get All Import Jobs")
	@GetMapping
	public ResponseEntity<Page<ImportResponseDto>> getAllJobs(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "createdAt") String sortBy,
			@RequestParam(defaultValue = "desc") String direction)	{
		return ResponseEntity.ok(importJobService.getAllJob(page, size, sortBy, direction));
	}
	
	@Operation(summary = "Get Import Records")
	@GetMapping("/{jobId}/records")
	public ResponseEntity<List<ImportRecordResponseDto>> getImportRecords(
	        @PathVariable Long jobId) {

	    return ResponseEntity.ok(
	            importJobService.getImportRecords(jobId));
	}
}
