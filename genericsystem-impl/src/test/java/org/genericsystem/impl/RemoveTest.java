package org.genericsystem.impl;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.exception.ReferentialIntegrityConstraintViolationException;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Type;
import org.genericsystem.api.generic.Holder;
import org.testng.annotations.Test;

@Test
public class RemoveTest extends AbstractTest {

	public void testRemoveType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		vehicle.remove(cache);
		assert !vehicle.isAlive(cache);
		assert !cache.getEngine().getInheritings(cache).contains(vehicle);
	}

	public void testRemoveTypeWithSubType() {
		new RollbackCatcher() {
			@Override
			public void intercept() {
				Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
				Type vehicle = cache.newType("Vehicle");
				vehicle.newSubType(cache, "Car");
				vehicle.remove(cache);
			}
		}.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
	}

	public void testRemoveAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute power = vehicle.setAttribute(cache, "power");
		power.remove(cache);
		assert !power.isAlive(cache);
		assert !cache.getEngine().getInheritings(cache).contains(power);
		assert cache.getEngine().getInheritings(cache).contains(vehicle);
	}

	// public void testRemoveAttributeWithSubAttribute() {
	// final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
	// Type vehicle = cache.newType("Vehicle");
	// final Attribute vehiclePower = vehicle.addAttribute(cache,"power");
	// Type car = vehicle.newSubType(cache,"Car");
	// Attribute carElectricPower = car.addSubAttribute(cache,vehiclePower, "electricPower");
	// cache.flush();
	// new RollbackCatcher() {
	// @Override
	// public void intercept() {
	// vehiclePower.remove(cache);
	// }
	// }.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
	// assert vehicle.isAlive(cache);
	// assert vehiclePower.isAlive(cache);
	// assert carElectricPower.isAlive(cache);
	// }

	public void testRemoveProperty() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type window = cache.newType("Window");
		Attribute height = window.setProperty(cache, "Height");
		Generic myWindow = window.newInstance(cache, "MyWindow");
		Holder myHeight1 = ((Attribute) myWindow).setValue(cache, height, 165);
		myHeight1.remove(cache);
		assert cache.getEngine().getInheritings(cache).contains(window);
		assert cache.getEngine().getInheritings(cache).contains(height.getImplicit());
		assert !cache.getEngine().getInheritings(cache).contains(height);
		assert !cache.getEngine().getAllSubTypes(cache).contains(myHeight1);
	}

	// public void testRemoveRelationWithSubRelation() {
	// final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
	// Type human = cache.newType("Human");
	// Type man = human.newSubType(cache,"Man");
	// Type vehicle = cache.newType("Vehicle");
	// Type car = vehicle.newSubType(cache,"Car");
	// final Relation humanDriveVehicle = human.addRelation(cache,"drive", vehicle);
	// Relation manSubDriveCar = man.addSubRelation(cache,humanDriveVehicle, "subDrive", car);
	// cache.flush();
	// new RollbackCatcher() {
	// @Override
	// public void intercept() {
	// humanDriveVehicle.remove(cache);
	// }
	// }.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
	// assert humanDriveVehicle.isAlive(cache);
	// assert human.isAlive(cache);
	// assert man.isAlive(cache);
	// assert vehicle.isAlive(cache);
	// assert car.isAlive(cache);
	// assert manSubDriveCar.isAlive(cache);
	// }

	public void testRemoveInstance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type window = cache.newType("Window");
		Generic myWindow = window.newInstance(cache, "myWindow");
		myWindow.remove(cache);
	}

	public void testRemoveTypeWithInstance() {
		new RollbackCatcher() {
			@Override
			public void intercept() {
				Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
				Type window = cache.newType("Window");
				window.newInstance(cache, "myWindow");
				window.remove(cache);
			}
		}.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
	}

}
