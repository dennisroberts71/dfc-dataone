/**
 * 
 */
package org.irods.jargon.dataone.def.event.persist.dao.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * 
 * @author mcc
 *
 */
@Entity
@Table(name = "access_log")
public class AccessLog {

	@Id
	@SequenceGenerator(name = "access_log_id_seq", sequenceName = "access_log_id_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "access_log_id_seq")
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@Column(name = "file_key", length = 1024, nullable = false)
	private String fileKey;

	/**
	 * 
	 */
	public AccessLog() {
	}

}
