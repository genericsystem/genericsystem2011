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
		Type car = cache.newSubType("Car");
		assert car.isAlive();
		assert car.isStructural();
		assert ((GenericImpl) car).isPrimary() : car.info();
		assert ((GenericImpl) car).getComponents().size() == 0;
		assert ((GenericImpl) car).getSupers().get(0).equals(car.getEngine()) : ((GenericImpl) car).getSupers().get(0);
		assert ((GenericImpl) car).getSupers().get(0).equals(cache.getEngine());
	}

	public void testExplicitAndStructuralForSubType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicule = cache.newType("Vehicule");
		Type car = vehicule.newSubType( "Car");
		assert vehicule.isStructural();
		assert car.isStructural();
	}

	public void testAncestorOfType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		assert car.getSupers().size() == 1;
		assert car.getSupers().contains(cache.getEngine());
	}

	public void testAncestorOfSubType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicule = cache.newType("Vehicule");
		Type car = vehicule.newSubType( "Car");
		// assert car.getSupers().size() == 1;
		assert car.getSupers().contains(vehicule);
	}

	public void testDependencyForType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		assert cache.getEngine().getInheritings().contains(car);
		assert car.getInheritings().isEmpty();
	}

	public void testDependencyForSubType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicule = cache.newType("Vehicule");
		Type car = vehicule.newSubType( "Car");
		assert vehicule.getInheritings().size() == 1;
		assert vehicule.getInheritings().contains(car);
	}

	public void testgetDirectSubTypes() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicule = cache.newType("Vehicule");
		Type car = vehicule.newSubType( "Car");
		Type audi = car.newSubType( "Audi");
		Type mercedes = car.newSubType( "Mercedes");

		assert vehicule.getDirectSubTypes().size() == 1;
		assert vehicule.getDirectSubTypes().contains(car);

		assert car.getDirectSubTypes().size() == 2;
		assert car.getDirectSubTypes().contains(audi);
		assert car.getDirectSubTypes().contains(mercedes);

		assert audi.getDirectSubTypes().size() == 0;
		assert mercedes.getDirectSubTypes().size() == 0;
	}

	public void testGetAllSubTypes() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicule = cache.newType("Vehicule");
		Type car = vehicule.newSubType( "Car");
		Type audi = car.newSubType( "Audi");
		Type mercedes = car.newSubType( "Mercedes");

		Snapshot<Generic> subTypes = vehicule.getSubTypes();
		assert subTypes.size() == 3;
		assert subTypes.contains(car);
		assert subTypes.contains(audi);
		assert subTypes.contains(mercedes);

		subTypes = car.getSubTypes();
		assert subTypes.size() == 2;
		assert subTypes.contains(audi);
		assert subTypes.contains(mercedes);

		assert audi.getSubTypes().isEmpty();
		assert mercedes.getSubTypes().isEmpty();
	}

	public void testgetDirectSubTypesWithImplicit() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicule = cache.newType("Vehicule");
		Type car = vehicule.newSubType( "Car");
		Type audi = car.newSubType( "Audi");
		Type mercedes = car.newSubType( "Mercedes");

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
