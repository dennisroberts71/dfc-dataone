package org.irods.jargon.dataone.events;

import java.util.Date;

import org.dataone.service.types.v1.Event;
import org.dataone.service.types.v1.Log;

// implements access to the iRODS event log 
public class EventLogAO {
	
	// Hard-code subject for now
	private final String SUBJECT_USER = "Public";
	
	// retrieve list of logEntry (events)
	//log?[fromDate={fromDate}][&toDate={toDate}][&event={event}][&pidFilter={pidFilter}][&start={start}][&count={count}]
	public Log getLogs(Date fromDate, Date toDate, EventsEnum event, String pidFilter, int startIdx, int count) {
		return null;
	}
	
	public void recordEvent(Event event) {
		
	}

}
