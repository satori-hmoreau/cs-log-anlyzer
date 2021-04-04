package ie.sator.csla.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * MatchedEvent gets stored in a repository when two InputEventData instances
 * are found for the same eventId and the states STARTED and FINISHED.
 * 
 */
@Builder
@Entity
@Table(name="matched_events")
public class MatchedEvent {

	@Getter
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Getter
	@Setter
	@Column(name="event_id", length=32)
	private String eventId;
	
	@Getter
	@Setter
	@Column(name="host", length=32)
	private String host;

	@Getter
	@Setter
	@Column(name="event_type", length=32)
	private String eventType;
	
	@Getter
	@Column(name="event_duration")
	private Long duration;
	
	@Getter
	@Column(name="long_event_alert")
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
