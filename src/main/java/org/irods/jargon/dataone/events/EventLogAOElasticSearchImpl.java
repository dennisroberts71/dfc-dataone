package org.irods.jargon.dataone.events;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.dataone.service.exceptions.ServiceFailure;
import org.dataone.service.types.v1.Event;
import org.dataone.service.types.v1.Identifier;
import org.dataone.service.types.v1.Log;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.InvalidArgumentException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.RuleProcessingAO;
import org.irods.jargon.core.rule.IRODSRuleExecResult;
import org.irods.jargon.dataone.auth.RestAuthUtils;
import org.irods.jargon.dataone.configuration.RestConfiguration;
import org.irods.jargon.dataone.id.UniqueIdAOHandleImpl;
import org.irods.jargon.dataone.utils.PropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.elasticsearch.search.SearchHit;

public class EventLogAOElasticSearchImpl implements EventLogAO {
	
		// Hard-code subject for now
		private final String SUBJECT_USER = "anonymous"; // TODO:should this be anonymous instead??
		final IRODSAccessObjectFactory irodsAccessObjectFactory;
		final RestConfiguration restConfiguration;
		
		private Logger log = LoggerFactory.getLogger(this.getClass());
		
		public EventLogAOElasticSearchImpl(IRODSAccessObjectFactory irodsAccessObjectFactory,
				RestConfiguration restConfiguration) {
	    	
			this.irodsAccessObjectFactory = irodsAccessObjectFactory;
			this.restConfiguration = restConfiguration;
		}
		
		
		// retrieve list of logEntry (events)
		//log?[fromDate={fromDate}][&toDate={toDate}][&event={event}][&pidFilter={pidFilter}][&start={start}][&count={count}]
		// TODO: The response MUST contain only records for which the requestor has permission to read. 
		@Override
		public Log getLogs(Date fromDate, Date toDate, EventsEnum event, String pidFilter, int startIdx, int count)
			throws NoNodeAvailableException {
			
			// ignoring pidFilter for now
			
			boolean datesExist = true;
			if (fromDate == null && toDate == null) {
				datesExist = false;
			}
			// maybe implement spring bean for these settings?
			// get elasticsearch properties
			PropertiesLoader loader = new PropertiesLoader();
			String elasticsearchDNS = loader.getProperty("irods.dataone.events.elasticsearch.dns");
			int elasticsearchport = Integer.parseInt(loader.getProperty("irods.dataone.events.elasticsearch.dns"));
			String searchIndex = loader.getProperty("irods.dataone.events.elasticsearch.searchindex");
			String searchType = loader.getProperty("irods.dataone.events.elasticsearch.searchtype");
			String rangeField = "created";

			BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
					.must(QueryBuilders.matchQuery("type", "databook.persistence.rule.rdf.ruleset.Access"));
				  //.must(QueryBuilders.matchQuery("userEntityLink", userEntityLink));
			if (event  != null) {
				boolQuery.must(QueryBuilders.matchQuery("title", event.getDatabookEvent()));
			}

			// Note that date time precision is limited to one millisecond.
			// TODO: If no timezone information is provided UTC will be assumed.
			RangeFilterBuilder filterBuilder = FilterBuilders
				.rangeFilter(rangeField);

			if (fromDate != null) {
				filterBuilder
					.from(fromDate.getTime()/1000)
					.includeLower(true);
			}
			if (toDate != null) {
				filterBuilder
				.to(toDate.getTime()/1000)
				.includeUpper(false);
			}

			Client client = new TransportClient()
	        	.addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
	

			SearchRequestBuilder srBuilder = client.prepareSearch(searchIndex)
					.setTypes(searchType)
					//.setSearchType(SearchType.DEFAULT)
					.setQuery(boolQuery)
					//.setPostFilter(filterBuilder)
					.setFrom(startIdx).setSize(count);
			if (datesExist) {
				srBuilder.setPostFilter(filterBuilder);
			}
			log.info("getLogs: built search request: {}", srBuilder.toString());
			
			SearchResponse response = srBuilder.execute().actionGet();
			log.info("getLogs: got search response: {}", response.toString());
			
			// get uri from response and validate 2 things:
			// event is related to a DataOne object AND it
			// TODO: is publicly accessible
			
			SearchHit[] searchHits = response.getHits().getHits();
		    Map<String, Object> s=searchHits[0].sourceAsMap();
//		    Map<String, Date> duration=(Map<String, Date>) s.get("duration");
//		    Date start=duration.get("start");
//		    Date end=duration.get("end");
		    
			// now put data and put in Log object
			Log log = new Log();

			return log;
		}
		
		
		// execute rule to add event to databook event log
		// sendAccess("synch_failure", user name, data object identifier, timestamp in seconds, short description);
		@Override
		public void recordEvent(Event event, Identifier id, String description) 
									throws InvalidArgumentException, JargonException, ServiceFailure {
			
			// check for valid input parameters
			if (event == null) {
				throw new InvalidArgumentException("invalid Event parameter");
			}
			if (id == null) {
				throw new InvalidArgumentException("invalid data object identifier");
			}
			
			EventsEnum e = EventsEnum.valueOfFromDataOne(event);
			String databookEvent = e.getDatabookEvent();
			Long timeNow = new Long(java.lang.System.currentTimeMillis()/1000);
			String timeNowStr = timeNow.toString();
			
			UniqueIdAOHandleImpl handleImpl = new UniqueIdAOHandleImpl(restConfiguration, irodsAccessObjectFactory);
			long dataObjectId = handleImpl.getDataObjectIdFromDataOneIdentifier(id);
			String dataObjIdStr = new Long(dataObjectId).toString();
				
			IRODSAccount irodsAccount = RestAuthUtils
				.getIRODSAccountFromBasicAuthValues(this.restConfiguration);
			RuleProcessingAO ruleProcessingAO = irodsAccessObjectFactory
				.getRuleProcessingAO(irodsAccount);
			
			StringBuilder sb = new StringBuilder();
			sb.append("addEventRule {\n");
			sb.append(" sendAccess(\"");
			sb.append(databookEvent);
			sb.append("\", \"");
			sb.append(SUBJECT_USER);
			sb.append("\", \"");
			sb.append(dataObjIdStr);
			sb.append("\", \"");
			sb.append(timeNowStr);
			if (description != null) {
				sb.append("\", \"");
				sb.append(description);
			}
			sb.append("\");\n}\n");
			sb.append("OUTPUT ruleExecOut");
			String ruleString = sb.toString();

			IRODSRuleExecResult result = ruleProcessingAO.executeRule(ruleString);
		}

}
