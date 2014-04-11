package org.irods.jargon.dataone;

import org.dataone.service.exceptions.InsufficientResources;
import org.dataone.service.exceptions.InvalidRequest;
import org.dataone.service.exceptions.InvalidToken;
import org.dataone.service.exceptions.NotAuthorized;
import org.dataone.service.exceptions.NotFound;
import org.dataone.service.exceptions.NotImplemented;
import org.dataone.service.exceptions.ServiceFailure;
import org.dataone.service.types.v1.Checksum;
import org.dataone.service.types.v1.Event;
import org.dataone.service.types.v1.Identifier;
import org.dataone.service.types.v1.Log;
import org.dataone.service.types.v1.Node;
import org.dataone.service.types.v1.Schedule;
import org.dataone.service.types.v1.Services;
import org.dataone.service.types.v1.Service;
import org.dataone.service.types.v1.Session;
import org.dataone.service.types.v1.Subject;
import org.dataone.service.types.v1.Synchronization;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.dataone.tier1.MNCoreImpl;
import org.irods.jargon.dataone.tier1.MNReadImpl;
import org.irods.jargon.dataone.configuration.RestConfiguration;
import org.irods.jargon.dataone.domain.MNChecksum;
import org.irods.jargon.dataone.domain.MNLog;
import org.irods.jargon.dataone.domain.MNSchedule;
import org.irods.jargon.dataone.domain.MNService;
import org.irods.jargon.dataone.domain.MNSynchronization;
import org.irods.jargon.dataone.domain.MNPing;
import org.irods.jargon.dataone.domain.MNNode;
import org.jboss.resteasy.annotations.providers.jaxb.json.Mapped;
import org.jboss.resteasy.annotations.providers.jaxb.json.XmlNsMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.icu.text.SimpleDateFormat;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: Lisa Stillwell - RENCI, UNC @ Chapel Hill
 * Date: 10/4/13
 * Time: 2:22 PM
 */

@Named
@Path("/mn/v1")
public class MemberNodeService {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Inject
	IRODSAccessObjectFactory irodsAccessObjectFactory;

	@Inject
	RestConfiguration restConfiguration;

    @GET
    @Path("/monitor/ping")
    @Produces(MediaType.TEXT_XML)
    @Mapped(namespaceMap = { @XmlNsMap(namespace = "http://irods.org/irods-dataone", jsonName = "irods-dataone") })
//    public Response handlePing(@HeaderParam("Authorization") final String authorization)
    public Response handlePing()
    //public void handlePing(@Context final HttpServletResponse response)
    		throws NotImplemented, ServiceFailure, InsufficientResources, JargonException {
    	
//    	if (authorization == null || authorization.isEmpty()) {
//			throw new IllegalArgumentException("null or empty authorization");
//		}

		if (irodsAccessObjectFactory == null) {
			throw new IllegalArgumentException("null irodsAccessObjectFactory");
		}

//    	MNCoreImpl mnCoreImpl = new MNCoreImpl(irodsAccessObjectFactory, restConfiguration, authorization);
		MNCoreImpl mnCoreImpl = new MNCoreImpl(irodsAccessObjectFactory, restConfiguration);
    	Date irodsDate = mnCoreImpl.ping();
		
		SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
		String dateStr = df.format(irodsDate);
		
		Response.ResponseBuilder builder = Response.ok();
        builder.header("Date", dateStr);
        builder.type("text/xml");
        builder.header("Content-Length", "0");
        
        return builder.build();
    }
    
    @GET
    @Path("/node")
    @Produces(MediaType.TEXT_XML)
    @Mapped(namespaceMap = { @XmlNsMap(namespace = "http://irods.org/irods-dataone", jsonName = "irods-dataone") })
    public MNNode handleGetCapabilities()
    		throws NotImplemented, ServiceFailure {
    	
    	MNNode nodeCapabilities = new MNNode();
    	    	
    	MNCoreImpl mnCoreImpl = new MNCoreImpl(irodsAccessObjectFactory, restConfiguration);
    	Node node = mnCoreImpl.getCapabilities();
    	
//    	nodeCapabilities.setName(node.getName());
//    	nodeCapabilities.setIdentifier(node.getIdentifier().getValue());
//    	nodeCapabilities.setDescription(node.getDescription());
//    	nodeCapabilities.setBaseURL(node.getBaseURL());
//    	
//    	Services services = node.getServices();
//    	List<MNService> mnServices = new ArrayList<MNService>();
//    	for (int i=0; i<services.sizeServiceList(); i++) {
//    		Service s = services.getService(i);
//    		MNService mnService = new MNService();
//    		mnService.setName(s.getName());
//    		mnService.setVersion(s.getVersion());
//    		mnService.setAvailable(s.getAvailable());
//    		mnServices.add(mnService);
//    	}
//    	nodeCapabilities.setServices(mnServices);
//    	
//    	MNSynchronization mnSynchronization = new MNSynchronization();
//    	Synchronization synchronization= node.getSynchronization();
//    	
//    	MNSchedule mnSchedule = new MNSchedule();
//    	
//    	Schedule schedule = synchronization.getSchedule();
//    	mnSchedule.setHour(schedule.getHour());
//    	mnSchedule.setMday(schedule.getMday());
//    	mnSchedule.setMin(schedule.getMin());
//    	mnSchedule.setMon(schedule.getMon());
//    	mnSchedule.setSec(schedule.getSec());
//    	mnSchedule.setWday(schedule.getWday());
//    	mnSchedule.setYear(schedule.getYear());
//    	
//    	mnSynchronization.setSchedule(mnSchedule);
//    	mnSynchronization.setLastHarvested(synchronization.getLastHarvested());
//    	mnSynchronization.setLastCompleteHarvest(synchronization.getLastCompleteHarvest());
//    	nodeCapabilities.setSynchronization(mnSynchronization);
    	
    	MNPing mnPing = new MNPing();
    	mnPing.setSuccess(node.getPing().getSuccess());
    	nodeCapabilities.setPing(mnPing);
    	if(node.getPing().getSuccess()) {
    		nodeCapabilities.setState("up");
    	}
    	else {
    		nodeCapabilities.setState("down");
    	}
    	
    	List<String> s1 = new ArrayList<String>();
    	List<Subject> subjects = node.getSubjectList();
    	for (int i=0; i<subjects.size(); i++) {
    		Subject s = subjects.get(i);
    		s1.add(s.getValue());
    	}
    	nodeCapabilities.setSubject(s1);
    	
    	List<String> s2 = new ArrayList<String>();
    	List<Subject> contactSubjects = node.getContactSubjectList();
    	for (int i=0; i<contactSubjects.size(); i++) {
    		Subject s = contactSubjects.get(i);
    		s2.add(s.getValue());
    	}
    	nodeCapabilities.setSubject(s1);
    	nodeCapabilities.setContactSubject(s2);
    	  	  		
    	return nodeCapabilities;
    }
    
    //log?[fromDate={fromDate}][&toDate={toDate}][&event={event}][&pidFilter={pidFilter}][&start={start}][&count={count}]
	@GET
    @Path("/log")
    @Produces(MediaType.TEXT_XML)
    @Mapped(namespaceMap = { @XmlNsMap(namespace = "http://irods.org/irods-dataone", jsonName = "irods-dataone") })
    public MNLog handleGetLogRecords(
    							@HeaderParam("Authorization") final String authorization,
    							@QueryParam("fromDate") Date fromDate,
    							@QueryParam("toDate") Date toDate,
    							@QueryParam("event") Event event,
    							@QueryParam("pidFilter") String pidFilter,
    							@QueryParam("start") int start,
    							@QueryParam("count") int count
    							)
    							throws NotImplemented,
    								   ServiceFailure,
    								   NotAuthorized, 
    								   InvalidRequest,
    								   InvalidToken {

		log.info("/log request: fromData={} toDate={}", fromDate, toDate);
		log.info("/log request: event={} pidFilter={}", event, pidFilter);
		log.info("/log request: start={} count={}", start, count);
		
		MNCoreImpl mnCoreImpl = new MNCoreImpl(irodsAccessObjectFactory, restConfiguration);
		//TODO: need to permissions of user here?
		// If no session info provided - defaults to public user
    	Session session = new Session();
    	
		Log log = mnCoreImpl.getLogRecords(
									session,
									fromDate,
									toDate,
									event,
									pidFilter,
									start,
									count);
		
		// set Log attributes in MNLog
		MNLog mnLog = new MNLog();
    	
		return mnLog;
	}

	@GET
	@Path("/object/{id}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public void handleRead(@PathParam("id") final String pid,
								  @Context final HttpServletResponse response) 
										  throws InvalidToken,
										  ServiceFailure,
										  NotAuthorized,
										  NotFound,
										  NotImplemented,
										  InsufficientResources {


		if (pid == null || pid.isEmpty()) {
			throw new NotFound("404", "1020");
		}
		
		MNReadImpl mnReadImpl = new MNReadImpl(irodsAccessObjectFactory, restConfiguration);
		
		// Had to write new method in mnReadImpl since I
		// couldn't figure out how to override the get method
		mnReadImpl.streamObject(response, pid);
		
	}
		
	@GET
	@Path("/checksum/{id}")
	@Produces(MediaType.TEXT_XML)
	public MNChecksum handleGetChecksum(@PathParam("id") final String pid, 
								  @DefaultValue("MD5") @QueryParam("checksumAlgorithm") final String algorithm,
								  @Context final HttpServletResponse response) 
										  throws InvalidToken,
										  ServiceFailure,
										  NotAuthorized,
										  NotFound,
										  NotImplemented,
										  InvalidRequest {
		
		MNChecksum mnChecksum = new MNChecksum();
		
		if (!algorithm.toUpperCase().equals("MD5")) {
			throw new InvalidRequest("400", "1402");
		}

		if (pid == null || pid.isEmpty()) {
			throw new NotFound("404", "1420");
		}
		
		Identifier id = new Identifier();
		id.setValue(pid);
		
		MNReadImpl mnReadImpl = new MNReadImpl(irodsAccessObjectFactory, restConfiguration);
		Checksum checksum = mnReadImpl.getChecksum(id, algorithm);
		
		mnChecksum.setValue(checksum.getValue());
		mnChecksum.setAlgorithm(checksum.getAlgorithm());		
		
		return mnChecksum;
	}
	
	@GET
	@Path("/replica/{id}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public void handleReplica(@PathParam("id") final String pid,
								  @Context final HttpServletResponse response) 
										  throws InvalidToken,
										  ServiceFailure,
										  NotAuthorized,
										  NotFound,
										  NotImplemented,
										  InsufficientResources {


		if (pid == null || pid.isEmpty()) {
			throw new NotFound("404", "2185");
		}
		
		MNReadImpl mnReadImpl = new MNReadImpl(irodsAccessObjectFactory, restConfiguration);
		
		// Had to write new method in mnReadImpl since I
		// couldn't figure out how to override the get method
		mnReadImpl.streamObject(response, pid);
		
	}
}
