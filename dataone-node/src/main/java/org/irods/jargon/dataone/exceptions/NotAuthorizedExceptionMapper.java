package org.irods.jargon.dataone.exceptions;

import org.dataone.service.exceptions.NotAuthorized;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * @author Sarah Roberts - CyVerse
 */
public class NotAuthorizedExceptionMapper implements ExceptionMapper<NotAuthorized> {
    @Override
    public Response toResponse(NotAuthorized notAuthorized) {
        return Response
                .status(Response.Status.UNAUTHORIZED)
                .header("Vary", "Accept-Encoding")
                .entity(notAuthorized.serialize(notAuthorized.FMT_XML))
                .type(MediaType.TEXT_XML)
                .build();
    }
}
