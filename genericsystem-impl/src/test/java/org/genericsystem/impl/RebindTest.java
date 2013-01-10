package org.genericsystem.impl;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Relation;
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
		assert ((GenericImpl) carPower).reBind(cache).inheritsFrom(vehiclePower);
	}

	public void testRelationRebind() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type metal = cache.newType("metal");
		Type preciousMetal = metal.newSubType(cache, "preciousMetal");
		Generic zink = metal.newInstance(cache, "zink");
		Generic iron = preciousMetal.newInstance(cache, "iron");
		Type color = cache.newType("color");
		Type primeColor = color.newSubType(cache, "primeColor");
		Generic gray = color.newInstance(cache, "gray");
		Generic yellow = primeColor.newInstance(cache, "yellow");
		Relation preciousMetalPrimeColor = preciousMetal.addRelation(cache, "metalColor", primeColor);
		iron.bind(cache, preciousMetalPrimeColor, yellow);
		iron.setLink(cache, preciousMetalPrimeColor, "almost", yellow);
		assert zink.inheritsFrom(metal);
		Relation metalColor = metal.addRelation(cache, "metalColor", color);
		zink.bind(cache, metalColor, gray);
		assert metalColor.isAlive(cache);
	}

}
