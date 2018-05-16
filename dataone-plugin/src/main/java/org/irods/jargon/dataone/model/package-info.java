/**
 * This package contains an abstraction model for objects that have been exposed to DataOne. The most obvious type
 * of object to expose to DataOne is the iRODS data object, which represents a file stored in iRODS. It is conceivable
 * that a member node implementation may want to expose a Collection, which represents a directory stored in iRODS,
 * instead. For maximum flexibility, this package provides the DataOneObject interface and one or more implementations
 * thereof. In cases where the default DataOneObject implementations aren't sufficient, a specific plugin implementation
 * may create a new implementation of this interface to suit its needs.
 *
 * @author Sarah Roberts - CyVerse
 */
package org.irods.jargon.dataone.model;
