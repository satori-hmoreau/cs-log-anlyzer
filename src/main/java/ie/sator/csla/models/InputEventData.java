package ie.sator.csla.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InputEventData {

	public static final String STARTED = "STARTED";
	public static final String FINISHED = "FINISHED";
	
	private String eventId;
	private String state;
	private String eventType;
	private String host;
	@Builder.Default
	private Long timestamp = 0L;
	
	@JsonCreator
	public InputEventData(
			@JsonProperty(value="id", required=true) String eventId,
			@JsonProperty(value="state", required=true) String state,
			@JsonProperty("type") String eventType,
			@JsonProperty("host") String host,
			@JsonProperty(value="timestamp", required=true) Long timestamp) {
		this.eventId = eventId;
		this.state = state;
		if (eventType != null) {
            this.eventType = eventType;
		}
		if (host != null) {
            this.host = host;
		}
		this.timestamp = timestamp;
	}
	
	public Boolean isStartEvent() {
		return this.state.equals(STARTED);
	}
	
	public Boolean isFinishEvent() {
		return this.state.equals(FINISHED);
	}
	
}
