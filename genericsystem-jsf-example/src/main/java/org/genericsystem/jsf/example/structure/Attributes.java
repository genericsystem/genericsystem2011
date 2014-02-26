package org.genericsystem.jsf.example.structure;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.jsf.example.structure.Types.Cars;

public class Attributes {

	@SystemGeneric
	@Components(Cars.class)
	public static class Power extends GenericImpl {

	}

	// @SystemGeneric
	// @Components(Cars.class)
	// public static class Price extends GenericImpl {
	// }
	//
	// @SystemGeneric
	// @Components(Cars.class)
	// public static class FuelTank extends GenericImpl {
	// }

}
