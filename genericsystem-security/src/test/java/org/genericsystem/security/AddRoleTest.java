package org.genericsystem.security;

import javax.inject.Inject;

import org.genericsystem.security.manager.RoleManager;
import org.genericsystem.security.manager.SecurityManager;
import org.genericsystem.security.manager.UserManager;
import org.testng.annotations.Test;

@Test
public class AddRoleTest extends AbstractTest {

	@Inject
	SecurityManager securityManager;

	@Inject
	UserManager userManager;

	@Inject
	RoleManager roleManager;

	public void testAddRole() {
		securityManager.connect("admin", "middleware");
		roleManager.addRole("developers");
	}
}
