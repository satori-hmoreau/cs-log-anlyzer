package ie.sator.csla.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * MatchedEvent gets stored in a repository when two InputEventData instances
 * are found for the same eventId and the states STARTED and FINISHED.
 * 
 */
@Builder
public class MatchedEvent {

	@Getter
	private Long id;
	@Getter
	@Setter
	private String eventId;
	
	@Getter
	@Setter
	private String host;
	@Getter
	@Setter
	private String eventType;
	
	@Getter
	private Long duration;
	@Setter
	private Boolean alert;
	
	public static MatchedEvent createFrom(InputEventData inputEvent) {
		return MatchedEvent.builder()
				.eventId(inputEvent.getEventId())
				.host(inputEvent.getHost())
				.eventType(inputEvent.getEventType())
				.duration(0L)
				.alert(false)
				.build();
	}
	
	private static Long LONG_DURATION = 4L;
	
	public void calculateDuration(InputEventData ev1, InputEventData ev2) {
		this.duration = Math.abs(ev1.getTimestamp() - ev2.getTimestamp());
		this.alert = this.duration > LONG_DURATION;
	}
}
