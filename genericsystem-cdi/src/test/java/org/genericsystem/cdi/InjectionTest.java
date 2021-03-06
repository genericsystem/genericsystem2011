package org.genericsystem.cdi;

import org.genericsystem.core.Generic;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class InjectionTest extends AbstractTest {

	public void testInstanceIsConcreteWithValue() {
		cache.start();
		Type car = cache.addType("Car");
		Type color = cache.addType("Color");
		Relation carColor = car.setRelation("outsideColor", color);
		Generic audi = car.addInstance("audi");
		Generic red = color.addInstance("red");
		audi.setLink(carColor, "audiRed", red);
		assert audi.getTargets(carColor).contains(red) : audi.getTargets(carColor);
	}

	// @Inject
	// @InjectClass(Vehicle.class)
	// private Generic vehicle;
	//
	// public void testAnnotType() {
	// assert cache.getGeneric("Vehicle", cache.getEngine()) != null;
	// }
	//
	// @SystemGeneric
	// @StringValue("Vehicle")
	// public static class Vehicle extends GenericImpl {
	//
	// }

}
