package org.irods.jargon.dataone.domain;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.dataone.service.types.v1.Checksum;

@XmlRootElement(name = "checksum")
public class MNChecksum {
	
	@XmlAttribute
	private String algorithm;
	
	@XmlValue
	private String value;
	
	public MNChecksum() {
		
	}	
	
//	public String getValue() {
//		return value;
//	}
	public void setValue(String value) {
		this.value = value;
	}
//	public String getAlgorithm() {
//		return algorithm;
//	}
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}
	
	public void copy(Checksum checksum) {
		
		if (checksum == null) {
			throw new IllegalArgumentException("MNChecksum::copy - Checksum is null");
		}
		
		if (checksum.getValue() != null) {
			this.value = checksum.getValue();
		}
		
		if (checksum.getAlgorithm() != null) {
			this.algorithm = checksum.getAlgorithm();
		}
		
	}

}
