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
import org.irods.jargon.dataone.tier1.model.MNCoreModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MNCoreImpl implements MNCore {
	
	private final IRODSAccessObjectFactory irodsAccessObjectFactory;
	private final RestConfiguration restConfiguration;
	private final MNCoreModel mnCoreModel;
    
    public MNCoreImpl(
    			IRODSAccessObjectFactory irodsAccessObjectFactory,
    			RestConfiguration restConfiguration) {
    	
    	this.irodsAccessObjectFactory = irodsAccessObjectFactory;
    	this.restConfiguration = restConfiguration;
    	this.mnCoreModel = new MNCoreModel(irodsAccessObjectFactory, restConfiguration);
    }

    @Override
    public Date ping() throws NotImplemented, ServiceFailure, InsufficientResources {
    	
    	Date serverTime = null;
    	serverTime = mnCoreModel.doPing();

        return serverTime;
    }

    @Override
    public Log getLogRecords(Date date, Date date2, Event event, String s, Integer integer, Integer integer2) throws InvalidRequest, InvalidToken, NotAuthorized, NotImplemented, ServiceFailure {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Node getCapabilities() throws NotImplemented, ServiceFailure {
    	
    	Node node = null;
    	node = mnCoreModel.doGetCapabilities();
    	
    	return node;
    }

    @Override
    public Log getLogRecords(Session session, Date date1, Date date2, Event event, String s, Integer integer1, Integer integer2) throws InvalidRequest, InvalidToken, NotAuthorized, NotImplemented, ServiceFailure {
    	
    	Log log = null;
    	log = mnCoreModel.doGetLogRecords(session, date1, date2, event, s, integer1, integer2);
    	
    	return log;
    }
}
