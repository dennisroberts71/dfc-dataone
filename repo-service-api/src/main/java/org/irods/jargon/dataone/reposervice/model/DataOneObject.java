package org.irods.jargon.dataone.reposervice.model;

import org.dataone.service.types.v1.DescribeResponse;
import org.irods.jargon.core.exception.JargonException;

import java.io.InputStream;

/**
 * @author Dennis Roberts, CyVerse
 */
public interface DataOneObject {

    /**
     * Returns the name of the object.
     *
     * @return the name of the object.
     * @throws JargonException
     */
    String getName() throws JargonException;

    /**
     * Returns the size of the object in bytes.
     *
     * @return the size of the object.
     * @throws JargonException
     */
    long getSize() throws JargonException;

    /**
     * Returns a description of an object in the DataOne member node repository.
     *
     * @return A description of the object.
     * @throws JargonException
     */
    DescribeResponse describe() throws JargonException;

    /**
     * Gets an input stream that can be used to fetch the contents of the object.
     *
     * @return The input stream.
     * @throws JargonException
     */
    InputStream getInputStream() throws JargonException;
}
