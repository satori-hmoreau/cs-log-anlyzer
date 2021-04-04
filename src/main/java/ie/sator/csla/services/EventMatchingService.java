package ie.sator.csla.services;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import ie.sator.csla.models.InputEventData;
import ie.sator.csla.models.MatchedEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * EventMatchingService matches InputEventData instances with the
 * same eventId and complementary STARTED and FINISHED states.
 * It saves the matched events as MatchedEvents in a repository.
 * 
 * It keeps a running total of the number of events it has hatched
 * and you can also query it for the current set of unmatched events.
 *
 * This is a stateful service, so it provides a reset() method to reset its state,
 */
@Service
@Slf4j
public class EventMatchingService {

	private Map<String, InputEventData> map = new ConcurrentHashMap<>();
	
	private AtomicInteger matchCount = new AtomicInteger(0);
	
	public Boolean matchIncomingEvent(InputEventData eventData) {
		if (!matchableEvent(eventData)) {
			log.debug("Not matching event with state {}", eventData.getState());
			return false;
		}
		var matchCandidate = MatchedEvent.createFrom(eventData);
		var result = map.merge(eventData.getEventId(), eventData, 
				(existingEv, newEv) ->
					calculateMatchedEvents(existingEv, newEv, matchCandidate) ? null : existingEv);
		if (result == null) {
			matchCount.getAndIncrement();
		}
		return result == null;
	}
	
	private Boolean matchableEvent(InputEventData eventData) {
		return eventData.getState().equals(InputEventData.FINISHED) ||
				eventData.getState().equals(InputEventData.STARTED);
	}
	
	private Boolean calculateMatchedEvents(InputEventData ev1, InputEventData ev2, MatchedEvent matchResult) {
		if (ev1.isStartEvent() && ev2.isFinishEvent()  
				|| ev1.isFinishEvent() && ev2.isStartEvent()) {
			matchResult.calculateDuration(ev1, ev2);
			if (ev1.isStartEvent()) {
				if (ev1.getEventType() != null) {
					matchResult.setEventType(ev1.getEventType());
				}
				if (ev1.getHost() != null) {
					matchResult.setHost(ev1.getHost());
				}
			}
			return true;
		}
		return false;
	}
	public Integer getMatchedCount() {
		return matchCount.intValue();
	}
	
	public Collection<InputEventData> getUnmatchedEvents() {
		return map.values();
	}
	
	public synchronized void reset() {
		if (map.size() > 0) {
			log.warn("Discarding {} unmatched events", map.size());
		}
		map = new ConcurrentHashMap<>();
		matchCount.set(0);
	}
}
