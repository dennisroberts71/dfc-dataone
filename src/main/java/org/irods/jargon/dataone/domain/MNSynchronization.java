package org.irods.jargon.dataone.domain;

import java.util.Date;

public class MNSynchronization {
	
	private Date lastHarvested;
	private Date lastCompleteHarvest;
	private MNSchedule schedule;
	
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

}
