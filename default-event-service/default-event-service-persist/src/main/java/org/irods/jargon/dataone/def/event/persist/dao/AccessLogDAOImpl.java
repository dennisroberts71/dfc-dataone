/**
 * 
 */
package org.irods.jargon.dataone.def.event.persist.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.irods.jargon.dataone.def.event.persist.dao.domain.AccessLog;
import org.irods.jargon.dataone.events.EventLoggingException;
import org.irods.jargon.dataone.events.EventsEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Impl of an access log
 * 
 * @author mcc
 *
 */
@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)

public class AccessLogDAOImpl implements AccessLogDAO {

	private static final Logger log = LoggerFactory.getLogger(AccessLogDAOImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

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
			sessionFactory.getCurrentSession().saveOrUpdate(accessLog);
		} catch (Exception e) {
			log.error("error in save(AccessLog)", e);
			throw new EventLoggingException("Failed save(accessLog)", e);
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

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AccessLog.class);
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
		log.info("entering delete(AccessLog)");

		try {
			sessionFactory.getCurrentSession().delete(accessLog);
		} catch (Exception e) {
			log.error("error in delete(AccessLog)", e);
			throw new EventLoggingException("Failed delete(AccessLog)", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AccessLog> find(Date start, Date end, EventsEnum eventType, String pid, int offset, int limit)
			throws EventLoggingException {
		log.info("find start:{}, end:{}, eventType:{}, pid:{}, offset:{}, limit:{}", start, end, eventType, pid, offset,
				limit);

		List<AccessLog> results;
		try {
			Criteria crit = sessionFactory.getCurrentSession().createCriteria(AccessLog.class);
			if (start != null) {
				crit.add(Restrictions.ge("dateAdded", start));
			}
			if (end != null) {
				crit.add(Restrictions.le("dateAdded", end));
			}
			if (eventType != null) {
				crit.add(Restrictions.eq("event", eventType));
			}
			if (pid != null) {
				crit.add(Restrictions.eq("permanentId", pid));
			}
			crit.setFirstResult(offset);
			crit.setMaxResults(limit);
			results = crit.list();
		} catch (Exception e) {
			log.error("error in find(start, end, eventType, offset, limit)", e);
			throw new EventLoggingException("failed find(start, end, eventType, offset, limit)", e);
		}
		return results;
	}

	@Override
	public int count(Date start, Date end, EventsEnum eventType, String pid)
			throws EventLoggingException {
		log.info("count start:{}, end:{}, eventType:{}, pid:{}", start, end, eventType, pid);

		int result = 0;
		try {
			CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
			CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
			criteria.select(builder.count(criteria.from(AccessLog.class)));
			Root<AccessLog> root = criteria.from(AccessLog.class);
			if (start != null) {
				criteria.where()
			}
		} catch (Exception e) {
			log.error("error in count(start, end, eventType, pid", e);
			throw new EventLoggingException("failed count(start, end, eventType, pid)", e);
		}
		return result;
	}

	/**
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * @param sessionFactory
	 *            the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		log.info("sessionFactory being set:{}", sessionFactory);
		this.sessionFactory = sessionFactory;
	}

}
