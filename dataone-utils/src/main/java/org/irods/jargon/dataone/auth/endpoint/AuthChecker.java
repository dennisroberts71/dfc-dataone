package org.irods.jargon.dataone.auth.endpoint;

import org.dataone.service.exceptions.NotAuthorized;

import javax.servlet.http.HttpServletRequest;

/**
 * This interface determines whether or not a request to an endpoint is authorized to proceed.
 *
 * @author Sarah Roberts - CyVerse
 */
public interface AuthChecker {

    /**
     * Checks the authorization in an incoming HTTP request.
     *
     * @param request the request to check.
     * @throws NotAuthorized if the authorization isn't present.
     */
    void checkAuthorization(HttpServletRequest request) throws NotAuthorized;
}
