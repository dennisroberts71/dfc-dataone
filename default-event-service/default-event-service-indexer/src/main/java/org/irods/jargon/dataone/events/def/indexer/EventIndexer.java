/**
 * 
 */
package org.irods.jargon.dataone.events.def.indexer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ImportResource;

/**
 * Master flow core
 * 
 * @author mcc
 *
 */

@SpringBootApplication
@ImportResource({ "/integration.xml", "/event-dao-hibernate-spring.cfg.xml", "/event-service-beans.xml" })
public class EventIndexer {

	/**
	 * 
	 */
	public EventIndexer() {
	}

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext ctx = new SpringApplication(EventIndexer.class).run(args);
		System.out.println("Hit Enter to terminate");
		System.in.read();
		ctx.close();
	}

}
