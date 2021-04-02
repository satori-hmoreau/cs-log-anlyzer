package ie.sator.csla;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import ie.sator.csla.services.EventMatchingService;
import ie.sator.csla.services.LogfileAnalyzer;

@Configuration
public class CsLogAnalyserConfiguration {

	@Autowired
	private EventMatchingService eventMatchingService;
	
	@Bean
	@Scope("prototype")
	public LogfileAnalyzer logfileAnalyzer() {
		return new LogfileAnalyzer(eventMatchingService);
	}
}
