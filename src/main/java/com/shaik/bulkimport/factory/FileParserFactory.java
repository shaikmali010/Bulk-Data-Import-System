package com.shaik.bulkimport.factory;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.shaik.bulkimport.exception.InvalidFileException;
import com.shaik.bulkimport.parser.FileParser;
import com.shaik.bulkimport.parser.impl.CsvFileParserImpl;
import com.shaik.bulkimport.parser.impl.ExcelFileParserImpl;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FileParserFactory {

	private final CsvFileParserImpl csvFileParser;
	
	private final ExcelFileParserImpl excelFileParser;
	
	public FileParser getParser(MultipartFile file) {
		
		String fileName = file.getOriginalFilename();
		
		if(fileName == null) {
			throw new InvalidFileException("File name canot be null.");
		}
		
		fileName = fileName.toLowerCase();
		
		if(fileName.endsWith(".csv")) {
			return csvFileParser;
		}
		
		if(fileName.endsWith(".xlsx")) {
			return excelFileParser;
		}
		
		throw new InvalidFileException("Oly CSV and Excel files are supported.");
	}
}
