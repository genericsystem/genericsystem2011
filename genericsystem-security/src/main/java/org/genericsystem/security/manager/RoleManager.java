package org.genericsystem.security.manager;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.genericsystem.core.Cache;
import org.genericsystem.security.exception.ConnectionException;
import org.genericsystem.security.exception.PermissionException;
import org.genericsystem.security.structure.Types.Roles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
public class RoleManager {

	protected static Logger log = LoggerFactory.getLogger(RoleManager.class);

	@Inject
	private Cache cache;

	private Roles roles;

	@Inject
	private SecurityManager securityManager;

	@PostConstruct
	private void init() {
		roles = cache.find(Roles.class);
	}

	public void addRole(String role) throws ConnectionException, PermissionException {
		securityManager.checkSecurity();
		roles.addInstance(role);

	}

	public void removeRole(String role) throws ConnectionException, PermissionException {
		securityManager.checkSecurity();
		roles.getInstance(role).remove();
	}
}
