package org.irods.jargon.dataone.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.NONE)
public class MNSubject {
	
	@XmlAttribute
	private String CN;
	@XmlAttribute
	private List<String> DC;
	
	
	public String getCN() {
		return CN;
	}
	public void setCN(String cN) {
		CN = cN;
	}
	
	public List<String> getDC() {
		return DC;
	}
	public void setDC(List<String> dC) {
		DC = dC;
	}

}
