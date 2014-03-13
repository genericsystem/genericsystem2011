package org.genericsystem.jsf.example.structure;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.jsf.example.structure.Types.Issues;

public class Attributes {

	@SystemGeneric
	@Components(Issues.class)
	@SingularConstraint
	public static class Created extends GenericImpl {

	}

}
