package org.genericsystem.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Statics;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class MapTest extends AbstractTest {

	public void testPropertyMap() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		Generic myBmw = car.addInstance("myBmw");
		Map<Serializable, Serializable> vehicleMap = vehicle.getPropertiesMap();
		Map<Serializable, Serializable> map2 = new HashMap<>();
		Map<Serializable, Serializable> carMap = car.getPropertiesMap();
		Map<Serializable, Serializable> myBmwMap = myBmw.getPropertiesMap();
		Statics.logTimeIfCurrentThreadDebugged("starts");
		for (int i = 0; i < 1000; i++) {
			int key = (int) (Math.random() * 10);
			assert Objects.equals(vehicleMap.get(key), map2.get(key));
			vehicleMap.put(key, i);
			map2.put(key, i);
			assert Objects.equals(vehicleMap.get(key), map2.get(key)) : "key : " + key + " i : " + i + " " + vehicleMap.get(key) + " " + map2.get(key);
			assert Objects.equals(map2.get(key), carMap.get(key)) : "key : " + key + " i : " + i + " " + map2.get(key) + " " + carMap.get(key);
			assert Objects.equals(carMap.get(key), myBmwMap.get(key)) : "key : " + key + " i : " + i + " " + map2.get(key) + " " + carMap.get(key);
		}
		Statics.logTimeIfCurrentThreadDebugged("stops");

		vehicleMap.put("key2", false);
		assert !(Boolean) carMap.get("key2");
		carMap.put("key2", true);
		assert !(Boolean) vehicleMap.get("key2");
		assert (Boolean) carMap.get("key2");
		carMap.remove("key2");
		assert !(Boolean) vehicleMap.get("key2");
		assert !(Boolean) carMap.get("key2");
	}

	public void testPropertyInherit() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		Generic myBmw = car.addInstance("myBmw");
		vehicle.getPropertiesMap().put("power", 123);
		assert vehicle.getPropertiesMap().get("power").equals(123) : vehicle.getPropertiesMap();
		myBmw.getPropertiesMap().put("wheel", 4);
		assert myBmw.getPropertiesMap().get("power").equals(123) : myBmw.getPropertiesMap();
		assert myBmw.getPropertiesMap().get("wheel").equals(4);
		myBmw.getPropertiesMap().remove("power");
		assert vehicle.getPropertiesMap().get("power").equals(123) : vehicle.getPropertiesMap().get("power");
		assert myBmw.getPropertiesMap().get("power") == null : myBmw.getPropertiesMap().get("power");
		assert myBmw.getPropertiesMap().get("wheel").equals(4);
	}

	public void testPropertyInherit2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Generic myBmw = car.addInstance("myBmw");
		car.getPropertiesMap().put("power", 123);
		assert car.getPropertiesMap().get("power").equals(123) : car.getPropertiesMap();
		assert myBmw.getPropertiesMap().get("power").equals(123) : myBmw.getPropertiesMap();
		myBmw.getPropertiesMap().remove("power");
		assert car.getPropertiesMap().get("power").equals(123);
		assert myBmw.getPropertiesMap().get("power") == null : myBmw.getPropertiesMap().get("power");
	}

	public void testSingleMap() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type vehicle = cache.addType("Vehicle");
		vehicle.getPropertiesMap().put("power", 123);
		vehicle.getPropertiesMap().put("power", 255);
		assert !vehicle.getPropertiesMap().get("power").equals(123);
		assert vehicle.getPropertiesMap().get("power").equals(255);
		vehicle.getPropertiesMap().remove("power");
		assert vehicle.getPropertiesMap().isEmpty();
	}

	public void testProperty() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		vehicle.getPropertiesMap().put("power", 123);
		vehicle.getPropertiesMap().put("whell", 4);
		assert car.getPropertiesMap().get("power").equals(123);
		car.getPropertiesMap().put("power", 255);
		assert vehicle.getPropertiesMap().get("power").equals(123);
		assert !car.getPropertiesMap().get("power").equals(123);
		assert car.getPropertiesMap().get("power").equals(255);
	}

	public void testRemove() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		vehicle.getPropertiesMap().put("power", 123);
		assert car.getPropertiesMap().get("power").equals(123);
		car.getPropertiesMap().remove("power");
		// car.getPropertiesMap().put("power", null);
		car.getPropertiesMap().remove("power");
		assert car.getPropertiesMap().get("power").equals(123) : car.getPropertiesMap();
		car.getPropertiesMap().put("power", 123);
		assert car.getPropertiesMap().get("power").equals(123);
		// vehicle.getProperties().put("power", null);
		// assert vehicle.getProperties().get("power") == null;
		// assert car.getProperties().get("power") == null;
	}

	public void testRemoveNotNull() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		vehicle.getPropertiesMap().put("power", 123);
		assert car.getPropertiesMap().get("power").equals(123);
		car.getPropertiesMap().put("power", null);
		// assert car.getPropertiesMap().get("power").equals(254) : car.getPropertiesMap();
		car.getPropertiesMap().put("power", 255);
	}

	public void testRemove2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		vehicle.getPropertiesMap().put("power", 123);
		assert car.getPropertiesMap().get("power").equals(123);
		car.getPropertiesMap().remove("power");
		assert car.getPropertiesMap().get("power") == null : car.getPropertiesMap().get("power");
		car.getPropertiesMap().put("power", 124);
		assert car.getPropertiesMap().get("power").equals(124);
	}

	public void testOtherMap() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(MyMapProvider.class).start();
		Type vehicle = cache.addType("Vehicle");
		vehicle.getMap(MyMapProvider.class).put("power", 123);
		assert vehicle.getMap(MyMapProvider.class).get("power").equals(123);
		vehicle.getMap(MyMapProvider.class).remove("power");
		assert vehicle.getMap(MyMapProvider.class).get("power") == null;

		vehicle.getPropertiesMap().put("power", 123);
		assert vehicle.getPropertiesMap().get("power").equals(123);
		assert vehicle.getMap(MyMapProvider.class).get("power") == null;
	}

	public void testSingularMap() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type color = cache.addType("Color");
		assert !((Holder) vehicle).isSingularConstraintEnabled();
		Relation vehicleColor = vehicle.setRelation("VehicleColor", color).<Relation> enableSingularConstraint(Statics.TARGET_POSITION);
		assert vehicleColor.isSingularConstraintEnabled(Statics.TARGET_POSITION);
		assert !vehicleColor.isSingularConstraintEnabled(Statics.BASE_POSITION);
		assert !((Holder) vehicle).isSingularConstraintEnabled();
		vehicleColor.disableSingularConstraint(Statics.TARGET_POSITION);
		assert !vehicleColor.isSingularConstraintEnabled(Statics.TARGET_POSITION);
		assert !vehicleColor.isSingularConstraintEnabled(Statics.BASE_POSITION);
		assert !((Holder) vehicle).isSingularConstraintEnabled();
	}
}
