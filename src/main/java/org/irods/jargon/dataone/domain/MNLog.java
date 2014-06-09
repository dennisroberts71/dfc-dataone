package org.irods.jargon.dataone.domain;

import java.util.ArrayList;
import java.util.List;

import org.dataone.service.types.v1.Log;
import org.dataone.service.types.v1.LogEntry;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "d1:log")
public class MNLog {
	
	private List<MNLogEntry> logEntry;

	public List<MNLogEntry> getLogEntry() {
		return logEntry;
	}

	public void setLogEntry(List<MNLogEntry> logEntry) {
		this.logEntry = logEntry;
	}
	
	public void copy(Log log) {
		
		if (log == null) {
			throw new IllegalArgumentException("MNLogy::copy - Log is null");
		}
		
		if (log.getLogEntryList() != null) {
			List<LogEntry> logEntryList = log.getLogEntryList();
			this.logEntry = new ArrayList<MNLogEntry>();
			
			for (LogEntry l : logEntryList) {
				MNLogEntry mnLogEntry = new MNLogEntry();
				mnLogEntry.copy(l);
				this.logEntry.add(mnLogEntry);
			}
		}
	}

}
