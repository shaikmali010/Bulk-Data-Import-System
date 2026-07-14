package com.shaik.bulkimport.validator;

import org.springframework.web.multipart.MultipartFile;

public class FileValidator {

	private FileValidator() {
//		prevent object creation
	}
	
	public static void validateFile(MultipartFile file) {
		
		if(file == null || file.isEmpty()) {
			throw new IllegalArgumentException("Uploaded file is empty.");
		}
		
		String fileName = file.getOriginalFilename();
		
		if(fileName == null) {
			throw new IllegalArgumentException("File name is missing.");
		}
		
		String lowerCaseFileName = fileName.toLowerCase();
		
		if(!(lowerCaseFileName.endsWith(".csv") 
				|| lowerCaseFileName.endsWith(".xlsx"))) {
			
			throw new IllegalArgumentException(
					"Only Csv and Excel (.xlsx) files are supported.");
		}
		
	}
}
