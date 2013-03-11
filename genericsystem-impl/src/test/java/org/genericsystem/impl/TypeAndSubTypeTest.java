package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Snapshot;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class TypeAndSubTypeTest extends AbstractTest {

	public void testNewSubTypeWithEngine() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newSubType("Car");
		assert car.isAlive(cache);
		assert car.isStructural();
		assert ((GenericImpl) car).isPrimary() : car.info();
		assert ((GenericImpl) car).getComponents().size() == 0;
		assert ((GenericImpl) car).getSupers().get(0).equals(car.getEngine()) : ((GenericImpl) car).getSupers().get(0);
		assert ((GenericImpl) car).getSupers().get(0).equals(cache.getEngine());
	}

	public void testExplicitAndStructuralForSubType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicule = cache.newType("Vehicule");
		Type car = vehicule.newSubType(cache, "Car");
		assert vehicule.isStructural();
		assert car.isStructural();
	}

	public void testAncestorOfType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		assert car.getSupers().size() == 1;
		assert car.getSupers().contains(cache.getEngine());
	}

	public void testAncestorOfSubType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicule = cache.newType("Vehicule");
		Type car = vehicule.newSubType(cache, "Car");
		assert car.getSupers().size() == 1;
		assert car.getSupers().contains(vehicule);
	}

	public void testDependencyForType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		assert cache.getEngine().getInheritings(cache).contains(car);
		assert car.getInheritings(cache).isEmpty();
	}

	public void testDependencyForSubType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicule = cache.newType("Vehicule");
		Type car = vehicule.newSubType(cache, "Car");
		assert vehicule.getInheritings(cache).size() == 1;
		assert vehicule.getInheritings(cache).contains(car);
	}

	public void testgetDirectSubTypes() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicule = cache.newType("Vehicule");
		Type car = vehicule.newSubType(cache, "Car");
		Type audi = car.newSubType(cache, "Audi");
		Type mercedes = car.newSubType(cache, "Mercedes");

		assert vehicule.getDirectSubTypes(cache).size() == 1;
		assert vehicule.getDirectSubTypes(cache).contains(car);

		assert car.getDirectSubTypes(cache).size() == 2;
		assert car.getDirectSubTypes(cache).contains(audi);
		assert car.getDirectSubTypes(cache).contains(mercedes);

		assert audi.getDirectSubTypes(cache).size() == 0;
		assert mercedes.getDirectSubTypes(cache).size() == 0;
	}

	public void testGetAllSubTypes() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicule = cache.newType("Vehicule");
		Type car = vehicule.newSubType(cache, "Car");
		Type audi = car.newSubType(cache, "Audi");
		Type mercedes = car.newSubType(cache, "Mercedes");

		Snapshot<Generic> subTypes = vehicule.getSubTypes(cache);
		assert subTypes.size() == 3;
		assert subTypes.contains(car);
		assert subTypes.contains(audi);
		assert subTypes.contains(mercedes);

		subTypes = car.getSubTypes(cache);
		assert subTypes.size() == 2;
		assert subTypes.contains(audi);
		assert subTypes.contains(mercedes);

		assert audi.getSubTypes(cache).isEmpty();
		assert mercedes.getSubTypes(cache).isEmpty();
	}

	public void testgetDirectSubTypesWithImplicit() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicule = cache.newType("Vehicule");
		Type car = vehicule.newSubType(cache, "Car");
		Type audi = car.newSubType(cache, "Audi");
		Type mercedes = car.newSubType(cache, "Mercedes");

		assert vehicule.getInheritings(cache).size() == 1;
		assert vehicule.getInheritings(cache).contains(car);

		Snapshot<Generic> subTypes = car.getInheritings(cache);
		assert subTypes.size() == 2;
		assert subTypes.contains(audi);
		assert subTypes.contains(mercedes);

		assert audi.getInheritings(cache).size() == 0;
		assert mercedes.getInheritings(cache).size() == 0;
	}

}
