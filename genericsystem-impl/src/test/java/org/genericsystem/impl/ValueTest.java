package org.genericsystem.impl;

import java.io.Serializable;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Type;
import org.genericsystem.api.generic.Value;
import org.testng.annotations.Test;

@Test
public class ValueTest extends AbstractTest {

	public void testValueOfEngine() {
		Engine engine = GenericSystem.newCacheOnANewInMemoryEngine().getEngine();
		assert engine.getValue() != null;
	}

	public void testValueOfType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Serializable value = cache.getEngine().newSubType(cache,"Car").getValue();
		assert value != null;
		assert value.equals("Car");
	}

	public void testValueWithSubAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache,"Car");
		Attribute vehiclePower = vehicle.addAttribute(cache,"power");
		Attribute carUltraPower = car.addSubAttribute(cache,vehiclePower, "ultraPower");
		vehicle.addValue(cache,vehiclePower, 0);
		Generic myCar = car.newInstance(cache,"myCar");
		Value value = myCar.addValue(cache,carUltraPower, 123);
		assert myCar.getValueHolders(cache,carUltraPower).contains(value);
		assert myCar.getValueHolders(cache,vehiclePower).contains(value);
	}

}
