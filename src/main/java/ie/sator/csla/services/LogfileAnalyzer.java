package ie.sator.csla.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import ie.sator.csla.models.AnalyzerState;
import ie.sator.csla.models.InputEventData;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * LogFileAnalyzer reads a log file and
 * for each line, tries to create an InputEventData
 * It passes each InputEventData to the EventMatchingServicce
 * to turn into a matched event.
 * 
 */
@Slf4j
public class LogfileAnalyzer {
	
	private EventMatchingService eventMatchingService;
	
	private ObjectMapper objectMapper;
	
	private AtomicInteger lineCount = new AtomicInteger(0);
	
	@Getter
	private AnalyzerState status = AnalyzerState.INITIALIZED;
	
	public int lineCount() {
		return lineCount.intValue();
	}
	
	@Autowired
	public LogfileAnalyzer(EventMatchingService eventMatchingService, ObjectMapper objectMapper) {
		this.eventMatchingService = eventMatchingService;
		this.objectMapper = objectMapper;
	}
	
 
	private Optional<InputEventData> parseEventData(String data) {
        lineCount.getAndIncrement();
		try {
			var inputEventData = objectMapper.readValue(data, InputEventData.class);
			return Optional.of(inputEventData);
		} catch (Exception e) {
			log.error("Failed to create event data for '{}' at line {}, error: {}", 
					data, lineCount.intValue(), e.getMessage());
			return Optional.empty();
		}
	}

	public void analyzeFile(Path path) {
		try {
            try (var lineStream = Files.lines(path)) {
            	status = AnalyzerState.PROCESSSING;
                long matchedCount = lineStream.map(this::parseEventData).filter(o -> o.isPresent())
                	.filter(o -> eventMatchingService.matchIncomingEvent(o.get())).collect(Collectors.counting());
                log.info("Matched {} events", matchedCount);
                status = AnalyzerState.COMPLETE;
            }
		} catch (IOException e) {
			log.error("{} caught trying to stream over {}", e, path);
			status = AnalyzerState.ERROR;
		}
	}

}
