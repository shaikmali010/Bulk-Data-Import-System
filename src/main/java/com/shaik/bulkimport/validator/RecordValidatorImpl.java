package com.shaik.bulkimport.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class RecordValidatorImpl implements RecordValidator{
	
	private static final Set<String> NUMERIC_FIELDS = Set.of(
	        "age",
	        "salary",
	        "price",
	        "amount",
	        "quantity",
	        "stock",
	        "count");

	private static final Set<String> EMAIL_FIELDS = Set.of(
	        "email");

	private static final Set<String> MOBILE_FIELDS = Set.of(
	        "mobile",
	        "phone",
	        "contact");

	
	
	@Override
	public List<String> validate(List<String> headers, String rowData){
		
		List<String> errors = new ArrayList<>();
		
		String[] values = rowData.split(",");
		
		if(values.length != headers.size()) {
			errors.add("Invlaid number of columns.");
			return errors;
		}
		
			for(int i = 0; i < headers.size(); i++) {
				
				String header = headers.get(i).trim();
		
				String value = values[i].trim();
				
				if(value.isEmpty()) {
					errors.add(header+" is required");
					continue;
				}
				
				if(EMAIL_FIELDS.contains(header.toLowerCase())) {
					
					if(!value.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
						errors.add(header + " format is invalid");
					}
				}
				
				if(MOBILE_FIELDS.contains(header.toLowerCase())) {
					
					if(!value.matches("\\d{10}")) {
						errors.add(header + " must contain exactly 10 didgits."); 
					}
				}
				
				if(NUMERIC_FIELDS.contains(header.toLowerCase())) {

	            try {

	            	double number = Double.parseDouble(value);
	            	
	            	if(number <= 0) {
	            		errors.add(header + " must be greater than 0.");
	            	}
	              

	               

	            } catch (NumberFormatException ex) {

	                errors.add(header + " must be valid number.");
	            }
	        }      
	}

		return errors;
	}
}
