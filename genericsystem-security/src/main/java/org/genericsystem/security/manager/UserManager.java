package org.genericsystem.security.manager;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.security.exception.ConnectionException;
import org.genericsystem.security.exception.PermissionException;
import org.genericsystem.security.hachage.MD5Hashing;
import org.genericsystem.security.structure.Attributes.Login;
import org.genericsystem.security.structure.Attributes.Password;
import org.genericsystem.security.structure.Relations.UsersRolesRelation;
import org.genericsystem.security.structure.Types.Roles;
import org.genericsystem.security.structure.Types.Users;

@RequestScoped
public class UserManager {

	@Inject
	private Cache cache;

	private Users users;

	private Roles roles;

	private Relation usersRolesRelation;

	@Inject
	SecurityManager securityManager;

	@PostConstruct()
	private void init() {
		users = cache.find(Users.class);
		roles = cache.find(Roles.class);
		usersRolesRelation = cache.find(UsersRolesRelation.class);
	}

	public void addUser(String name, String login, String password, String role) throws PermissionException, ConnectionException {
		securityManager.checkSecurity();
		Generic newUser = users.addInstance(name);
		newUser.setValue(cache.<Attribute> find(Login.class), login);
		newUser.setValue(cache.<Attribute> find(Password.class), MD5Hashing.hashPassword(password));
		if (roles.getInstance(role) != null)
			newUser.setLink(usersRolesRelation, usersRolesRelation.toString(), roles.getInstance(role));
	}

	public void removeUser(String name) throws ConnectionException, PermissionException {
		securityManager.checkSecurity();
		if (users.getInstance(name) != null)
			users.getInstance(name).remove();
		else
			throw new org.genericsystem.exception.RollbackException("user name :" + name + " doesn't exist");
	}
}
