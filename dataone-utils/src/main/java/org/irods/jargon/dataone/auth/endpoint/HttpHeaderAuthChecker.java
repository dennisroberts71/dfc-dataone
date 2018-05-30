package org.irods.jargon.dataone.auth.endpoint;

import org.dataone.service.exceptions.NotAuthorized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * An authorization checker that looks for an HTTP header to determine whether or not the request is
 * authenticated. Member node services that use this class should reside behind a reverse proxy that
 * handles the authorization and sets the {@code X-Authorized} header in the request. If the
 * authorization succeeds the header value should be {@code true}, otherwise it should be {@code false}.
 *
 * @author Sarah Roberts - CyVerse
 */
public class HttpHeaderAuthChecker implements AuthChecker {
    private static String HEADER_NAME = "X-Authorized";
    private static String AUTHORIZED_VALUE = "success";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void checkAuthorization(HttpServletRequest request) throws NotAuthorized {
        String value = request.getHeader(HEADER_NAME);
        logger.debug("checking authentication in request. {}: {}", HEADER_NAME, value);
        if (value == null || !value.equalsIgnoreCase(AUTHORIZED_VALUE)) {
            throw new NotAuthorized("100001.001", "not authorized");
        }
    }
}
