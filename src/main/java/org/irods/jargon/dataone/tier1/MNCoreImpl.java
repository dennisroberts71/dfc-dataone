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
import org.irods.jargon.dataone.domain.MNNode;
import org.irods.jargon.dataone.tier1.model.MNCoreModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MNCoreImpl implements MNCore {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private final IRODSAccessObjectFactory irodsAccessObjectFactory;
	private final RestConfiguration restConfiguration;
//	private final MNCoreModel mnCoreModel;
    
    public MNCoreImpl(
    			IRODSAccessObjectFactory irodsAccessObjectFactory,
    			RestConfiguration restConfiguration) {
    	
    	this.irodsAccessObjectFactory = irodsAccessObjectFactory;
    	this.restConfiguration = restConfiguration;
//    	this.mnCoreModel = new MNCoreModel(irodsAccessObjectFactory, restConfiguration);
    }

    @Override
    public Date ping() throws NotImplemented, ServiceFailure, InsufficientResources {
        
        try {
			IRODSAccount irodsAccount = RestAuthUtils
					.getIRODSAccountFromBasicAuthValues(restConfiguration);
	
			EnvironmentalInfoAO environmentalInfoAO = irodsAccessObjectFactory
					.getEnvironmentalInfoAO(irodsAccount);
	
			long currentTime = environmentalInfoAO.getIRODSServerCurrentTime();
			return new Date(currentTime);
			
		} catch (Exception e) {
			log.error("ping failed: {}", e.getMessage());
			throw new ServiceFailure("500", "2042");
		} finally {
			irodsAccessObjectFactory.closeSessionAndEatExceptions();
		}
    }

    @Override
    public Log getLogRecords(Date date, Date date2, Event event, String s, Integer integer, Integer integer2) throws InvalidRequest, InvalidToken, NotAuthorized, NotImplemented, ServiceFailure {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Node getCapabilities() throws NotImplemented, ServiceFailure {
    	
    	Node node = new Node();
    	
    	Ping ping = new Ping();
    	ping.setSuccess(true);
    	
    	try {
			IRODSAccount irodsAccount = RestAuthUtils
					.getIRODSAccountFromBasicAuthValues(restConfiguration);
	
			EnvironmentalInfoAO environmentalInfoAO = irodsAccessObjectFactory
					.getEnvironmentalInfoAO(irodsAccount);
	
			long bootTime = environmentalInfoAO.getIRODSServerProperties().getServerBootTime();
			
		} catch (Exception e) {
			log.error("getCapabilities: iRODS server is not running");
			ping.setSuccess(false);
		} finally {
			irodsAccessObjectFactory.closeSessionAndEatExceptions();
		}
    	
    // TODO: need to figure out correct format for these and implement properly
    	List<Subject> subjects = new ArrayList<Subject>();
    	Subject subject = new Subject();
    	subject.setValue("CN=urn:node:DEMO2, DC=dataone, DC=org");
    	subjects.add(subject);
    	List<Subject> contactSubjects =  new ArrayList<Subject>();
    	Subject contactSubject = new Subject();
    	contactSubject.setValue("CN=METACAT1, DC=dataone, DC=org");
    	contactSubjects.add(contactSubject);

    	node.setPing(ping);
    	node.setSubjectList(subjects);
    	node.setContactSubjectList(contactSubjects);
    	
        return node;
    }

    @Override
    public Log getLogRecords(Session session, Date date1, Date date2, Event event, String s, Integer integer1, Integer integer2) throws InvalidRequest, InvalidToken, NotAuthorized, NotImplemented, ServiceFailure {
    	
    	Log log = null;
//    	log = mnCoreModel.doGetLogRecords(session, date1, date2, event, s, integer1, integer2);
    	
    	return log;
    }
    
}
