package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class GetGenericTest extends AbstractTest {

	public void testGetType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.setType("Car");
		assert cache.getGeneric("Car", car.getMeta()) == car : cache.getGeneric("Car", car.getMeta()).info();
	}

	public void testGetAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.setType("Car");
		Attribute powerVehicle = car.setAttribute("power");
		assert cache.getGeneric("power", powerVehicle.getMeta(), car) == powerVehicle : cache.getGeneric("power", powerVehicle.getMeta(), car).info();
	}

	public void testGetAttributeByEngine() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.setType("Car");
		car.setAttribute("power");
		assert cache.getGeneric("power", cache.getEngine(), car) == null;
	}

	public void testGetAttribute2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.setType("Car");
		Attribute powerVehicle = car.setAttribute("power");
		Type bike = cache.setType("Bike");
		bike.setAttribute("power");
		assert cache.getGeneric("power", powerVehicle.getMeta(), car) == powerVehicle : cache.getGeneric("power", powerVehicle.getMeta(), car).info();
	}

	public void testBug() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.setType("Car");
		Attribute carPower = car.setAttribute("power");
		Holder car123 = car.setValue(carPower, 123);
		Holder car126 = car.setValue(carPower, 126);
		Generic myCar = car.setInstance("myCar");
		Holder old = myCar.setValue(car123, 123);
		Holder myCar123 = myCar.setValue(car126, 123);
		assert myCar123.inheritsFrom(car123);
		assert myCar123.inheritsFrom(car126);
		assert !old.isAlive();
	}

}
