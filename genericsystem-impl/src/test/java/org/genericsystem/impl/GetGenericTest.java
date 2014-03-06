package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Attribute;
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

}
