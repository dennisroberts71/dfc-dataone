package org.irods.jargon.dataone.domain;

import java.util.List;

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

}
