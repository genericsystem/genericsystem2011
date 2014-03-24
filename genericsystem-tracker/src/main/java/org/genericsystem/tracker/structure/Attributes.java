package org.genericsystem.tracker.structure;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.tracker.annotation.DateFormat;
import org.genericsystem.tracker.structure.Types.Issues;

public class Attributes {

	@SystemGeneric
	@Components(Issues.class)
	@SingularConstraint
	@DateFormat("dd/mm/yyyy")
	public static class Created extends GenericImpl {

	}

	@SystemGeneric
	@Components(Issues.class)
	@SingularConstraint
	public static class Description extends GenericImpl {

	}

}
