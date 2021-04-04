package ie.sator.csla.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import ie.sator.csla.CsLogAnalyserConfiguration;
import ie.sator.csla.models.InputEventData;
import ie.sator.csla.repositories.MatchedEventRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = {ConfigFileApplicationContextInitializer.class},
		classes={CsLogAnalyserConfiguration.class, EventMatchingService.class})
@ActiveProfiles({"test"})
class EventMatchingServiceTests {
	
	@Autowired
	private EventMatchingService eventMatchingService;
	
	@MockBean
	private MatchedEventRepository matchedEventRepository;
	
	@Test
	void contextInitializedOK() {
		assertNotNull(eventMatchingService);
	}
	
	@Test
	void testSimpleMatchOperation() {
		eventMatchingService.reset();
		var ev1 = InputEventData.builder().eventId("aaa").state(InputEventData.FINISHED).timestamp(1234L).build();
		var ev2 = InputEventData.builder().eventId("aaa").state(InputEventData.STARTED).timestamp(1230L).build();
		eventMatchingService.matchIncomingEvent(ev1);
		assertTrue(eventMatchingService.getMatchedCount() == 0, 
				() -> "Has a non zero matched count");
		assertTrue(eventMatchingService.getUnmatchedEvents().size() > 0, 
				() -> "Has no unmatched events");
		eventMatchingService.matchIncomingEvent(ev2);
		assertTrue(eventMatchingService.getMatchedCount() == 1, 
				() -> "Failed to match event aaa");
		assertTrue(eventMatchingService.getUnmatchedEvents().size() == 0, 
				() -> "Should not have unmatched events");
	}
	
	@Test
	void testMatchedEventHostAndEventType() {
		eventMatchingService.reset();
		var ev1host = "EV1_HOST";
		var ev1eventType = "EV1_EVENT_TYPE";
		var ev2eventType = "EV2_EVENT_TYPE";
		var ev1 = InputEventData.builder()
				.eventId("aaa").state(InputEventData.FINISHED).timestamp(1234L)
				.eventType(ev1eventType).host(ev1host)
				.build();
		var ev2 = InputEventData.builder()
				.eventId("aaa").state(InputEventData.STARTED).timestamp(1230L)
				.eventType(ev2eventType)
				.build();
		eventMatchingService.matchIncomingEvent(ev1);
		var matchedEvent = eventMatchingService.matchIncomingEvent(ev2);
		assertTrue(matchedEvent.isPresent(), () -> "Failed to match events");
		assertEquals(ev1host, matchedEvent.get().getHost(), () -> "Wrong value for host");
		assertEquals(ev2eventType, matchedEvent.get().getEventType(), () -> "Wrong value for eventType");
	}
	
	@Test
	void testNotMatchingWithWrongState() {
		var ev1 = InputEventData.builder().eventId("aaa").state("IN_PROGRESS").timestamp(1234L).build();
		var savedMatchCount = eventMatchingService.getMatchedCount();
		var savedUnmatchedSize = eventMatchingService.getUnmatchedEvents().size();
		assertFalse(eventMatchingService.matchIncomingEvent(ev1).isPresent(), 
				() -> "Matched event with wrong state");
		assertTrue(eventMatchingService.getMatchedCount() == savedMatchCount, 
				() -> "matchedCount has changed");
		assertTrue(eventMatchingService.getUnmatchedEvents().size() == savedUnmatchedSize, 
				() -> "Unmatched events has changed");
		
	}
	
	@Test
	void testNotMatchingTwoStarts() {
		eventMatchingService.reset();
		var ev1 = InputEventData.builder().eventId("aaa").state(InputEventData.STARTED).timestamp(1234L).build();
		var ev2 = InputEventData.builder().eventId("aaa").state(InputEventData.STARTED).timestamp(1236L).build();
		eventMatchingService.matchIncomingEvent(ev1);
		assertFalse(eventMatchingService.matchIncomingEvent(ev2).isPresent(), () -> "Matched 2 STARTED events");
		eventMatchingService.reset();
	}
	
	@Test
	void testNotMatchingDifferentEvents() {
		eventMatchingService.reset();
		var ev1 = InputEventData.builder().eventId("aaa").state(InputEventData.STARTED).timestamp(1234L).build();
		var ev2 = InputEventData.builder().eventId("aab").state(InputEventData.FINISHED).timestamp(1230L).build();
		eventMatchingService.matchIncomingEvent(ev1);
		assertFalse(eventMatchingService.matchIncomingEvent(ev2).isPresent(), () -> "Matched events with different ids");
		eventMatchingService.reset();
		
	}
}
