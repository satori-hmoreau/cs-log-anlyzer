package ie.sator.csla;

import org.springframework.boot.Banner.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ie.sator.csla.services.CSLogAnalyzerService;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class CsLogAnalyzerApplication implements CommandLineRunner {

	@Autowired
	private CSLogAnalyzerService logAnalyzer;
	
	public static void main(String[] args) {
		var app = new SpringApplication(CsLogAnalyzerApplication.class);
		app.setBannerMode(Mode.OFF);
		app.run(args);
	}

	@Override
	public void run(String... args) throws Exception {
		
	}

}
