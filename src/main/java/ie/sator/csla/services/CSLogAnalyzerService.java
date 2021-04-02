package ie.sator.csla.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.Getter;

/**
 * Basic Log analyzer service.
 * One of these should be created for each input file.
 * 
 */
@Service
public class CSLogAnalyzerService {

	@Value("${csla.inputFile:none}")
	@Getter
	private String inputFilename;
	
}
