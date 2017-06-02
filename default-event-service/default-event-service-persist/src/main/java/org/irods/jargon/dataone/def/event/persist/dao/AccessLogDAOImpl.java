/**
 * 
 */
package org.irods.jargon.dataone.def.event.persist.dao;

import org.hibernate.Criteria;
import org.irods.jargon.dataone.def.event.persist.dao.domain.AccessLog;
import org.irods.jargon.dataone.events.EventLoggingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Impl of an access log
 * 
 * @author mcc
 *
 */
@SuppressWarnings("deprecation")
public class AccessLogDAOImpl extends HibernateDaoSupport implements AccessLogDAO {

	private static final Logger log = LoggerFactory.getLogger(AccessLogDAOImpl.class);

	/**
	 * 
	 */
	public AccessLogDAOImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.irods.jargon.dataone.def.event.persist.dao.AccessLogDAO#save(org.
	 * irods.jargon.dataone.def.event.persist.dao.domain.AccessLog)
	 */
	@Override
	public void save(AccessLog accessLog) throws EventLoggingException {

		log.info("save accessLog:{}", accessLog);

		try {
			getSessionFactory().getCurrentSession().saveOrUpdate(accessLog);
		} catch (Exception e) {
			log.error("error in save(AccessLog)", e);
			throw new EventLoggingException("Failed save(TransferItem)", e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.irods.jargon.dataone.def.event.persist.dao.AccessLogDAO#findById(java
	 * .lang.Long)
	 */
	@Override
	public AccessLog findById(Long id) throws EventLoggingException {

		log.info("find accessLog:{}", id);

		Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(AccessLog.class);
		return (AccessLog) criteria.uniqueResult();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.irods.jargon.dataone.def.event.persist.dao.AccessLogDAO#delete(org.
	 * irods.jargon.dataone.def.event.persist.dao.domain.AccessLog)
	 */
	@Override
	public void delete(AccessLog accessLog) throws EventLoggingException {
		logger.info("entering delete(AccessLog)");

		try {
			getSessionFactory().getCurrentSession().delete(accessLog);
		} catch (Exception e) {
			log.error("error in delete(AccessLog)", e);
			throw new EventLoggingException("Failed delete(AccessLog)", e);
		}
	}

}
