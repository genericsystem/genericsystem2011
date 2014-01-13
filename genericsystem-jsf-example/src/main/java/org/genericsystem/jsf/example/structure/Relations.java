package org.genericsystem.jsf.example.structure;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.jsf.example.structure.Types.Cars;
import org.genericsystem.jsf.example.structure.Types.Colors;

public class Relations {

	@SystemGeneric
	@Components({ Cars.class, Colors.class })
	public static class CarColorRelation {

	}
}
