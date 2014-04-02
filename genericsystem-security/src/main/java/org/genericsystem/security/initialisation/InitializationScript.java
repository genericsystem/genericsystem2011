package org.genericsystem.security.initialisation;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.genericsystem.cdi.event.EventLauncher.AfterGenericSystemStarts;
import org.genericsystem.core.Cache;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.genericsystem.security.structure.Attributes.Login;
import org.genericsystem.security.structure.Attributes.Password;
import org.genericsystem.security.structure.Instances.RoleAdmin;
import org.genericsystem.security.structure.Relations.UsersRolesRelation;
import org.genericsystem.security.structure.Types.Roles;
import org.genericsystem.security.structure.Types.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@RequestScoped
public class InitializationScript {

	protected static Logger log = LoggerFactory.getLogger(InitializationScript.class);

	@Inject
	private Engine engine;

	public void init(@Observes AfterGenericSystemStarts event) {

		Cache cache = engine.newCache().start();

		/**
		 * User Admin
		 */
		Type users = cache.find(Users.class);
		Type roles = cache.find(Roles.class);
		Relation userRoleRelation = cache.find(UsersRolesRelation.class);
		Attribute password = cache.find(Password.class);
		Attribute login = cache.find(Login.class);

		Generic userAdmin = users.setInstance("Admin");
		Generic roleAdmin = cache.find(RoleAdmin.class);
		userAdmin.setLink(userRoleRelation, "userRoleRelation", roleAdmin);
		userAdmin.setValue(login, "admin");
		userAdmin.setValue(password, "b200f0642a4dc4d9d66162920860c3f0");// middleware

		/**
		 * User developer
		 */
		Generic user = users.setInstance("Michael");
		user.setValue(login, "mory");
		user.setValue(password, "7661bfdb88890e9a28369c7d794a1670");// middlewest
		Generic roleDeveloper = roles.setInstance("Developer");
		user.setLink(userRoleRelation, "userRoleRelation1", roleDeveloper);

		cache.flush();
	}
}
