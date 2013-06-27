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
		Relation carColor = car.setRelation(cache, "outsideColor", color);
		Generic audi = car.newInstance(cache, "audi");
		Generic red = color.newInstance(cache, "red");
		audi.setLink(cache, carColor, "audiRed", red);
		assert audi.getTargets(cache, carColor).contains(red) : audi.getTargets(cache, carColor);
	}

}
