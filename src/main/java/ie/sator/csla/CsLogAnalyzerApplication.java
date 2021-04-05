package ie.sator.csla;

/*
 * Copyright (C) Satori Ltd. 2021.
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ie.sator.csla.services.LogfileAnalyzerService;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class CsLogAnalyzerApplication implements CommandLineRunner {

	@Autowired
	private LogfileAnalyzerService logAnalyzerService;
	
	public static void main(String[] args) {
		var app = new SpringApplication(CsLogAnalyzerApplication.class);
		app.setBannerMode(Mode.OFF);
		app.run(args);
	}

	@Override
	public void run(String... args) throws Exception {
		for (var pathname: args) {
			log.debug("Analyze {}", pathname);
			var success = logAnalyzerService.analyzeFile(pathname);
			if (!success) {
				log.debug("Failed to analyze {}", pathname);
			}
		}
	}

}
