package org.genericsystem.security.manager;

import java.io.Serializable;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.genericsystem.security.exception.AuthentificationException;
import org.genericsystem.security.exception.ConnectionException;
import org.genericsystem.security.exception.PermissionException;
import org.genericsystem.security.hachage.MD5Hashing;
import org.genericsystem.security.structure.Attributes.Login;
import org.genericsystem.security.structure.Attributes.Password;
import org.genericsystem.security.structure.Relations.UsersRolesRelation;
import org.genericsystem.security.structure.Types.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SessionScoped
public class SecurityManager implements Serializable {

	private static final long serialVersionUID = -6924645112350109140L;

	protected static Logger log = LoggerFactory.getLogger(SecurityManager.class);

	@Inject
	private Cache cache;

	private Users users;

	private Generic currentUser;

	private static final String ADMIN = "Admin";

	@PostConstruct
	private void init() {
		users = cache.find(Users.class);
	}

	public void connect(String login, String password) throws AuthentificationException {
		Attribute loginAtt = cache.find(Login.class);
		Attribute passwordAtt = cache.find(Password.class);
		for (Generic user : users.getAllInstances()) {
			if (Objects.equals(user.getValue(loginAtt), login) && Objects.equals(user.getValue(passwordAtt), MD5Hashing.hashPassword(password))) {
				currentUser = user;
				return;
			}
		}
		throw new AuthentificationException("User login/password :" + login + "/" + password + " doesn't exist");
	}

	public void disconnect() {
		currentUser = null;
	}

	public boolean isConnected() {
		return currentUser != null;
	}

	public boolean isAdmin() {
		if (!isConnected())
			return false;
		Relation userRoleRelation = cache.find(UsersRolesRelation.class);
		return currentUser.<Type> getTargets(userRoleRelation).get(0).toString().equals(ADMIN);
	}

	public void checkSecurity() throws ConnectionException, PermissionException {
		if (!isConnected())
			throw new ConnectionException("user is not connected");
		if (!isAdmin())
			throw new PermissionException("Insufficient permissions");
	}
}
