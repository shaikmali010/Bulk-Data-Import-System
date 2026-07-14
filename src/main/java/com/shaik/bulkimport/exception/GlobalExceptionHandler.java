package com.shaik.bulkimport.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, 
								HttpServletRequest request) {
		
		ErrorResponse response = ErrorResponse.builder()
				.timestamp(LocalDateTime.now())
				.status(status.value())
				.error(status.getReasonPhrase())
				.message(message)
				.path(request.getRequestURI())
				.build();
				
		return ResponseEntity.status(status).body(response);
	}
	
	
	@ExceptionHandler(ImportJobNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleImportJobNotFound(
			ImportJobNotFoundException ex, HttpServletRequest request){
		
		return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
				
}
	
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
			IllegalArgumentException ex, HttpServletRequest request){
		
		return buildResponse(
				HttpStatus.BAD_REQUEST, ex.getMessage(), request);
	}
	
	@ExceptionHandler(InvalidFileException.class)
	public ResponseEntity<ErrorResponse> handleInvalidFileException(
			InvalidFileException ex, HttpServletRequest request){
		return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);			
	}
	
	@ExceptionHandler(DuplicateFileException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateFileException(
			DuplicateFileException ex, HttpServletRequest request){
		return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
	}

}