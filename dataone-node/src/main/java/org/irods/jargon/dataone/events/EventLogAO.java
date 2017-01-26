package org.irods.jargon.dataone.events;

import java.util.Date;

import org.dataone.service.exceptions.ServiceFailure;
import org.dataone.service.types.v1.Event;
import org.dataone.service.types.v1.Identifier;
import org.dataone.service.types.v1.Log;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.exception.InvalidArgumentException;

// implements access to the iRODS event log 
public interface EventLogAO {
	
	// retrieve list of logEntry (events)
	//log?[fromDate={fromDate}][&toDate={toDate}][&event={event}][&pidFilter={pidFilter}][&start={start}][&count={count}]
	// use elastic search api to retrieve events
	public Log getLogs(Date fromDate, Date toDate, EventsEnum event, String pidFilter, int startIdx, int count);
	
	
	// execute rule to add event to databook event log
	// sendAccess("synch_failure", user name, data object identifier, timestamp in seconds, short description);
	public void recordEvent(Event event, Identifier id, String description) 
								throws InvalidArgumentException, JargonException, ServiceFailure;

}
