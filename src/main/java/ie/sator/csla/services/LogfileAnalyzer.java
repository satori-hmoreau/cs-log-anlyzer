package ie.sator.csla.services;
/*
 * Copyright (C) Satori Ltd. 2021.
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import ie.sator.csla.models.AnalyzerState;
import ie.sator.csla.models.InputEventData;
import ie.sator.csla.repositories.MatchedEventRepository;
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
	
	private MatchedEventRepository repository;
	
	private ObjectMapper objectMapper;
	
	private AtomicInteger lineCount = new AtomicInteger(0);
	
	@Getter
	private AnalyzerState status = AnalyzerState.INITIALIZED;
	
	public int lineCount() {
		return lineCount.intValue();
	}
	
	@Autowired
	public LogfileAnalyzer(EventMatchingService eventMatchingService, 
			ObjectMapper objectMapper, MatchedEventRepository repository) {
		this.eventMatchingService = eventMatchingService;
		this.objectMapper = objectMapper;
		this.repository = repository;
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
                long matchedCount = lineStream.map(this::parseEventData).filter(Optional::isPresent)
                	.filter(this::matchAndSaveEvent).collect(Collectors.counting());
                log.info("Matched {} events", matchedCount);
                status = AnalyzerState.COMPLETE;
            }
		} catch (IOException e) {
			log.error("{} caught trying to stream over {}", e, path);
			status = AnalyzerState.ERROR;
		}
	}
	
	@Transactional
	private Boolean matchAndSaveEvent(Optional<InputEventData> data) {
		var matchedEvent = eventMatchingService.matchIncomingEvent(data.get());
		if (matchedEvent.isPresent()) {
			repository.save(matchedEvent.get());
			return true;
		}
		return false;
	}

}
