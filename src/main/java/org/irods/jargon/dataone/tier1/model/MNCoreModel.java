package org.irods.jargon.dataone.tier1.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.dataone.service.exceptions.NotImplemented;
import org.dataone.service.exceptions.ServiceFailure;
import org.dataone.service.types.v1.Event;
import org.dataone.service.types.v1.Log;
import org.dataone.service.types.v1.Node;
import org.dataone.service.types.v1.NodeReference;
import org.dataone.service.types.v1.Ping;
import org.dataone.service.types.v1.Schedule;
import org.dataone.service.types.v1.Service;
import org.dataone.service.types.v1.Services;
import org.dataone.service.types.v1.Session;
import org.dataone.service.types.v1.Subject;
import org.dataone.service.types.v1.Synchronization;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.connection.IRODSServerProperties;
import org.irods.jargon.core.pub.EnvironmentalInfoAO;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.dataone.auth.RestAuthUtils;
import org.irods.jargon.dataone.configuration.RestConfiguration;

public class MNCoreModel {
	
	private final IRODSAccessObjectFactory irodsAccessObjectFactory;
	private final RestConfiguration restConfiguration;
	
	public MNCoreModel(IRODSAccessObjectFactory irodsAccessObjectFactory, RestConfiguration restConfiguration) {
		this.irodsAccessObjectFactory = irodsAccessObjectFactory;
		this.restConfiguration = restConfiguration;
	}
	
	public Date doPing() throws ServiceFailure {
		
		try {
			IRODSAccount irodsAccount = RestAuthUtils
					.getIRODSAccountFromBasicAuthValues(restConfiguration);
	
			EnvironmentalInfoAO environmentalInfoAO = irodsAccessObjectFactory
					.getEnvironmentalInfoAO(irodsAccount);
	
			long currentTime = environmentalInfoAO.getIRODSServerCurrentTime();
			return new Date(currentTime);
			
		} catch (Exception e) {
			throw new ServiceFailure(e.getMessage(), e.toString());
		} finally {
			irodsAccessObjectFactory.closeSessionAndEatExceptions();
		}
	}
	
	public Node doGetCapabilities() throws NotImplemented, ServiceFailure {
		
		// TODO: need to fill in the real stuff here
    	// get most from a config file I think
    	NodeReference identifier = new NodeReference();
    	identifier.setValue("urn:node:DFC");
    	
    	String name = "DFC : iRODS Member Node";
    	
    	String description = "DFC DataONE Memeber Node";
    	
    	String baseURL = "https://dfcweb.datafed.org/irods-dataone/rest/mn/v1";

    	Service s1 = new Service();
    	s1.setName("MNCore");
    	s1.setVersion("v1");
    	s1.setAvailable(true);
    	Service s2 = new Service();
    	s2.setName("MNRead");
    	s2.setVersion("v1");
    	s2.setAvailable(false);
    	Services services = new Services();
    	services.addService(s1);
    	services.addService(s2);
    	
    	Synchronization synchronization = new Synchronization();
    	Schedule schedule = new Schedule();
    	schedule.setHour("hour");
    	schedule.setMday("mday");
    	schedule.setMin("min");
    	schedule.setMon("mon");
    	schedule.setSec("sec");
    	schedule.setWday("wday");
    	schedule.setYear("year");
    	synchronization.setSchedule(schedule);
    	synchronization.setLastHarvested(new Date());
    	synchronization.setLastCompleteHarvest(new Date());
    	
    	Ping ping = new Ping();
    	ping.setSuccess(true);
    	
    	List<Subject> subjects = new ArrayList<Subject>();
    	Subject subject = new Subject();
    	subject.setValue("CN=urn:node:DEMO2, DC=dataone, DC=org");
    	subjects.add(subject);
    	List<Subject> contactSubjects =  new ArrayList<Subject>();
    	Subject contactSubject = new Subject();
    	contactSubject.setValue("CN=METACAT1, DC=dataone, DC=org");
    	contactSubjects.add(contactSubject);
    	
    	// populate node capabilties here
    	Node node = new Node();
    	node.setIdentifier(identifier);
    	node.setName(name);
    	node.setDescription(description);
    	node.setBaseURL(baseURL);
    	node.setServices(services);
    	node.setSynchronization(synchronization);
    	node.setPing(ping);
    	node.setSubjectList(subjects);
    	node.setContactSubjectList(contactSubjects);
    	
        return node;
	}
	
	public Log doGetLogRecords(Session session, Date date, Date date2, Event event, String s, Integer integer, Integer integer2) {
		return null;
	}

}
