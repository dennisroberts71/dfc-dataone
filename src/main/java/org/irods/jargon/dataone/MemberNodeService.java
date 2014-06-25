package org.irods.jargon.dataone;

import org.dataone.service.exceptions.InsufficientResources;
import org.dataone.service.exceptions.InvalidRequest;
import org.dataone.service.exceptions.InvalidToken;
import org.dataone.service.exceptions.NotAuthorized;
import org.dataone.service.exceptions.NotFound;
import org.dataone.service.exceptions.NotImplemented;
import org.dataone.service.exceptions.ServiceFailure;
import org.dataone.service.exceptions.SynchronizationFailed;
import org.dataone.service.types.v1.Checksum;
import org.dataone.service.types.v1.DescribeResponse;
import org.dataone.service.types.v1.Event;
import org.dataone.service.types.v1.Identifier;
import org.dataone.service.types.v1.Log;
import org.dataone.service.types.v1.Node;
import org.dataone.service.types.v1.ObjectFormatIdentifier;
import org.dataone.service.types.v1.ObjectList;
import org.dataone.service.types.v1.Schedule;
import org.dataone.service.types.v1.Services;
import org.dataone.service.types.v1.Service;
import org.dataone.service.types.v1.Session;
import org.dataone.service.types.v1.Subject;
import org.dataone.service.types.v1.Synchronization;
import org.dataone.service.types.v1.SystemMetadata;
import org.irods.jargon.core.exception.InvalidArgumentException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.dataone.tier1.MNCoreImpl;
import org.irods.jargon.dataone.tier1.MNReadImpl;
import org.irods.jargon.dataone.utils.ISO8601;
import org.irods.jargon.dataone.configuration.RestConfiguration;
import org.irods.jargon.dataone.domain.MNChecksum;
import org.irods.jargon.dataone.domain.MNLog;
import org.irods.jargon.dataone.domain.MNObjectList;
import org.irods.jargon.dataone.domain.MNSchedule;
import org.irods.jargon.dataone.domain.MNService;
import org.irods.jargon.dataone.domain.MNSynchronization;
import org.irods.jargon.dataone.domain.MNPing;
import org.irods.jargon.dataone.domain.MNNode;
import org.irods.jargon.dataone.domain.MNSystemMetadata;
import org.irods.jargon.dataone.events.EventLogAOElasticSearchImpl;
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
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
    	
    	nodeCapabilities.copy(node);
    	  	  		
    	return nodeCapabilities;
    }
    
    @GET
    @Path("/")
    @Produces(MediaType.TEXT_XML)
    @Mapped(namespaceMap = { @XmlNsMap(namespace = "http://irods.org/irods-dataone", jsonName = "irods-dataone") })
    public MNNode handleDeafaultGetCapabilities()
    		throws NotImplemented, ServiceFailure {
    	
    	MNNode nodeCapabilities = handleGetCapabilities();
    	  	  		
    	return nodeCapabilities;
    }
    
    //log?[fromDate={fromDate}][&toDate={toDate}][&event={event}][&pidFilter={pidFilter}][&start={start}][&count={count}]
	@GET
    @Path("/log")
    @Produces(MediaType.TEXT_XML)
    @Mapped(namespaceMap = { @XmlNsMap(namespace = "http://irods.org/irods-dataone", jsonName = "irods-dataone") })
    public MNLog handleGetLogRecords(
//    							@QueryParam("fromDate") Date fromDate,
//    							@QueryParam("toDate") Date toDate,
    							@QueryParam("fromDate") String fromDateStr,
    							@QueryParam("toDate") String toDateStr,
    							@QueryParam("event") Event event,
    							@QueryParam("pidFilter") String pidFilter,
    							@DefaultValue("0") @QueryParam("start") int start,
    							@DefaultValue("1000") @QueryParam("count") int count
    							)
    							throws NotImplemented,
    								   ServiceFailure,
    								   NotAuthorized, 
    								   InvalidRequest,
    								   InvalidToken {

		log.info("/log request: fromData={} toDate={}", fromDateStr, toDateStr);
		log.info("/log request: event={} pidFilter={}", event, pidFilter);
		log.info("/log request: start={} count={}", start, count);
		
		MNCoreImpl mnCoreImpl = new MNCoreImpl(irodsAccessObjectFactory, restConfiguration);
    	
		// TODO: make sure dates are converted to UTC?
		// parse date strings
		Date fromDate = null;
		Date toDate = null;
		try {
			if (fromDateStr != null) {
				fromDate = ISO8601.toCalendar(fromDateStr).getTime();
			}
			if (fromDateStr != null) {
				toDate = ISO8601.toCalendar(toDateStr).getTime();
			}
		} catch (ParseException e) {
			log.error("handleListObjects: unable to parse query dates");
			throw new InvalidRequest("1480", e.getMessage());
		}
		
		Log log = mnCoreImpl.getLogRecords(
									fromDate,
									toDate,
									event,
									pidFilter,
									start,
									count);
		
		// set Log attributes in MNLog
		MNLog mnLog = new MNLog();
		mnLog.copy(log);
    	
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

//		if (pid == null || pid.isEmpty()) {
//			throw new NotFound("1020", "invalid iRODS data object id");
//		}
		
		MNReadImpl mnReadImpl = new MNReadImpl(irodsAccessObjectFactory, restConfiguration);
		
		Identifier id = new Identifier();
		id.setValue(pid);
		// Had to write new method in mnReadImpl since I
		// couldn't figure out how to override the get method
		mnReadImpl.streamObject(response, id);
		
		// now log the event
		EventLogAOElasticSearchImpl eventLog = new EventLogAOElasticSearchImpl(irodsAccessObjectFactory, restConfiguration);
		try {
			eventLog.recordEvent(Event.READ, id, "DataONE replication");
		} catch (Exception e) {
			log.error("Unable to log EVENT: {} for data object id: {}", Event.READ, pid);
		}
		
	}
		
	@GET
	@Path("/checksum/{id}")
	@Produces(MediaType.TEXT_XML)
	public MNChecksum handleGetChecksum(@PathParam("id") final String pid, 
								  @DefaultValue("MD5") @QueryParam("checksumAlgorithm") final String algorithm) 
										  throws InvalidToken,
										  ServiceFailure,
										  NotAuthorized,
										  NotFound,
										  NotImplemented,
										  InvalidRequest {
		
		MNChecksum mnChecksum = new MNChecksum();
		
		if (!algorithm.toUpperCase().equals("MD5")) {
			throw new InvalidRequest("1402", "invalid checksum algorithm requested - only MD5 supported");
		}

//		if (pid == null || pid.isEmpty()) {
//			throw new NotFound("1420", "invalid iRODS data object id");
//		}
		
		Identifier id = new Identifier();
		id.setValue(pid);
		
		MNReadImpl mnReadImpl = new MNReadImpl(irodsAccessObjectFactory, restConfiguration);
		Checksum checksum = mnReadImpl.getChecksum(id, algorithm);
		
		mnChecksum.copy(checksum);		
		
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


//		if (pid == null || pid.isEmpty()) {
//			throw new NotFound("2185", "invalid iRODS data object id");
//		}
		
		MNReadImpl mnReadImpl = new MNReadImpl(irodsAccessObjectFactory, restConfiguration);
		
		Identifier id = new Identifier();
		id.setValue(pid);
		
		// Had to write new method in mnReadImpl since I
		// couldn't figure out how to override the get method
		mnReadImpl.streamObject(response, id);
		
		// now log the event
		EventLogAOElasticSearchImpl eventLog = new EventLogAOElasticSearchImpl(irodsAccessObjectFactory, restConfiguration);
		try {
			eventLog.recordEvent(Event.REPLICATE, id, "DataONE replication");
		} catch (Exception e) {
			log.error("Unable to log EVENT: {} for data object id: {}", Event.REPLICATE, pid);
		}
		
	}
	
	@GET
	@Path("/meta/{id}")
	@Produces(MediaType.TEXT_XML)
	public MNSystemMetadata handleGetSystemMetadata(@PathParam("id") final String pid)
								  //@Context final HttpServletResponse response) 
										  throws InvalidToken,
										  ServiceFailure,
										  NotAuthorized,
										  NotFound,
										  NotImplemented,
										  InvalidRequest {
		
		MNSystemMetadata mnSystemMetadata = new MNSystemMetadata();

//		if (pid == null || pid.isEmpty()) {
//			throw new NotFound("1420", "invalid iRODS data object id");
//		}
		
		Identifier id = new Identifier();
		id.setValue(pid);
		
		MNReadImpl mnReadImpl = new MNReadImpl(irodsAccessObjectFactory, restConfiguration);
		SystemMetadata sysMetadata = mnReadImpl.getSystemMetadata(id);
		
		mnSystemMetadata.copy(sysMetadata);		
		
		return mnSystemMetadata;
	}
	
	@HEAD
    @Path("/object/{id}")
    @Produces(MediaType.TEXT_XML)
    @Mapped(namespaceMap = { @XmlNsMap(namespace = "http://irods.org/irods-dataone", jsonName = "irods-dataone") })
    public Response handleDescribe(@PathParam("id") final String pid)
			  //@Context final HttpServletResponse response)
    		throws NotAuthorized, NotImplemented, ServiceFailure, NotFound, InvalidToken {

//		if (pid == null || pid.isEmpty()) {
//			throw new NotFound("1420", "invalid iRODS data object id");
//		}
		
		Identifier id = new Identifier();
		id.setValue(pid);
		
		DescribeResponse describeResponse;

		MNReadImpl mnReadImpl = new MNReadImpl(irodsAccessObjectFactory, restConfiguration);
		try {	
			describeResponse = mnReadImpl.describe(id);
		} catch (NotFound ex) {
			
			Response.ResponseBuilder builder = Response.ok();
			builder.status(Status.NOT_FOUND);
//	        builder.header("Last-Modified", dateStr);
//	        builder.header("Content-Length", describeResponse.getContent_Length().toString());
	        builder.type(MediaType.TEXT_XML);
	        builder.header("DataONE-Exception-Name", "NotFound");
	        builder.header("DataONE-Exception-DetailCode", ex.getDetail_code());
	        builder.header("DataONE-Exception-Description", ex.getDescription());
	        builder.header("DataONE-Exception-PID", pid);
	        
	        return builder.build();
		}
    	
    	Checksum checksum = describeResponse.getDataONE_Checksum();
    	String checksumStr = checksum.getAlgorithm() + "," + checksum.getValue();
    	
    	//TODO: test this!
//    	SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
//		String dateStr = df.format(describeResponse.getLast_Modified());
    	Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    	cal.setTime(describeResponse.getLast_Modified());
    	String dateStr = ISO8601.fromCalendar(cal);
		
		Response.ResponseBuilder builder = Response.ok();
        builder.header("Last-Modified", dateStr);
        builder.header("Content-Length", describeResponse.getContent_Length().toString());
        builder.type(MediaType.APPLICATION_OCTET_STREAM);
        builder.header("DataONE-formatId", describeResponse.getDataONE_ObjectFormatIdentifier().getValue().toString());
        builder.header("DataONE-Checksum", checksumStr);
        builder.header("DataONE-SerialVersion", describeResponse.getSerialVersion().toString());
        
        return builder.build();
    }
	
	@POST
    @Path("/error")
    @Produces(MediaType.TEXT_XML)
    @Mapped(namespaceMap = { @XmlNsMap(namespace = "http://irods.org/irods-dataone", jsonName = "irods-dataone") })
    public Response handleSynchronizationFailed(
				final SynchronizationFailed message)
    		throws NotAuthorized, NotImplemented, ServiceFailure, InvalidToken {

		if (message == null) {
			throw new ServiceFailure("2161", "Synch Failure Exception object is null");
		}
		
		MNReadImpl mnReadImpl = new MNReadImpl(irodsAccessObjectFactory, restConfiguration);
		boolean success = mnReadImpl.synchronizationFailed(message);
		
		if (!success) {
			throw new ServiceFailure("2161", "Failed to log Synchronization Failure event");
		}
		
		Response.ResponseBuilder builder = Response.ok();
		
		return builder.build();
	}
	
	// GET /object[?fromDate={fromDate}&toDate={toDate}&formatId={formatId}
	// &replicaStatus={replicaStatus} &start={start}&count={count}]
	@GET
	@Path("/object")
	@Produces(MediaType.TEXT_XML)
	public MNObjectList handleListObjects( 
//					@QueryParam("fromDate") final Date fromDate,
//					@QueryParam("toDate") final Date toDate,
					@QueryParam("fromDate") final String fromDateStr,
					@QueryParam("toDate") final String toDateStr,
					@QueryParam("formatId") final String formatIdStr,
					@QueryParam("replicaStatus") final Boolean replicaStatus,
					@DefaultValue("0") @QueryParam("start") final Integer start,
					@DefaultValue("1000") @QueryParam("count") final Integer count
				) throws 
					InvalidToken,
					ServiceFailure,
					NotAuthorized,
					InvalidRequest,
					NotImplemented {
		MNObjectList mnObjectList = new MNObjectList();
		
		// parse any query arguments to convert into correct formats,
		// if not handled by data type constructors
		ObjectFormatIdentifier formatId = new ObjectFormatIdentifier();
		formatId.setValue(formatIdStr);
		
		Date fromDate = null;
		Date toDate = null;
		try {
			if (fromDateStr != null) {
				fromDate = ISO8601.toCalendar(fromDateStr).getTime();
			}
			if (toDateStr != null) {
				toDate = ISO8601.toCalendar(toDateStr).getTime();
			}
		} catch (ParseException e) {
			log.error("handleListObjects: unable to parse query dates");
			throw new InvalidRequest("1540", e.getMessage());
		}
		
		MNReadImpl mnReadImpl = new MNReadImpl(irodsAccessObjectFactory, restConfiguration);
		ObjectList objectList = mnReadImpl.listObjects(fromDate, toDate, formatId, replicaStatus, start, count);
		mnObjectList.copy(objectList);
		
		return mnObjectList;
	}
}
