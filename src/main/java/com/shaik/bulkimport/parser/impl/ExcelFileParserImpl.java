package com.shaik.bulkimport.parser.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.shaik.bulkimport.dto.ParsedFileDto;
import com.shaik.bulkimport.parser.FileParser;

@Service
public class ExcelFileParserImpl implements FileParser{
	
	@Override
	public ParsedFileDto parse(MultipartFile file) throws IOException{
		
		List<String> headers = new ArrayList<>();
		
		List<String> rows = new ArrayList<>();
		
		DataFormatter formatter = new DataFormatter();
		
		try(Workbook workbook = new XSSFWorkbook(file.getInputStream())){
			Sheet sheet = workbook.getSheetAt(0);
			
			boolean headerRow = true;
			
			for(Row row : sheet) {
				
				if(headerRow) {
					
					for(Cell cell : row) {
						
						headers.add(formatter.formatCellValue(cell));
					}
					
					headerRow = false;
					continue;
				}
				
				List<String> values = new ArrayList<>();
				
				for(int i = 0; i < headers.size(); i++) {
					
					Cell cell = row.getCell(i);
					
					if(cell == null) {
						values.add("");
					}else {
						values.add(formatter.formatCellValue(cell));
					}
				}
				
				rows.add(String.join(",", values));
			}
		}
		
		return ParsedFileDto.builder()
				.headers(headers)
				.rows(rows)
				.build();
	}

}
