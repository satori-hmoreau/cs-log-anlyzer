package ie.sator.csla;
/*
 * Copyright (C) Satori Ltd. 2021.
 */

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ie.sator.csla.services.LogfileAnalyzerService;

@SpringBootTest
@ActiveProfiles({"test"})
class CsLogAnalyzerApplicationTests {

	@Test
	void contextLoads() throws Exception {
	}
	
	@Autowired
	private LogfileAnalyzerService logAnalyzer;
	
	@Test
	void serviceExists() throws Exception {
		assertTrue(logAnalyzer != null, () -> "No CsLogAnalyzerService configured");
	}
	
	@Test
	void runMainNoArgs() throws Exception {
		CsLogAnalyzerApplication.main(new String[] {"a", "b", "c"});
	}
	
}
