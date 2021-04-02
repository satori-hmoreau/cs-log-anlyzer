package ie.sator.csla.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;

import ie.sator.csla.models.AnalyzerState;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogfileAnalyzer {
	
	private EventMatchingService eventMatchingService;
	
	private AtomicInteger lineCount = new AtomicInteger(0);
	
	@Getter
	private AnalyzerState status = AnalyzerState.INITIALIZED;
	
	public int lineCount() {
		return lineCount.intValue();
	}
	
	@Autowired
	public LogfileAnalyzer(EventMatchingService eventMatchingService) {
		this.eventMatchingService = eventMatchingService;
	}
	
	public void analyzeFile(Path path) {
		try {
            try (var lineStream = Files.lines(path)) {
            	status = AnalyzerState.PROCESSSING;
                lineStream.forEach((line) -> {
                	lineCount.getAndIncrement();
                	log.debug(line);
                });
                status = AnalyzerState.COMPLETE;
            }
		} catch (IOException e) {
			log.error("{} caught trying to stream over {}", e, path);
			status = AnalyzerState.ERROR;
		}
	}

}
