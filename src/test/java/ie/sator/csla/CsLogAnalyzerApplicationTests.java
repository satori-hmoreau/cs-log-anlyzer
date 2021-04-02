package ie.sator.csla;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ie.sator.csla.services.CSLogAnalyzerService;

@SpringBootTest
class CsLogAnalyzerApplicationTests {

	@Test
	void contextLoads() throws Exception {
	}
	
	@Autowired
	private CSLogAnalyzerService logAnalyzer;
	
	@Test
	void serviceExists() throws Exception {
		assertTrue(logAnalyzer != null, () -> "No CsLogAnalyzer configured");
	}

}
