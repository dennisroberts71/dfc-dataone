package org.irods.jargon.dataone.domain;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.dataone.service.types.v1.LogEntry;
import org.irods.jargon.dataone.utils.ISO8601;

//@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder={"entryId","identifier","ipAddress","userAgent","subject","event","dateLogged","nodeIdentifier"})
public class MNLogEntry {
	
	private String entryId;
	private String dateLogged;
	private String event;
	private String identifier;
	private String nodeIdentifier;
	private String ipAddress;
	private String userAgent;
	private String subject;
	
	public String getEntryId() {
		return entryId;
	}
	public void setEntryId(String entryId) {
		this.entryId = entryId;
	}
	
	public String getDateLogged() {
		return dateLogged;
	}
	public void setDateLogged(String dateLogged) {
		this.dateLogged = dateLogged;
	}
	
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public String getNodeIdentifier() {
		return nodeIdentifier;
	}
	public void setNodeIdentifier(String nodeIdentifier) {
		this.nodeIdentifier = nodeIdentifier;
	}
	
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public void copy(LogEntry logEntry) {
		
		if (logEntry == null) {
			throw new IllegalArgumentException("MNLogEntry::copy - LogEntry is null");
		}
		
		if (logEntry.getEntryId() != null) {
			this.entryId = logEntry.getEntryId();
		}
		
		if (logEntry.getDateLogged() != null) {
			this.dateLogged = ISO8601.convertToGMTString(logEntry.getDateLogged());
		}
		
		if (logEntry.getEvent() != null) {
			this.event = logEntry.getEvent().xmlValue();
		}
		
		if (logEntry.getIdentifier() != null) {
			this.identifier = logEntry.getIdentifier().getValue();
		}
		
		if (logEntry.getNodeIdentifier() != null) {
			this.nodeIdentifier = logEntry.getNodeIdentifier().getValue();
		}
		
		if (logEntry.getIpAddress() != null) {
			this.ipAddress = logEntry.getIpAddress();
		}
		
		else {
			// need to use empty string to make this tag show up
			this.ipAddress = new String();
		}
		
		if (logEntry.getUserAgent() != null) {
			this.userAgent = logEntry.getUserAgent();
		}
		else {
			// need to use empty string to make this tag show up
			this.userAgent = new String();
		}
		
		if (logEntry.getSubject() != null) {
			this.subject = logEntry.getSubject().getValue();
		}	
		
	}
		
}
