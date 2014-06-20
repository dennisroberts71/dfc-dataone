
package org.irods.jargon.dataone.domain;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.dataone.service.types.v1.Node;
import org.dataone.service.types.v1.Ping;
import org.dataone.service.types.v1.Service;
import org.dataone.service.types.v1.Services;
import org.dataone.service.types.v1.Subject;
import org.dataone.service.types.v1.Synchronization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name = "node")
@XmlType(propOrder={"identifier","name","description","baseURL",
			"services","synchronization","ping","subject","contactSubject"})
public class MNNode {
	
	private final String serviceKey = "irods.dataone.service.";
	private final String serviceKeyName = ".name";
	
	@XmlAttribute
	private String replicate;
	
	@XmlAttribute
	private String synchronize;
	
	@XmlAttribute 
	private String type;
	
	@XmlAttribute
	private String state;
	
	private String identifier; // MN NodeReference type
	private String name;
	private String description;
	private String baseURL;
	private List<MNService> services;
	private MNSynchronization synchronization;
//	private NodeReplicationPolicy nodeReplicationPolicy;
	private MNPing ping;
	private List<String> subject;
	private List<String> contactSubject;
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public MNNode() {
		initializeProperties();
	}
	
	private void initializeProperties() {
		Properties prop = new Properties();
		InputStream input = null;
	 
		try {
			String filename = "d1client.properties";
			input = getClass().getClassLoader().getResourceAsStream(filename);
	 
			// load a properties file
			prop.load(input);
			
			this.replicate = prop.getProperty("irods.dataone.replicate");
			this.synchronize = prop.getProperty("irods.dataone.synchronize");
			this.type = prop.getProperty("irods.dataone.type");
	//TODO: make sure proper exceptions are caught and handled
			
			this.identifier = prop.getProperty("irods.dataone.identifier");
			this.name = prop.getProperty("irods.dataone.name");
			this.description = prop.getProperty("irods.dataone.description");
			this.baseURL = prop.getProperty("irods.dataone.baseurl");
			this.services = initServices(prop);
			this.synchronization = initSychronization(prop);
//			this.subject = initSubjects(prop);
//			this.contactSubject = initContactSubjects(prop);
	 
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
	
	private List<String> initContactSubjects(Properties prop) {
		// TODO Auto-generated method stub
		return null;
	}

	private List<String> initSubjects(Properties prop) {
		// TODO Auto-generated method stub
		return null;
	}

	private MNSynchronization initSychronization(Properties prop) {
		MNSynchronization mnSynchronization = new MNSynchronization();
				
		return mnSynchronization;
	}

	private List<MNService> initServices(Properties prop) {
		List<MNService> services = new ArrayList<MNService>();
		
		Enumeration<?> keys = prop.propertyNames();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			if((key.startsWith(this.serviceKey)) && (key.endsWith(this.serviceKeyName))) {
				String serviceName = prop.getProperty(key);
				MNService service = new MNService();
				service.setName(serviceName);
				String isServiceAvailableKey = this.serviceKey + serviceName.toLowerCase();
				String isServiceAvailableValue = prop.getProperty(isServiceAvailableKey);
				service.setAvailable(Boolean.parseBoolean(isServiceAvailableValue));
				String verKey = isServiceAvailableKey + ".version";
				service.setVersion(prop.getProperty(verKey));
				services.add(service);
			}
		}
			
		return services;
	}

	//public NodeReference getIdentifier() {
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getBaseURL() {
		return baseURL;
	}
	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}
	
	@XmlElementWrapper(name = "services")
	@XmlElement(name = "service")
	public List<MNService> getServices() {
		return services;
	}
	public void setServices(List<MNService> services) {
		this.services = services;
	}
	
	public MNSynchronization getSynchronization() {
		return synchronization;
	}
	public void setSynchronization(MNSynchronization synchronization) {
		this.synchronization = synchronization;
	}
	
	public MNPing getPing() {
		return ping;
	}
	public void setPing(MNPing ping) {
		this.ping = ping;
	}
	
//	public MNSubject getSubject() {
//		return subject;
//	}
//	public void setSubject(MNSubject subject) {
//		this.subject = subject;
//	}
//	
//	public MNSubject getContactSubject() {
//		return contactSubject;
//	}
//	public void setContactSubject(MNSubject contactSubject) {
//		this.contactSubject = contactSubject;
//	}
	
	public List<String> getSubject() {
		return subject;
	}
	public void setSubject(List<String> subject) {
		this.subject = subject;
	}
	
	
	public List<String> getContactSubject() {
		return contactSubject;
	}
	public void setContactSubject(List<String> contactSubject) {
		this.contactSubject = contactSubject;
	}
	
//	public NodeReplicationPolicy getNodeReplicationPolicy() {
//		return nodeReplicationPolicy;
//	}
//
//	public void setNodeReplicationPolicy(NodeReplicationPolicy nodeReplicationPolicy) {
//		this.nodeReplicationPolicy = nodeReplicationPolicy;
//	}

	
	public void copy(Node node) {
		
		if (node == null) {
			throw new IllegalArgumentException("MNNode::copy - Node is null");
		}
		
		this.replicate = Boolean.toString(node.isReplicate());
		this.synchronize = Boolean.toString(node.isSynchronize());
		
		if (node.getType() != null) {
			this.type = node.getType().name();
		}
		
		if (node.getState() != null) {
			this.state = node.getState().xmlValue();
		}
		
		if (node.getIdentifier() != null) {
			this.identifier = node.getIdentifier().getValue();
		}
		
		if (node.getName() != null) {
			this.name = node.getName();
		}
		
		if (node.getDescription() != null) {
			this.description = node.getDescription();
		}
		
		if (node.getBaseURL() != null) {
			this.baseURL = node.getBaseURL();
		}
		
		if (node.getServices() != null) {
			Services services = node.getServices();
			List<Service> serviceList = services.getServiceList();
			this.services = new ArrayList<MNService>();
			
			for (Service s : serviceList) {
				MNService mnService = new MNService();
				mnService.copy(s);
				this.services.add(mnService);
			}
		}
		
		if (node.getSynchronization() != null) {
			Synchronization synch = node.getSynchronization();
			this.synchronization = new MNSynchronization();
			this.synchronization.copy(synch);	
		}
		
		if (node.getPing() != null) {
			Ping ping = node.getPing();
			this.ping = new MNPing();
			this.ping.copy(ping);	
		}
		
		if (node.getSubjectList() != null) {
			List<Subject> subjectList = node.getSubjectList();
			this.subject = new ArrayList<String>();

			for (Subject s : subjectList) {
				this.subject.add(s.getValue());
			}
		}

		if (node.getContactSubjectList() != null) {
			List<Subject> subjectList = node.getContactSubjectList();
			this.contactSubject = new ArrayList<String>();

			for (Subject s : subjectList) {
				this.contactSubject.add(s.getValue());
			}
		}
	}
}
