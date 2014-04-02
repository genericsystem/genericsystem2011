package org.genericsystem.security;

import javax.inject.Inject;

import org.genericsystem.core.Cache;
import org.genericsystem.exception.RollbackException;
import org.genericsystem.security.exception.AuthentificationException;
import org.genericsystem.security.exception.ConnectionException;
import org.genericsystem.security.exception.PermissionException;
import org.genericsystem.security.manager.SecurityManager;
import org.genericsystem.security.manager.UserManager;
import org.genericsystem.security.structure.Types.Users;
import org.testng.annotations.Test;

@Test
public class AddUserTest extends AbstractTest {

	@Inject
	Cache cache;

	@Inject
	SecurityManager securityManager;

	@Inject
	UserManager userManager;

	public void testAddUserWithAdmin() {
		try {
			Users users = cache.find(Users.class);
			securityManager.connect("admin", "middleware");
			userManager.addUser("Ahmed", "ajerbi", "middleware", "Admin");
			assert users.getInstance("Ahmed") != null;
		} catch (AuthentificationException e) {
			assert false;
		}
	}

	public void testAddUserExist() {
		securityManager.connect("admin", "middleware");
		try {
			userManager.addUser("Michael", "mory", "middlewest", null);
			assert false;
		} catch (RollbackException ignore) {

		}
	}

	public void testAddUserAdminNotConnected() {
		try {
			userManager.addUser("Charles", "cjobi", "middleware", null);
			assert false;
		} catch (ConnectionException ignore) {
			log.warn(ignore.toString());
		}
	}

	public void testAddUserAdminDeconnected() {
		try {
			securityManager.connect("admin", "middleware");
			securityManager.disconnect();
			userManager.addUser("Nicolas", "cilrp", "middleware", null);
			assert false;
		} catch (ConnectionException ignore) {
			log.warn(ignore.toString());
		}
	}

	public void testInsufficientPermission() {
		try {
			securityManager.connect("mory", "middlewest");
			userManager.addUser("xyz", "xyz", "xyz", null);
			assert false;
		} catch (PermissionException ignore) {
			log.warn(ignore.toString());

		}
	}

	public void testRemoveUserNotAdmin() {
		try {
			securityManager.connect("mory", "middlewest");
			userManager.removeUser("Ahmed");
			assert false;
		} catch (PermissionException ignore) {
			log.warn(ignore.toString());

		}
	}

	public void testRemoveUser() {
		try {
			Users users = cache.find(Users.class);
			securityManager.connect("admin", "middleware");
			userManager.removeUser("Ahmed");
			assert users.getInstance("Ahmed") == null;
		} catch (RollbackException e) {
			assert false;
		}
	}

	public void testRemoveUserNotExist() {
		try {
			securityManager.connect("admin", "middleware");
			userManager.removeUser("R8SP");
			assert false;
		} catch (RollbackException e) {
			log.warn(e.getMessage());
		}
	}

}
