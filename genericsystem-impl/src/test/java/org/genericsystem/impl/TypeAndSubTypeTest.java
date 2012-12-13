package org.genericsystem.impl;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.generic.Type;
import org.genericsystem.impl.core.GenericImpl;
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
		Type car = vehicule.newSubType(cache,"Car");
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
		Type car = vehicule.newSubType(cache,"Car");
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
		Type car = vehicule.newSubType(cache,"Car");
		assert vehicule.getInheritings(cache).size() == 1;
		assert vehicule.getInheritings(cache).contains(car);
	}

	public void testGetSubTypes() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicule = cache.newType("Vehicule");
		Type car = vehicule.newSubType(cache,"Car");
		Type audi = car.newSubType(cache,"Audi");
		Type mercedes = car.newSubType(cache,"Mercedes");

		assert vehicule.getSubTypes(cache).size() == 1;
		assert vehicule.getSubTypes(cache).contains(car);

		assert car.getSubTypes(cache).size() == 2;
		assert car.getSubTypes(cache).contains(audi);
		assert car.getSubTypes(cache).contains(mercedes);

		assert audi.getSubTypes(cache).size() == 0;
		assert mercedes.getSubTypes(cache).size() == 0;
	}

	public void testGetAllSubTypes() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicule = cache.newType("Vehicule");
		Type car = vehicule.newSubType(cache,"Car");
		Type audi = car.newSubType(cache,"Audi");
		Type mercedes = car.newSubType(cache,"Mercedes");

		Snapshot<Generic> allSubTypes = vehicule.getAllSubTypes(cache);
		assert allSubTypes.size() == 4;
		assert allSubTypes.contains(vehicule);
		assert allSubTypes.contains(car);
		assert allSubTypes.contains(audi);
		assert allSubTypes.contains(mercedes);

		allSubTypes = car.getAllSubTypes(cache);
		assert allSubTypes.size() == 3;
		assert allSubTypes.contains(car);
		assert allSubTypes.contains(audi);
		assert allSubTypes.contains(mercedes);

		assert audi.getAllSubTypes(cache).size() == 1;
		assert audi.getAllSubTypes(cache).contains(audi);

		assert mercedes.getAllSubTypes(cache).size() == 1;
		assert mercedes.getAllSubTypes(cache).contains(mercedes);
	}

	public void testGetSubTypesWithImplicit() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicule = cache.newType("Vehicule");
		Type car = vehicule.newSubType(cache,"Car");
		Type audi = car.newSubType(cache,"Audi");
		Type mercedes = car.newSubType(cache,"Mercedes");

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
