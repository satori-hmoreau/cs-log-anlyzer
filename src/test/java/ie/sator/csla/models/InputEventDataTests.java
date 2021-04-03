package ie.sator.csla.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import ie.sator.csla.CsLogAnalyserConfiguration;
import ie.sator.csla.services.EventMatchingService;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = {ConfigFileApplicationContextInitializer.class},
		classes = {CsLogAnalyserConfiguration.class, EventMatchingService.class})
@ActiveProfiles({"test"})
public class InputEventDataTests {

	@Autowired
	private ObjectMapper objectMapper;
	
	private static final String GOOD_JSON = 
			"{\"id\":\"scsmbstgra\", \"state\":\"STARTED\", " + 
			"\"type\":\"APPLICATION_LOG\", \"host\":\"12345\", " + 
			"\"timestamp\":1491377495212}";
	
	@Test
	void testGoodDeserialization() throws Exception {
		var inputEventData = objectMapper.readValue(GOOD_JSON, InputEventData.class);
		assertEquals("scsmbstgra", inputEventData.getEventId(), () -> "event id not as expected");
		assertEquals("APPLICATION_LOG", inputEventData.getEventType(), () -> "event type not as expected");
	}

	private static final String GOOD_JSON_NO_HOST_OR_TYPE = 
			"{\"id\":\"scsmbstgra\", \"state\":\"STARTED\", " + 
			"\"timestamp\":1491377495212}";
	
	@Test
	void testGoodDeserializationNoTypeOrHost() throws Exception {
		var inputEventData = objectMapper.readValue(GOOD_JSON_NO_HOST_OR_TYPE, InputEventData.class);
		assertEquals("scsmbstgra", inputEventData.getEventId(), () -> "event id not as expected");
		assertEquals(InputEventData.UNKNOWN_VALUE, inputEventData.getHost(), () -> "Host not default value");
		assertEquals(InputEventData.UNKNOWN_VALUE, inputEventData.getEventType(), () -> "Type not default value");
	}
	
	private static final String GOOD_JSON_MISSING_ID = 
			"{\"state\":\"STARTED\", " + 
			"\"type\":\"APPLICATION_LOG\", \"host\":\"12345\", " + 
			"\"timestamp\":1491377495212}";
	
	@Test
	void testMissingEventId() throws Exception {
		assertThrows(MismatchedInputException.class, () -> {
			objectMapper.readValue(GOOD_JSON_MISSING_ID, InputEventData.class); 
		});
	}

	private static final String GOOD_JSON_MISSING_TIMESTAMP = 
			"{\"id\":\"scsmbstgra\", \"state\":\"STARTED\", " + 
			"\"type\":\"APPLICATION_LOG\", \"host\":\"12345\" } ";
	
	@Test
	void testMissingTimestamp() throws Exception {
		assertThrows(MismatchedInputException.class, () -> {
			objectMapper.readValue(GOOD_JSON_MISSING_TIMESTAMP, InputEventData.class); 
		});
	}
	
	private static final String GOOD_JSON_MISSING_STATE = 
			"{\"id\":\"scsmbstgra\", " + 
			"\"type\":\"APPLICATION_LOG\", \"host\":\"12345\", " + 
			"\"timestamp\":1491377495212}";
	
	@Test
	void testMissingState() throws Exception {
		assertThrows(MismatchedInputException.class, () -> {
			objectMapper.readValue(GOOD_JSON_MISSING_STATE, InputEventData.class); 
		});
	}
	
	
}
