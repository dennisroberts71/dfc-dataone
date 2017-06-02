/**
 * 
 */
package org.irods.jargon.dataone.def.event.persist.dao;

import org.irods.jargon.dataone.def.event.persist.dao.domain.AccessLog;
import org.irods.jargon.dataone.events.EventLoggingException;

/**
 * DAO interface for Access log
 * 
 * @author Mike Conway
 * 
 */
public interface AccessLogDAO {

	/**
	 * Save the <code>AccessLog</code> entry in the audit database
	 * 
	 * @param accessLog
	 *            {@link AccessLog} entry containing the event
	 * @throws TransferDAOException
	 */
	void save(AccessLog accessLog) throws EventLoggingException;

	/**
	 * locate the access log entry by ID
	 * 
	 * @param id
	 *            <code>Long</code> with the access id
	 * @return {@link AccessLog} or <code>null</code>
	 * @throws EventLoggingException
	 */
	AccessLog findById(Long id) throws EventLoggingException;

	/**
	 * Delete the given entry
	 * 
	 * @param accessLog
	 *            {@link AccessLog} to delete
	 * @throws EventLoggingException
	 */
	void delete(AccessLog accessLog) throws EventLoggingException;

}
