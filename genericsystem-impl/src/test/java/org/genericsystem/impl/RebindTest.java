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

	public void simpleTestRebindDependencies() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Attribute carPower = car.addAttribute(cache, "Power");
		Attribute vehiclePower = vehicle.addAttribute(cache, "Power");
		assert !carPower.isAlive(cache);
		assert ((GenericImpl) carPower).reBind(cache).inheritsFrom(vehiclePower);
	}

	public void testRebindDependencies() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Attribute carPower = car.addAttribute(cache, "Power");
		Attribute carPowerUnit = carPower.addAttribute(cache, "Unit");
		Generic myCar = car.newInstance(cache, "Audi");
		Value vPower = myCar.setValue(cache, carPower, "200");
		Value vUnit = vPower.setValue(cache, carPowerUnit, "HorsePower");
		assert vPower.getValueHolders(cache, carPowerUnit).contains(vUnit);
		assert myCar.getValueHolders(cache, carPower).contains(vPower);
		Attribute vehiclePower = vehicle.addAttribute(cache, "Power");
		assert ((GenericImpl) carPower).reBind(cache).inheritsFrom(vehiclePower);
	}

	public void testRelationRebindDependencies() {
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

	public void rebindNode() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type color = cache.newType("color");
		Type primeColor = color.newSubType(cache, "primeColor");
		primeColor.newInstance(cache, "red");
		int colorHashCode = System.identityHashCode(color);
		Generic reboundColor = ((GenericImpl) color).rebind(cache);
		int newReboundColor = System.identityHashCode(reboundColor);
		assert colorHashCode != newReboundColor;
		assert !color.isAlive(cache);
		assert reboundColor.isAlive(cache);
		assert primeColor.isAlive(cache);

	}

}
