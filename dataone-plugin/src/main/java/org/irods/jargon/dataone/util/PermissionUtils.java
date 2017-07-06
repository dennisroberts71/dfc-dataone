package org.irods.jargon.dataone.util;

import org.dataone.service.types.v1.Permission;
import org.irods.jargon.core.protovalues.FilePermissionEnum;
import org.irods.jargon.core.pub.domain.UserFilePermission;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dennis Roberts - CyVerse
 */
public class PermissionUtils {

	// Prevent instantiation.
	private PermissionUtils() {
	}

	/**
	 * Convert an iRODS permission to a set of DataOne permissions.
	 *
	 * @param irodsPermission the iRODS permission.
	 * @return the corresponding DataOne permission.
	 */
	public static List<Permission> getDataOnePermission(final UserFilePermission irodsPermission) {
		List<Permission> permissions = new ArrayList<>();
		FilePermissionEnum fpEnum = irodsPermission.getFilePermissionEnum();
		switch (fpEnum) {
			case READ:
				permissions.add(MNPermissionEnum.valueForDataOne(fpEnum));
				break;

			case WRITE:
				permissions.add(Permission.READ);
				permissions.add(MNPermissionEnum.valueForDataOne(fpEnum));
				break;

			case OWN:
				permissions.add(Permission.READ);
				permissions.add(Permission.WRITE);
				permissions.add(MNPermissionEnum.valueForDataOne(fpEnum));
				break;
			default:
				permissions.add(MNPermissionEnum.valueForDataOne(fpEnum));
				break;
		}
		return permissions;
	}
}
