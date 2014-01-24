package org.genericsystem.jsf.example.structure;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.jsf.example.structure.Types.Cars;

public class Attributes {

	@SystemGeneric
	@Components(Cars.class)
	public static class Power {
	}

	@SystemGeneric
	@Components(Cars.class)
	public static class Power2 {
	}

}
