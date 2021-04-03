package ie.sator.csla.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import ie.sator.csla.CsLogAnalyserConfiguration;
import ie.sator.csla.models.AnalyzerState;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = {ConfigFileApplicationContextInitializer.class},
		classes={CsLogAnalyserConfiguration.class})
@ActiveProfiles({"test"})
class LogfileAnalyzerTests {
	
	@MockBean
	private EventMatchingService eventMatchingService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private LogfileAnalyzer logfileAnalyzer() {
		return new LogfileAnalyzer(eventMatchingService, objectMapper);
	}

	private static final String EMPTY_FILE = "src/test/resources/empty_file.txt";
	
	@BeforeEach
	void setupEventMatcher() {
		when(eventMatchingService.matchIncomingEvent(ArgumentMatchers.any())).thenReturn(true);
	}
	
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
		assertEquals(0, logAnalyzer.lineCount());
		logAnalyzer.analyzeFile(Paths.get(LOGFILE_TXT));
		assertTrue(logAnalyzer.lineCount() > 0, () -> "Did not count lines");
		assertEquals(AnalyzerState.COMPLETE, logAnalyzer.getStatus());
		verify(eventMatchingService, times(logAnalyzer.lineCount()))
			.matchIncomingEvent(ArgumentMatchers.any());
	}
	
	private static final String BAD_LOGFILE_TXT = "src/test/resources/slightly_bad_logfile.txt";
	private static final int BAD_LINE_COUNT = 3;

	@Test
	void analyzeFileWithSomeBadData() {
		var logAnalyzer = logfileAnalyzer();
		assertEquals(0, logAnalyzer.lineCount());
		logAnalyzer.analyzeFile(Paths.get(BAD_LOGFILE_TXT));
		assertTrue(logAnalyzer.lineCount() > 0, () -> "Did not count lines");
		assertEquals(AnalyzerState.COMPLETE, logAnalyzer.getStatus());
		verify(eventMatchingService, times(logAnalyzer.lineCount() - BAD_LINE_COUNT))
			.matchIncomingEvent(ArgumentMatchers.any());
		
	}

}
