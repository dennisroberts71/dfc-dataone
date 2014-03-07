package org.irods.jargon.dataone.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "checksum")
public class MNChecksum {
	
	private String value;
	private String algorithm;
	
	public MNChecksum() {
		
	}	
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getAlgorithm() {
		return algorithm;
	}
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

}
