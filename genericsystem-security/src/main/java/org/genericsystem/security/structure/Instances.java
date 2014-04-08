package org.genericsystem.security.structure;

import org.genericsystem.annotation.Meta;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.value.StringValue;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.security.structure.Types.Roles;

public class Instances extends GenericImpl {

	@SystemGeneric
	@Meta(Roles.class)
	@StringValue("Admin")
	public static class RoleAdmin extends GenericImpl {

	}

}
