package org.genericsystem.impl;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Type;
import org.genericsystem.api.generic.Value;
import org.genericsystem.impl.core.GenericImpl;
import org.testng.annotations.Test;

@Test
public class RebindTest {

	public void testRebindDependenciesOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Attribute carPower = car.addAttribute(cache, "Power");
		Attribute vehiclePower = vehicle.addAttribute(cache, "Power");
		assert !carPower.isAlive(cache);
		assert ((GenericImpl) carPower).reBind(cache).inheritsFrom(vehiclePower);
	}

	public void testRebindDependenciesKO() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Attribute carPower = car.addAttribute(cache, "Power");
		Attribute carPowerUnit = carPower.addAttribute(cache, "Unit");
		Generic myCar = car.newInstance(cache, "Audi");
		Value vPower = myCar.addValue(cache, carPower, "200");
		Value vUnit = vPower.addValue(cache, carPowerUnit, "HorsePower");
		assert vPower.getValues(cache, carPowerUnit).contains(vUnit);
		assert myCar.getValues(cache, carPower).contains(vPower);
		Attribute vehiclePower = vehicle.addAttribute(cache, "Power");
		assert !carPower.isAlive(cache);
		assert ((GenericImpl) carPower).reBind(cache).inheritsFrom(vehiclePower);
	}
}
