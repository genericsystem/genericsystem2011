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
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType( "Car");
		Attribute carPower = car.setAttribute( "Power");
		Attribute vehiclePower = vehicle.setAttribute( "Power");
		assert !carPower.isAlive();
		assert ((GenericImpl) carPower).reFind().inheritsFrom(vehiclePower);

	}

	public void testRebindDependencies() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType( "Car");
		Attribute carPower = car.setAttribute( "Power");
		Attribute carPowerUnit = carPower.setAttribute( "Unit");
		Generic myCar = car.newInstance( "Audi");
		Holder vPower = myCar.setValue( carPower, "200");
		Holder vUnit = vPower.setValue( carPowerUnit, "HorsePower");
		assert vPower.getHolders( carPowerUnit).contains(vUnit);
		assert myCar.getHolders( carPower).contains(vPower);
		Attribute vehiclePower = vehicle.setAttribute( "Power");
		assert ((CacheImpl) cache).reFind(carPower).inheritsFrom(vehiclePower);
	}

	public void testRelationRebindDependencies() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type metal = cache.newType("metal");
		Type preciousMetal = metal.newSubType( "preciousMetal");
		Generic zink = metal.newInstance( "zink");
		Generic iron = preciousMetal.newInstance( "iron");
		Type color = cache.newType("color");
		Type primeColor = color.newSubType( "primeColor");
		Generic gray = color.newInstance( "gray");
		Generic yellow = primeColor.newInstance( "yellow");
		Relation preciousMetalPrimeColor = preciousMetal.setRelation( "metalColor", primeColor);
		iron.bind( preciousMetalPrimeColor, yellow);
		iron.setLink( preciousMetalPrimeColor, "almost", yellow);
		assert zink.inheritsFrom(metal);
		Relation metalColor = metal.setRelation( "metalColor", color);
		zink.bind( metalColor, gray);
		assert metalColor.isAlive();
	}

	public void rebindAttributeNode() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type color = cache.newType("color");
		Type primeColor = color.newSubType( "primeColor");

		Generic red = primeColor.newInstance( "red");
		Attribute lightness = primeColor.setAttribute( "lightness");
		Holder lightnessValue = red.setValue( lightness, "40");
		Generic reboundLightness = ((GenericImpl) lightness).reBind();
		assert !lightness.isAlive();
		assert !lightnessValue.isAlive();
		assert null != ((CacheImpl) cache).reFind(lightness);
		assert reboundLightness.isAlive();
		assert primeColor.isAlive();

	}

	public void rebindTypeNode() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType( "Car");
		Generic mycar = car.newInstance( "mycar");
		Attribute carPower = car.setAttribute( "power");
		mycar.setValue( carPower, "123");
		assert vehicle.isAlive();
		Generic reboundVehicle = ((GenericImpl) vehicle).reBind();
		assert !vehicle.isAlive();
		assert !car.isAlive();
		assert null != ((CacheImpl) cache).reFind(vehicle);
		assert reboundVehicle.isAlive();
	}

	public void rebindTypeTest() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Generic myCar = car.newInstance( "myCar");
		Attribute power = car.setAttribute( "Power");
		Generic myCar123 = power.newInstance( "123", myCar);
		assert myCar123.inheritsFrom(power);
		Generic myCar2 = car.newInstance( "myCar2");
		myCar2.setValue( power, "10");
		((GenericImpl) car).reBind();
		assert myCar.getMeta().equals(myCar2.getMeta());
	}
}
