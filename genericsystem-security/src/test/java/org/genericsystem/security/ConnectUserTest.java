package org.genericsystem.security;

import javax.inject.Inject;

import org.genericsystem.core.Cache;
import org.genericsystem.security.exception.AuthentificationException;
import org.genericsystem.security.manager.SecurityManager;
import org.testng.annotations.Test;

@Test
public class ConnectUserTest extends AbstractTest {

	@Inject
	Cache cache;

	@Inject
	SecurityManager securityManager;

	public void testLoginOKPassOk() {
		try {
			securityManager.connect("admin", "middleware");
			assert securityManager.isConnected() == true;
			assert securityManager.isAdmin() == true;
		} catch (AuthentificationException e) {
			assert false;
		}
	}

	public void testLoginKOPassOk() {
		try {
			securityManager.connect("asas", "middleware");
			assert false;
		} catch (AuthentificationException ignore) {

		}
	}

	public void testLoginOKPasskO() {
		try {
			securityManager.connect("admin", "middlewares1");
			assert false;
		} catch (AuthentificationException e) {
			log.warn(e.toString());
		}
	}

	public void testLoginKOPasskO() {
		try {
			securityManager.connect("admins", "middlewares1");
			assert false;
		} catch (AuthentificationException e) {
			log.warn(e.toString());
		}
	}

	public void testDeconnectUser() {
		try {
			securityManager.connect("admin", "middleware");
			assert securityManager.isConnected() == true;
			securityManager.disconnect();
			assert securityManager.isConnected() == false;
		} catch (AuthentificationException e) {
			assert false;
		}
	}
}
