/**
 * 
 */
package org.irods.jargon.dataone.app;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.springframework.stereotype.Component;

/**
 * JAXRS application for dataone
 * 
 * @author mconway
 *
 */
@Component
@ApplicationPath("/dataone-irods/")
public class DataOneJaxrsApplication extends Application {

}
