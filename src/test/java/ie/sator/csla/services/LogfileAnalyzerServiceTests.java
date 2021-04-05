package ie.sator.csla.services;
/*
 * Copyright (C) Satori Ltd. 2021.
 */

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
		initializers = {ConfigFileApplicationContextInitializer.class}, 
		classes = {LogfileAnalyzerService.class})
@ActiveProfiles({"test"})
class LogfileAnalyzerServiceTests {
	
	@MockBean
	private LogfileAnalyzer logfileAnalyzer;
	
	@Autowired
	private LogfileAnalyzerService logAnalyzer;

	private static final String EMPTY_FILE = "src/test/resources/empty_file.txt";
	
	@Test
	void analyseEmptyFileOK() throws Exception {
		var emptyfilePath = Paths.get(EMPTY_FILE);
		assertTrue(logAnalyzer.analyzeFile(EMPTY_FILE), 
			() -> "Failed to analyze existing empty file.");
		verify(logfileAnalyzer, times(1)).analyzeFile(ArgumentMatchers.eq(emptyfilePath));
	}
	
	private static final String MISSING_FILE = "no_such_file.txt";
	
	@Test
	void analyseMissingFile() throws Exception {
		assertFalse(logAnalyzer.analyzeFile(MISSING_FILE), 
				() -> "Should have failed with non-existent file");
		verify(logfileAnalyzer, times(0)).analyzeFile(ArgumentMatchers.any());
	}

}
