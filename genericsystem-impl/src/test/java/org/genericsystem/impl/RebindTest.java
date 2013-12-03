package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.CacheImpl;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class RebindTest extends AbstractTest {

	public void simpleTestRebindDependencies() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		Attribute carPower = car.setAttribute("Power");
		Attribute vehiclePower = vehicle.setAttribute("Power");
		assert !carPower.isAlive();
		assert ((GenericImpl) carPower).reFind().inheritsFrom(vehiclePower);

	}

	public void testRebindDependencies() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		Attribute carPower = car.setAttribute("Power");
		Attribute carPowerUnit = carPower.setAttribute("Unit");
		Generic myCar = car.addInstance("Audi");
		Holder vPower = myCar.setValue(carPower, "200");
		Holder vUnit = vPower.setValue(carPowerUnit, "HorsePower");
		assert vPower.getHolders(carPowerUnit).contains(vUnit);
		assert myCar.getHolders(carPower).contains(vPower);
		// Statics.debugCurrentThread();
		Attribute vehiclePower = vehicle.setAttribute("Power");
		// assert false;
		assert !carPower.isAlive();
		assert ((CacheImpl) cache).reFind(carPower).inheritsFrom(vehiclePower);
	}

	public void testRebindDependencies2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		Attribute carPower = car.setAttribute("Power");
		Attribute vehiclePower = vehicle.setAttribute("Power");
		assert !carPower.isAlive();
		assert ((GenericImpl) carPower).reFind().isAlive();
	}

	public void testRelationRebindDependencies() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type metal = cache.addType("metal");
		Type preciousMetal = metal.addSubType("preciousMetal");
		Generic zink = metal.addInstance("zink");
		Generic iron = preciousMetal.addInstance("iron");
		Type color = cache.addType("color");
		Type primeColor = color.addSubType("primeColor");
		Generic gray = color.addInstance("gray");
		Generic yellow = primeColor.addInstance("yellow");
		Relation preciousMetalPrimeColor = preciousMetal.setRelation("metalColor", primeColor);
		iron.bind(preciousMetalPrimeColor, yellow);
		iron.setLink(preciousMetalPrimeColor, "almost", yellow);
		assert zink.inheritsFrom(metal);
		Relation metalColor = metal.setRelation("metalColor", color);
		zink.bind(metalColor, gray);
		assert metalColor.isAlive();
	}

	public void rebindAttributeNode() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type color = cache.addType("color");
		Type primeColor = color.addSubType("primeColor");

		Generic red = primeColor.addInstance("red");
		Attribute lightness = primeColor.setAttribute("lightness");
		Holder lightnessValue = red.setValue(lightness, "40");
		Generic reboundLightness = ((GenericImpl) lightness).reBind();
		assert !lightness.isAlive();
		assert !lightnessValue.isAlive();
		assert null != ((CacheImpl) cache).reFind(lightness);
		assert reboundLightness.isAlive();
		assert primeColor.isAlive();

	}

	public void rebindTypeNode() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		Generic mycar = car.addInstance("mycar");
		Attribute carPower = car.setAttribute("power");
		mycar.setValue(carPower, "123");
		assert vehicle.isAlive();
		Generic reboundVehicle = ((GenericImpl) vehicle).reBind();
		assert !vehicle.isAlive();
		assert !car.isAlive();
		assert null != ((CacheImpl) cache).reFind(vehicle);
		assert reboundVehicle.isAlive();
	}

	public void rebindTypeTest() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Generic myCar = car.addInstance("myCar");
		Attribute power = car.setAttribute("Power");
		Generic myCar123 = power.addInstance("123", myCar);
		assert myCar123.inheritsFrom(power);
		Generic myCar2 = car.addInstance("myCar2");
		myCar2.setValue(power, "10");
		((GenericImpl) car).reBind();
		assert myCar.getMeta().equals(myCar2.getMeta());
	}

	public void rebindTypeTest2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		car.remove();
		((GenericImpl) car).reBind();
	}
}
