package org.irods.jargon.dataone.domain;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
	
	public MNSchedule() {
		initializeProperties();
	}
	
	private void initializeProperties() {
		Properties prop = new Properties();
		InputStream input = null;
	 
		try {
	 
			input = new FileInputStream("../configuration/config.properties");
	 
			// load a properties file
			prop.load(input);
			
			setHour(prop.getProperty("irods.dataone.sync.schedule.hour"));
			setMday(prop.getProperty("irods.dataone.sync.schedule.mday"));
			setMin(prop.getProperty("irods.dataone.sync.schedule.min"));
			setMon(prop.getProperty("irods.dataone.sync.schedule.mon"));
			setSec(prop.getProperty("irods.dataone.sync.schedule.sec"));
			setWday(prop.getProperty("irods.dataone.sync.schedule.wday"));
			setYear(prop.getProperty("irods.dataone.sync.schedule.year"));
	 
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
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
