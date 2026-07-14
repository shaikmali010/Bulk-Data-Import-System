package com.shaik.bulkimport.validator;

import java.util.List;

public interface RecordValidator {

	List<String> validate(List<String> header, String rowData);
	
}
