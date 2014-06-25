package org.irods.jargon.dataone.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlType;

import org.dataone.service.types.v1.Schedule;
import org.dataone.service.types.v1.Service;
import org.dataone.service.types.v1.Services;
import org.dataone.service.types.v1.Synchronization;

@XmlType(propOrder={"schedule","lastHarvested","lastCompleteHarvest"})
public class MNSynchronization {
	
	private Date lastHarvested;
	private Date lastCompleteHarvest;
	private MNSchedule schedule;
	
	public MNSynchronization() {
		this.schedule = new MNSchedule();
	}
	
	public Date getLastHarvested() {
		return lastHarvested;
	}

	public void setLastHarvested(Date lastHarvested) {
		this.lastHarvested = lastHarvested;
	}

	public Date getLastCompleteHarvest() {
		return lastCompleteHarvest;
	}

	public void setLastCompleteHarvest(Date lastCompleteHarvest) {
		this.lastCompleteHarvest = lastCompleteHarvest;
	}

	public MNSchedule getSchedule() {
		return schedule;
	}

	public void setSchedule(MNSchedule schedule) {
		this.schedule = schedule;
	}
	
	public void copy(Synchronization synch) {
		
		if (synch == null) {
			throw new IllegalArgumentException("MNSynchronization::copy - Synchronization is null");
		}
		
		if (synch.getLastHarvested() != null) {
			this.lastHarvested = synch.getLastHarvested();
		}
		
		if (synch.getLastCompleteHarvest() != null) {
			this.lastCompleteHarvest = synch.getLastCompleteHarvest();
		}
		
		if (synch.getSchedule() != null) {
			Schedule s = synch.getSchedule();
			this.schedule = new MNSchedule();
			this.schedule.copy(s);
		}
	}

}
