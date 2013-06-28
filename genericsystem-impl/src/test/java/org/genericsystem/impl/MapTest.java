package org.genericsystem.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.RequiredConstraint;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.core.Cache;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Type;
import org.genericsystem.impl.MapTest.MyMapProvider.MyKey;
import org.genericsystem.impl.MapTest.MyMapProvider.MyValue;
import org.genericsystem.map.AbstractMapProvider;
import org.testng.annotations.Test;

@Test
public class MapTest extends AbstractTest {

	public void testPropertyMap() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType( "Car");
		Generic myBmw = car.newInstance( "myBmw");
		Map<Serializable, Serializable> vehicleMap = vehicle.getProperties();
		Map<Serializable, Serializable> map2 = new HashMap<>();
		Map<Serializable, Serializable> carMap = car.getProperties();
		Map<Serializable, Serializable> myBmwMap = myBmw.getProperties();

		for (int i = 0; i < 1000; i++) {
			int key = (int) (Math.random() * 10);
			assert Objects.equals(vehicleMap.get(key), map2.get(key));
			vehicleMap.put(key, i);
			map2.put(key, i);
			assert Objects.equals(vehicleMap.get(key), map2.get(key)) : "key : " + key + " i : " + i + " " + vehicleMap.get(key) + " " + map2.get(key);
			assert Objects.equals(map2.get(key), carMap.get(key)) : "key : " + key + " i : " + i + " " + map2.get(key) + " " + carMap.get(key);
			assert Objects.equals(carMap.get(key), myBmwMap.get(key)) : "key : " + key + " i : " + i + " " + map2.get(key) + " " + carMap.get(key);
		}

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
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType( "Car");
		Generic myBmw = car.newInstance( "myBmw");
		vehicle.getProperties().put("power", 123);
		assert vehicle.getProperties().get("power").equals(123) : vehicle.getProperties();
		myBmw.getProperties().put("wheel", 4);
		assert myBmw.getProperties().get("power").equals(123) : myBmw.getProperties();
		assert myBmw.getProperties().get("wheel").equals(4);
		myBmw.getProperties().remove("power");
		assert vehicle.getProperties().get("power").equals(123);
		assert myBmw.getProperties().get("power") == null : myBmw.getProperties().get("power");
		assert myBmw.getProperties().get("wheel").equals(4);
	}

	public void testPropertyInherit2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Generic myBmw = car.newInstance( "myBmw");
		car.getProperties().put("power", 123);
		assert car.getProperties().get("power").equals(123) : car.getProperties();
		assert myBmw.getProperties().get("power").equals(123) : myBmw.getProperties();
		myBmw.getProperties().remove("power");
		assert car.getProperties().get("power").equals(123);
		assert myBmw.getProperties().get("power") == null : myBmw.getProperties().get("power");
	}

	public void testSingleMap() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type vehicle = cache.newType("Vehicle");
		vehicle.getProperties().put("power", 123);
		vehicle.getProperties().put("power", 255);
		assert !vehicle.getProperties().get("power").equals(123);
		assert vehicle.getProperties().get("power").equals(255);
		vehicle.getProperties().remove("power");
		assert vehicle.getProperties().isEmpty();
	}

	public void testOnInstance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Generic myBmw = car.newInstance( "myBmw");
		myBmw.getProperties().put("power", 123);
		assert myBmw.getProperties().get("power").equals(123) : myBmw.getProperties();
		myBmw.getProperties().put("wheel", 4);
		assert myBmw.getProperties().get("power").equals(123) : myBmw.getProperties();
		assert myBmw.getProperties().get("wheel").equals(4);
		myBmw.getProperties().remove("power");
		assert myBmw.getProperties().get("power") == null;
	}

	public void testProperty() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType( "Car");
		vehicle.getProperties().put("power", 123);
		vehicle.getProperties().put("whell", 4);
		assert car.getProperties().get("power").equals(123);
		car.getProperties().put("power", 255);
		assert vehicle.getProperties().get("power").equals(123);
		assert !car.getProperties().get("power").equals(123);
		assert car.getProperties().get("power").equals(255);
	}

	public void testRemove() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType( "Car");
		vehicle.getProperties().put("power", 123);
		assert car.getProperties().get("power").equals(123);
		car.getProperties().remove("power");
		car.getProperties().remove("power");
		assert car.getProperties().get("power") == null : car.getProperties();
		car.getProperties().put("power", 123);
		assert car.getProperties().get("power").equals(123);
		// vehicle.getProperties().put("power", null);
		// assert vehicle.getProperties().get("power") == null;
		// assert car.getProperties().get("power") == null;
	}

	public void testRemove2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType( "Car");
		vehicle.getProperties().put("power", 123);
		assert car.getProperties().get("power").equals(123);
		car.getProperties().remove("power");
		car.getProperties().remove("power");
		assert car.getProperties().get("power") == null;
		car.getProperties().put("power", 124);
		assert car.getProperties().get("power").equals(124);
		// vehicle.getProperties().put("power", null);
		// assert vehicle.getProperties().get("power") == null;
		// assert car.getProperties().get("power").equals(124);
	}

	public void testOtherMap() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(MyMapProvider.class).start();
		Type vehicle = cache.newType("Vehicle");
		vehicle.getMap( MyMapProvider.class).put("power", 123);
		assert vehicle.getMap( MyMapProvider.class).get("power").equals(123);
		vehicle.getMap( MyMapProvider.class).remove("power");
		assert vehicle.getMap( MyMapProvider.class).get("power") == null;

		vehicle.getProperties().put("power", 123);
		assert vehicle.getProperties().get("power").equals(123);
		assert vehicle.getMap( MyMapProvider.class).get("power") == null;
	}

	@SystemGeneric
	@Components(Engine.class)
	@Dependencies({ MyKey.class, MyValue.class })
	public static class MyMapProvider extends AbstractMapProvider {

		@Override
		@SuppressWarnings("unchecked")
		public <T extends Attribute> Class<T> getKeyAttributeClass() {
			return (Class<T>) MyKey.class;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T extends Attribute> Class<T> getValueAttributeClass() {
			return (Class<T>) MyValue.class;
		}

		@SystemGeneric
		@Components(MyMapProvider.class)
		public static class MyKey extends GenericImpl implements Attribute {

		}

		@SystemGeneric
		@Components(MyKey.class)
		@SingularConstraint
		@RequiredConstraint
		public static class MyValue extends GenericImpl implements Attribute {

		}
	}
}
