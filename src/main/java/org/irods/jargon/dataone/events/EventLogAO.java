package org.irods.jargon.dataone.events;

import java.util.Date;

import org.dataone.service.exceptions.ServiceFailure;
import org.dataone.service.types.v1.Event;
import org.dataone.service.types.v1.Identifier;
import org.dataone.service.types.v1.Log;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.exception.InvalidArgumentException;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.RuleProcessingAO;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.core.rule.IRODSRuleExecResult;
import org.irods.jargon.dataone.auth.RestAuthUtils;
import org.irods.jargon.dataone.configuration.RestConfiguration;
import org.irods.jargon.dataone.utils.HandleUtils;

// implements access to the iRODS event log 
public class EventLogAO {
	
	// Hard-code subject for now
	private final String SUBJECT_USER = "Public";
	final IRODSAccessObjectFactory irodsAccessObjectFactory;
	final RestConfiguration restConfiguration;
	
	public EventLogAO(IRODSAccessObjectFactory irodsAccessObjectFactory,
			RestConfiguration restConfiguration) {
    	
		this.irodsAccessObjectFactory = irodsAccessObjectFactory;
		this.restConfiguration = restConfiguration;
	}
	
	
	// retrieve list of logEntry (events)
	//log?[fromDate={fromDate}][&toDate={toDate}][&event={event}][&pidFilter={pidFilter}][&start={start}][&count={count}]
	// use elastic search api to retrieve events
	public Log getLogs(Date fromDate, Date toDate, EventsEnum event, String pidFilter, int startIdx, int count) {
		return null;
	}
	
	
	// execute rule to add event to databook event log
	// sendAccess("synch_failure", user name, data object identifier, timestamp in seconds, short description);
	public void recordEvent(Event event, Identifier id, String description) 
								throws InvalidArgumentException, JargonException, ServiceFailure {
		
		// check for valid input parameters
		if (event == null) {
			throw new InvalidArgumentException("invalid Event parameter");
		}
		if (id == null) {
			throw new InvalidArgumentException("invalid data object identifier");
		}
		
		EventsEnum e = EventsEnum.valueOfFromDataOne(event);
		String databookEvent = e.getDatabookEvent();
		Long timeNow = new Long(java.lang.System.currentTimeMillis()/1000);
		String timeNowStr = timeNow.toString();
		
		HandleUtils handleUtils = new HandleUtils(restConfiguration, irodsAccessObjectFactory);
		long dataObject = handleUtils.getDataObjectIdFromDataOneIdentifier(id);
		String dataObjIdStr = new Long(dataObject).toString();
			
		IRODSAccount irodsAccount = RestAuthUtils
			.getIRODSAccountFromBasicAuthValues(this.restConfiguration);
		RuleProcessingAO ruleProcessingAO = irodsAccessObjectFactory
			.getRuleProcessingAO(irodsAccount);
		
		StringBuilder sb = new StringBuilder();
		sb.append("addEventRule {\n");
		sb.append(" sendAccess(\"");
		sb.append(databookEvent);
		sb.append("\", ");
		sb.append(SUBJECT_USER);
		sb.append("\", ");
		sb.append(dataObjIdStr);
		sb.append("\", ");
		sb.append(timeNowStr);
		if (description != null) {
			sb.append("\", ");
			sb.append(description);
		}
		sb.append("\");\n}i\n");
		sb.append("OUTPUT ruleExecOut");
		String ruleString = sb.toString();

		IRODSRuleExecResult result = ruleProcessingAO.executeRule(ruleString);
	}

}
