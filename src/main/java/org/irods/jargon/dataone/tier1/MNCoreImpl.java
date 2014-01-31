package org.irods.jargon.dataone.tier1;

import org.dataone.service.exceptions.*;
import org.dataone.service.mn.tier1.v1.MNCore;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MNCoreImpl implements MNCore {
	
	private final IRODSAccessObjectFactory irodsAccessObjectFactory;
	private final RestConfiguration restConfiguration;
	private final String authorization;
    
    public MNCoreImpl(
    			IRODSAccessObjectFactory irodsAccessObjectFactory,
    			RestConfiguration restConfiguration,
    			String authorization) {
    	
    	this.irodsAccessObjectFactory = irodsAccessObjectFactory;
    	this.restConfiguration = restConfiguration;
    	this.authorization = authorization;
    }

    @Override
    public Date ping() throws NotImplemented, ServiceFailure, InsufficientResources {
    	
    	Date serverTime = null;
    	
    	try {
			IRODSAccount irodsAccount = RestAuthUtils
					.getIRODSAccountFromBasicAuthValues(authorization,
							restConfiguration);

			EnvironmentalInfoAO environmentalInfoAO = irodsAccessObjectFactory
					.getEnvironmentalInfoAO(irodsAccount);

			IRODSServerProperties irodsServerProperties = environmentalInfoAO
					.getIRODSServerPropertiesFromIRODSServer();
			long currentTime = environmentalInfoAO.getIRODSServerCurrentTime();
			serverTime = new Date(currentTime);

// don't need this for now
//			ServerInfo serverInfo = new ServerInfo();
//			serverInfo.setApiVersion(irodsServerProperties.getApiVersion());
//			serverInfo.setIcatEnabled(irodsServerProperties.getIcatEnabled());
//			serverInfo.setInitializeDate(irodsServerProperties
//					.getInitializeDate());
//			serverInfo.setRelVersion(irodsServerProperties.getRelVersion());
//			serverInfo.setRodsZone(irodsServerProperties.getRodsZone());
//			serverInfo.setServerBootTime(irodsServerProperties
//					.getServerBootTime());
//			serverInfo.setCurrentServerTime(currentTime);
			
    	} catch (Exception e) {
    		throw new ServiceFailure(e.getMessage(), e.toString());
		} finally {
			irodsAccessObjectFactory.closeSessionAndEatExceptions();
		}

        return serverTime;
    }

    @Override
    public Log getLogRecords(Date date, Date date2, Event event, String s, Integer integer, Integer integer2) throws InvalidRequest, InvalidToken, NotAuthorized, NotImplemented, ServiceFailure {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Node getCapabilities() throws NotImplemented, ServiceFailure {
    	
    	// TODO: need to fill in the real stuff here
    	// get most from a config file I think
    	NodeReference identifier = new NodeReference();
    	identifier.setValue("mynode");
    	
    	String name = "test name";
    	
    	String description = "test dscription";
    	
    	String baseURL = "http://test.me.org";

    	Service s1 = new Service();
    	s1.setName("servicename1");
    	s1.setVersion("version1");
    	s1.setAvailable(true);
    	Service s2 = new Service();
    	s2.setName("servicename2");
    	s2.setVersion("version2");
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

    @Override
    public Log getLogRecords(Session session, Date date, Date date2, Event event, String s, Integer integer, Integer integer2) throws InvalidRequest, InvalidToken, NotAuthorized, NotImplemented, ServiceFailure {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
