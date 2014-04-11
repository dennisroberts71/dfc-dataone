package org.irods.jargon.dataone.events;

import org.dataone.service.types.v1.Event;

// Use this class to convert iRODS type events to DataONE events
public enum EventsEnum {
	
	CREATE(),
	READ(),
	UPDATE(),
	DELETE(),
	REPLICATE(),
	SYNCHRONIZATION_FAILED(),
	REPLICATION_FAILED(),;
	
	
	EventsEnum() {	
	}
	

}