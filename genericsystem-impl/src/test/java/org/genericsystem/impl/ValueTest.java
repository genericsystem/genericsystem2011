package org.genericsystem.impl;

import java.io.Serializable;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Engine;
import org.genericsystem.core.GenericSystem;
import org.testng.annotations.Test;

@Test
public class ValueTest extends AbstractTest {

	public void testValueOfEngine() {
		Engine engine = GenericSystem.newCacheOnANewInMemoryEngine().getEngine();
		assert engine.getValue() != null;
	}

	public void testValueOfType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Serializable value = cache.getEngine().addSubType( "Car").getValue();
		assert value != null;
		assert value.equals("Car");
	}

	// public void testValueWithSubAttribute() {
	// Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
	// Type vehicle = cache.newType("Vehicle");
	// Type car = vehicle.newSubType("Car");
	// Attribute vehiclePower = vehicle.addAttribute("power");
	// Attribute carUltraPower = car.addSubAttribute(vehiclePower, "ultraPower");
	// vehicle.setValue(vehiclePower, 0);
	// Generic myCar = car.newInstance("myCar");
	// Holder value = myCar.setValue(carUltraPower, 123);
	// assert myCar.getValueHolders(carUltraPower).contains(value);
	// assert myCar.getValueHolders(vehiclePower).contains(value);
	// }

}
