package ie.sator.csla;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import ie.sator.csla.services.EventMatchingService;
import ie.sator.csla.services.LogfileAnalyzer;

@Configuration
public class CsLogAnalyserConfiguration {

	@Autowired
	private EventMatchingService eventMatchingService;
	
	@Bean
	@Scope("prototype")
	public LogfileAnalyzer logfileAnalyzer() {
		return new LogfileAnalyzer(eventMatchingService, objectMapper());
	}
	
	@Bean
	public ObjectMapper objectMapper() {
		var objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return objectMapper;
	}
}
