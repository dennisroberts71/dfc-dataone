package org.irods.jargon.dataone.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class MNAccessPolicy {
	
	private String subject; // MNSubject type?
	private List<MNPermissionEnum> permission;
	
	public MNAccessPolicy() {
		
	}
	
	@XmlElement(name = "permission")
	public List<MNPermissionEnum> getPermission() {
		return permission;
	}
	
	public void setPermission(List<MNPermissionEnum> p) {
		permission = p;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

}
