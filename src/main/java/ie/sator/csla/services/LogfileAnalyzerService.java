package ie.sator.csla.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Basic Log analyzer service.
 * One of these should be created for each input file.
 * 
 */
@Service
@Slf4j
public class LogfileAnalyzerService implements ApplicationContextAware {
	
	public Boolean analyzeFile(String pathName) throws IOException {
		var path = Paths.get(pathName);
		if (!Files.exists(path)) {
			log.error("Cannot find file specified: {}", pathName);
			return false;
		}
		getLogFileAnalyzer().analyzeFile(path);
		return true;
	}
	
	@Setter
	private ApplicationContext applicationContext;
	
	private LogfileAnalyzer getLogFileAnalyzer() {
		return applicationContext.getBean(LogfileAnalyzer.class);
	}

}
