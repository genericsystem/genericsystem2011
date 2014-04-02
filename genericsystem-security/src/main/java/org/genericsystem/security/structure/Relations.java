package org.genericsystem.security.structure;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.security.structure.Types.Roles;
import org.genericsystem.security.structure.Types.Users;

public class Relations {

	@SystemGeneric
	@Components({ Users.class, Roles.class })
	public static class UsersRolesRelation extends GenericImpl {

	}

}
