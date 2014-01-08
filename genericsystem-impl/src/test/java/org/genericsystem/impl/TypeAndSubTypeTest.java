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
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		assert car.isAlive();
		assert car.isStructural();
		assert ((GenericImpl) car).components().size() == 0;
		assert ((GenericImpl) car).supers().get(0).equals(car.getEngine()) : ((GenericImpl) car).supers().get(0);
		assert ((GenericImpl) car).supers().get(0).equals(cache.getEngine());
	}

	public void testExplicitAndStructuralForSubType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicule = cache.addType("Vehicule");
		Type car = vehicule.addSubType("Car");
		assert vehicule.isStructural();
		assert car.isStructural();
	}

	public void testAncestorOfType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		assert car.supers().size() == 1;
		assert car.supers().contains(cache.getEngine());
	}

	public void testAncestorOfSubType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicule = cache.addType("Vehicule");
		Type car = vehicule.addSubType("Car");
		// assert car.getSupers().size() == 1;
		assert car.supers().contains(vehicule);
	}

	public void testDependencyForType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		assert cache.getEngine().getInheritings().contains(car);
		assert car.getInheritings().isEmpty();
	}

	public void testDependencyForSubType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicule = cache.addType("Vehicule");
		Type car = vehicule.addSubType("Car");
		assert vehicule.getInheritings().size() == 1;
		assert vehicule.getInheritings().contains(car);
	}

	public void testgetDirectSubTypes() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicule = cache.addType("Vehicule");
		Type car = vehicule.addSubType("Car");
		Type audi = car.addSubType("Audi");
		Type mercedes = car.addSubType("Mercedes");

		assert vehicule.getSubTypes().size() == 1;
		assert vehicule.getSubTypes().contains(car);

		assert car.getSubTypes().size() == 2;
		assert car.getSubTypes().contains(audi);
		assert car.getSubTypes().contains(mercedes);

		assert audi.getSubTypes().size() == 0;
		assert mercedes.getSubTypes().size() == 0;
	}

	public void testGetAllSubTypes() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicule = cache.addType("Vehicule");
		Type car = vehicule.addSubType("Car");
		Type audi = car.addSubType("Audi");
		Type mercedes = car.addSubType("Mercedes");

		Snapshot<Generic> subTypes = vehicule.getAllSubTypes();
		assert subTypes.size() == 3;
		assert subTypes.contains(car);
		assert subTypes.contains(audi);
		assert subTypes.contains(mercedes);

		subTypes = car.getAllSubTypes();
		assert subTypes.size() == 2;
		assert subTypes.contains(audi);
		assert subTypes.contains(mercedes);

		assert audi.getAllSubTypes().isEmpty();
		assert mercedes.getAllSubTypes().isEmpty();
	}

	public void testgetDirectSubTypesWithImplicit() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicule = cache.addType("Vehicule");
		Type car = vehicule.addSubType("Car");
		Type audi = car.addSubType("Audi");
		Type mercedes = car.addSubType("Mercedes");

		assert vehicule.getInheritings().size() == 1;
		assert vehicule.getInheritings().contains(car);

		Snapshot<Generic> subTypes = car.getInheritings();
		assert subTypes.size() == 2;
		assert subTypes.contains(audi);
		assert subTypes.contains(mercedes);

		assert audi.getInheritings().size() == 0;
		assert mercedes.getInheritings().size() == 0;
	}

}
