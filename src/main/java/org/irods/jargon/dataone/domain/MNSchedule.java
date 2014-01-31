package org.irods.jargon.dataone.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.NONE)
public class MNSchedule {
	
	@XmlAttribute
	private String hour;
	@XmlAttribute
	private String mday;
	@XmlAttribute
	private String min;
	@XmlAttribute
	private String mon;
	@XmlAttribute
	private String sec;
	@XmlAttribute
	private String wday;
	@XmlAttribute
	private String year;
	
	public String getHour() {
		return hour;
	}	
	public void setHour(String hour) {
		this.hour = hour;
	}
	
	public String getMday() {
		return mday;
	}
	public void setMday(String mday) {
		this.mday = mday;
	}
	
	public String getMin() {
		return min;
	}
	public void setMin(String min) {
		this.min = min;
	}
	
	public String getMon() {
		return mon;
	}
	public void setMon(String mon) {
		this.mon = mon;
	}
	
	public String getSec() {
		return sec;
	}
	public void setSec(String sec) {
		this.sec = sec;
	}
	
	public String getWday() {
		return wday;
	}
	public void setWday(String wday) {
		this.wday = wday;
	}
	
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}

}
