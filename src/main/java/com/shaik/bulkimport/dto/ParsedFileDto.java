package com.shaik.bulkimport.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsedFileDto {
	
	private List<String> headers;
	
	private List<String> rows;

}
