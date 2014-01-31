package org.irods.jargon.dataone.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "nodeCapabilities")
public class MNNode {
	
	public MNNode() {
	}
	
	
	private String identifier; // MN NodeReference type
	private String name;
	private String description;
	private String baseURL;
	private List<MNService> services;
	private MNSynchronization synchronization;
	private MNPing ping;
//	private MNSubject subject;
//	private MNSubject contactSubject;
	private List<String> subject;
	private List<String> contactSubject;
	
	
	//public NodeReference getIdentifier() {
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getBaseURL() {
		return baseURL;
	}
	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}
	@XmlElementWrapper(name = "services")
	@XmlElement(name = "service")
	public List<MNService> getServices() {
		return services;
	}
	public void setServices(List<MNService> services) {
		this.services = services;
	}
	
	public MNSynchronization getSynchronization() {
		return synchronization;
	}
	public void setSynchronization(MNSynchronization synchronization) {
		this.synchronization = synchronization;
	}
	
	public MNPing getPing() {
		return ping;
	}
	public void setPing(MNPing ping) {
		this.ping = ping;
	}
	
//	public MNSubject getSubject() {
//		return subject;
//	}
//	public void setSubject(MNSubject subject) {
//		this.subject = subject;
//	}
//	
//	public MNSubject getContactSubject() {
//		return contactSubject;
//	}
//	public void setContactSubject(MNSubject contactSubject) {
//		this.contactSubject = contactSubject;
//	}
	
	public List<String> getSubject() {
		return subject;
	}
	public void setSubject(List<String> subject) {
		this.subject = subject;
	}
	
	
	public List<String> getContactSubject() {
		return contactSubject;
	}
	public void setContactSubject(List<String> contactSubject) {
		this.contactSubject = contactSubject;
	}

}
