package ie.sator.csla.integration;
/*
 * Copyright (C) Satori Ltd. 2021.
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import ie.sator.csla.CsLogAnalyzerConfiguration;
import ie.sator.csla.models.AnalyzerState;
import ie.sator.csla.repositories.MatchedEventRepository;
import ie.sator.csla.services.EventMatchingService;
import ie.sator.csla.services.LogfileAnalyzer;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = {ConfigFileApplicationContextInitializer.class},
		classes={CsLogAnalyzerConfiguration.class,
				 EventMatchingService.class,
				 MatchedEventRepository.class})
@ActiveProfiles({"test"})
@DataJpaTest 
@ComponentScan("ie.sator.csla")
@EnableJpaRepositories(basePackages= {"ie.sator.csla"})
public class LogfileAnalyzerIntegrationTests {
	
	@Autowired
	private EventMatchingService eventMatchingService;
	
	@Autowired
	private MatchedEventRepository repository;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private LogfileAnalyzer logfileAnalyzer() {
		return new LogfileAnalyzer(eventMatchingService, objectMapper, repository);
	}

	private static final String EMPTY_FILE = "src/test/resources/empty_file.txt";
	
	@Test
	void analyseEmptyFileOK() throws Exception {
		var logAnalyzer = logfileAnalyzer();
		assertEquals(0, logAnalyzer.lineCount());
		logAnalyzer.analyzeFile(Paths.get(EMPTY_FILE));
		assertEquals(0, logAnalyzer.lineCount());
		assertEquals(AnalyzerState.COMPLETE, logAnalyzer.getStatus());
	}
	
	private static final String MISSING_FILE = "no_such_file.txt";
	
	@Test
	void analyseMissingFile() throws Exception {
		var logAnalyzer = logfileAnalyzer();
		logAnalyzer.analyzeFile(Paths.get(MISSING_FILE));
		assertEquals(AnalyzerState.ERROR, logAnalyzer.getStatus());
	}
	
	private static final String LOGFILE_TXT = "src/test/resources/logfile.txt";
	
	@Test
	void analyzeGoodFile() {
		var logAnalyzer = logfileAnalyzer();
		var expectedMatches = 3;
		var saveCount = repository.count();
		assertEquals(0, logAnalyzer.lineCount());
		logAnalyzer.analyzeFile(Paths.get(LOGFILE_TXT));
		assertTrue(logAnalyzer.lineCount() > 0, () -> "Did not count lines");
		assertEquals(AnalyzerState.COMPLETE, logAnalyzer.getStatus());
		assertEquals(saveCount + expectedMatches, repository.count(), () -> "Did not save expected number of matches");


	}
	
	private static final String BAD_LOGFILE_TXT = "src/test/resources/slightly_bad_logfile.txt";

	@Test
	void analyzeFileWithSomeBadData() {
		var logAnalyzer = logfileAnalyzer();
		var expectedMatches = 1;
		var saveCount = repository.count();
		assertEquals(0, logAnalyzer.lineCount());
		logAnalyzer.analyzeFile(Paths.get(BAD_LOGFILE_TXT));
		assertTrue(logAnalyzer.lineCount() > 0, () -> "Did not count lines");
		assertEquals(AnalyzerState.COMPLETE, logAnalyzer.getStatus());
		assertEquals(saveCount + expectedMatches, repository.count(), () -> "Did not save expected number of matches");
	}

}
