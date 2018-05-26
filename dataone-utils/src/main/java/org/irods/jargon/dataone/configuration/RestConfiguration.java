package org.irods.jargon.dataone.configuration;

import org.irods.jargon.dataone.auth.endpoint.AlwaysAllowAuthChecker;
import org.irods.jargon.dataone.auth.endpoint.AuthChecker;
import org.irods.jargon.dataone.auth.endpoint.HttpHeaderAuthChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pojo containing configuration information
 *
 * @author Mike Conway - DICE (www.irods.org)
 *
 */
public class RestConfiguration {

	private String irodsHost = "";
	private int irodsPort = 1247;
	private String irodsZone = "";
	private String defaultStorageResource = "";
	private String realm = "dfc-dataone";
	private boolean smimeEncryptAdminFunctions = false;
	private String privateCertAbsPath = "";
	private String publicKeyAbsPath = "";
	private String irodsUserName;
	private String irodsUserPswd;
	private String pluginJarLocation;
	private String authType = "";
	private String memberNodeAuthType = "";

	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Optional URL for a web interface to access grid data (typically an
	 * idrop-web installation pointing to the same grid)
	 */
	private String webInterfaceURL = "";

	/**
	 * @return the irodsHost
	 */
	public String getIrodsHost() {
		return irodsHost;
	}

	/**
	 * @param irodsHost
	 *            the irodsHost to set
	 */
	public void setIrodsHost(final String irodsHost) {
		this.irodsHost = irodsHost;
	}

	/**
	 * @return the irodsPort
	 */
	public int getIrodsPort() {
		return irodsPort;
	}

	/**
	 * @param irodsPort
	 *            the irodsPort to set
	 */
	public void setIrodsPort(final int irodsPort) {
		this.irodsPort = irodsPort;
	}

	/**
	 * @return the irodsZone
	 */
	public String getIrodsZone() {
		return irodsZone;
	}

	/**
	 * @param irodsZone
	 *            the irodsZone to set
	 */
	public void setIrodsZone(final String irodsZone) {
		this.irodsZone = irodsZone;
	}

	/**
	 * @return the defaultStorageResource
	 */
	public String getDefaultStorageResource() {
		return defaultStorageResource;
	}

	/**
	 * @param defaultStorageResource
	 *            the defaultStorageResource to set
	 */
	public void setDefaultStorageResource(final String defaultStorageResource) {
		this.defaultStorageResource = defaultStorageResource;
	}

	/**
	 * @return the realm
	 */
	public String getRealm() {
		return realm;
	}

	/**
	 * @param realm
	 *            the realm to set
	 */
	public void setRealm(final String realm) {
		this.realm = realm;
	}

	/**
	 * @return the smimeEncryptAdminFunctions
	 */
	public boolean isSmimeEncryptAdminFunctions() {
		return smimeEncryptAdminFunctions;
	}

	/**
	 * @param smimeEncryptAdminFunctions
	 *            the smimeEncryptAdminFunctions to set
	 */
	public void setSmimeEncryptAdminFunctions(final boolean smimeEncryptAdminFunctions) {
		this.smimeEncryptAdminFunctions = smimeEncryptAdminFunctions;
	}

	/**
	 * @return the privateCertAbsPath
	 */
	public String getPrivateCertAbsPath() {
		return privateCertAbsPath;
	}

	/**
	 * @param privateCertAbsPath
	 *            the privateCertAbsPath to set
	 */
	public void setPrivateCertAbsPath(final String privateCertAbsPath) {
		this.privateCertAbsPath = privateCertAbsPath;
	}

	/**
	 * @return the publicKeyAbsPath
	 */
	public String getPublicKeyAbsPath() {
		return publicKeyAbsPath;
	}

	/**
	 * @param publicKeyAbsPath
	 *            the publicKeyAbsPath to set
	 */
	public void setPublicKeyAbsPath(final String publicKeyAbsPath) {
		this.publicKeyAbsPath = publicKeyAbsPath;
	}

	public String getWebInterfaceURL() {
		return webInterfaceURL;
	}

	public void setWebInterfaceURL(final String webInterfaceURL) {
		this.webInterfaceURL = webInterfaceURL;
	}

	public String getIrodsUserName() {
		return irodsUserName;
	}

	public void setIrodsUserName(final String irodsUserName) {
		this.irodsUserName = irodsUserName;
	}

	public String getIrodsUserPswd() {
		return irodsUserPswd;
	}

	public void setIrodsUserPswd(final String irodsUserPswd) {
		this.irodsUserPswd = irodsUserPswd;
	}

	/**
	 * @return the pluginJarLocation
	 */
	public String getPluginJarLocation() {
		return pluginJarLocation;
	}

	/**
	 * @param pluginJarLocation
	 *            the pluginJarLocation to set
	 */
	public void setPluginJarLocation(String pluginJarLocation) {
		this.pluginJarLocation = pluginJarLocation;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RestConfiguration [");
		if (irodsHost != null) {
			builder.append("irodsHost=").append(irodsHost).append(", ");
		}
		builder.append("irodsPort=").append(irodsPort).append(", ");
		if (irodsZone != null) {
			builder.append("irodsZone=").append(irodsZone).append(", ");
		}
		if (defaultStorageResource != null) {
			builder.append("defaultStorageResource=").append(defaultStorageResource).append(", ");
		}
		if (realm != null) {
			builder.append("realm=").append(realm).append(", ");
		}
		builder.append("smimeEncryptAdminFunctions=").append(smimeEncryptAdminFunctions).append(", ");
		if (privateCertAbsPath != null) {
			builder.append("privateCertAbsPath=").append(privateCertAbsPath).append(", ");
		}
		if (publicKeyAbsPath != null) {
			builder.append("publicKeyAbsPath=").append(publicKeyAbsPath).append(", ");
		}
		if (irodsUserName != null) {
			builder.append("irodsUserName=").append(irodsUserName).append(", ");
		}
		if (irodsUserPswd != null) {
			builder.append("irodsUserPswd=").append(irodsUserPswd).append(", ");
		}
		if (pluginJarLocation != null) {
			builder.append("pluginJarLocation=").append(pluginJarLocation).append(", ");
		}
		if (webInterfaceURL != null) {
			builder.append("webInterfaceURL=").append(webInterfaceURL);
		}
		builder.append("]");
		return builder.toString();
	}

	/**
	 * @return the authType
	 */
	public String getAuthType() {
		return authType;
	}

	/**
	 * @param authType
	 *            the authType to set
	 */
	public void setAuthType(String authType) {
		this.authType = authType;
	}

	/**
	 * Gets the authorization type used by the member node service, which determines how the member
	 * node service checks for authorization in endpoints that require it. The currently available
	 * types are {@code http-header} and {@code always-allow}.
	 *
	 * @return the authorization type.
	 */
	public String getMemberNodeAuthType() {
		return memberNodeAuthType;
	}

	/**
	 * Sets the authorization type used by the member node service, which determines how the member
	 * node service check for authorization in endpoints that require it. The currently available
	 * types are {@code http-header} and {@code always-allow}.
	 *
	 * @param memberNodeAuthType the authorization type.
	 */
	public void setMemberNodeAuthType(String memberNodeAuthType) {
		this.memberNodeAuthType = memberNodeAuthType;
	}

	/**
	 * Obtains an authorization checker to use for the member node service based on the value of the
	 * value of {@code memberNodeAuthType}. If the value is equal to "http-header" (case insensitive),
	 * then an instance of {@link HttpHeaderAuthChecker} is returned. Otherwise, and instance of
	 * {@link AlwaysAllowAuthChecker} is returned.
	 *
	 * @return an instance of {@link AuthChecker}.
	 */
	public AuthChecker getAuthChecker() {
		String authType = getMemberNodeAuthType();
		logger.error("getting the auth checker for auth type: {}", authType);
		if ("http-header".equalsIgnoreCase(authType)) {
			return new HttpHeaderAuthChecker();
		} else {
			return new AlwaysAllowAuthChecker();
		}
	}
}
