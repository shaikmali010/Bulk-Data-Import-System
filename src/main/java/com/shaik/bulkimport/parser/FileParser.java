package com.shaik.bulkimport.parser;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.shaik.bulkimport.dto.ParsedFileDto;

public interface FileParser {
	
	ParsedFileDto parse(MultipartFile file) throws IOException;

}
