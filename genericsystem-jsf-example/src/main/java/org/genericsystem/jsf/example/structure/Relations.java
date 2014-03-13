package org.genericsystem.jsf.example.structure;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.jsf.example.structure.Types.Cars;
import org.genericsystem.jsf.example.structure.Types.Colors;

public class Relations {

	@SystemGeneric
	@Components({ Cars.class, Colors.class })
	@SingularConstraint
	public static class CarColorRelation extends GenericImpl {

	}

}
