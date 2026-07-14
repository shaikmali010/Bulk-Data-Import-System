package com.shaik.bulkimport.parser.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.shaik.bulkimport.dto.ParsedFileDto;
import com.shaik.bulkimport.parser.FileParser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CsvFileParserImpl implements FileParser{
		
		@Override
		public ParsedFileDto parse(
				MultipartFile file) 
						throws IOException{
			
			List<String> headers = new ArrayList<>();
			List<String> rows = new ArrayList<>();
			
			try(
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(
									file.getInputStream(), 
									StandardCharsets.UTF_8));
					
					CSVParser parser = CSVFormat.DEFAULT
							.builder()
							.setHeader()
							.setSkipHeaderRecord(true)
							.build()
							.parse(reader)){
				headers.addAll(parser.getHeaderNames());
				
				for(CSVRecord record : parser) {
					
					rows.add(String.join(",",record));
				}
			}
			
			return ParsedFileDto.builder()
					.headers(headers)
					.rows(rows)
					.build();
		}
	
}
