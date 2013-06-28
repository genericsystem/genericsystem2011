package org.genericsystem.cdi;

import org.genericsystem.core.Generic;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class AppliWebTest extends AbstractTest {

	public void testInstanceIsConcreteWithValue() {
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation("outsideColor", color);
		Generic audi = car.newInstance("audi");
		Generic red = color.newInstance("red");
		audi.setLink(carColor, "audiRed", red);
		assert audi.getTargets(carColor).contains(red) : audi.getTargets(carColor);
	}

}
