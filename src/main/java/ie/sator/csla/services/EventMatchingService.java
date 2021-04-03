package ie.sator.csla.services;

import org.springframework.stereotype.Service;

import ie.sator.csla.models.InputEventData;

@Service
public class EventMatchingService {

	public Boolean matchIncomingEvent(InputEventData eventData) {
		return true;
	}
}
