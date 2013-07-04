package org.genericsystem.impl;

import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.exception.ReferentialIntegrityConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class RemoveTest extends AbstractTest {

	public void testRemoveType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		vehicle.remove();
		assert !vehicle.isAlive();
		assert !cache.getEngine().getInheritings().contains(vehicle);
	}

	public void testRemoveTypeWithSubType() {
		new RollbackCatcher() {
			@Override
			public void intercept() {
				Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
				Type vehicle = cache.newType("Vehicle");
				vehicle.newSubType("Car");
				vehicle.remove();
			}
		}.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
	}

	public void testRemoveAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Attribute power = vehicle.setAttribute("power");
		power.remove();
		assert !power.isAlive();
		assert !cache.getEngine().getInheritings().contains(power);
		assert cache.getEngine().getInheritings().contains(vehicle);
	}

	// public void testRemoveAttributeWithSubAttribute() {
	// final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
	// Type vehicle = cache.newType("Vehicle");
	// final Attribute vehiclePower = vehicle.addAttribute("power");
	// Type car = vehicle.newSubType("Car");
	// Attribute carElectricPower = car.addSubAttribute(vehiclePower, "electricPower");
	// cache.flush();
	// new RollbackCatcher() {
	// @Override
	// public void intercept() {
	// vehiclePower.remove();
	// }
	// }.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
	// assert vehicle.isAlive();
	// assert vehiclePower.isAlive();
	// assert carElectricPower.isAlive();
	// }

	public void testRemoveProperty() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type window = cache.newType("Window");
		Attribute height = window.setProperty("Height");
		Generic myWindow = window.newInstance("MyWindow");
		Holder myHeight1 = ((Attribute) myWindow).setValue(height, 165);
		myHeight1.remove();
		assert cache.getEngine().getInheritings().contains(window);
		assert cache.getEngine().getInheritings().contains(height.getImplicit());
		assert !cache.getEngine().getInheritings().contains(height);
		assert !cache.getEngine().getSubTypes().contains(myHeight1);
	}

	// public void testRemoveRelationWithSubRelation() {
	// final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
	// Type human = cache.newType("Human");
	// Type man = human.newSubType("Man");
	// Type vehicle = cache.newType("Vehicle");
	// Type car = vehicle.newSubType("Car");
	// final Relation humanDriveVehicle = human.addRelation("drive", vehicle);
	// Relation manSubDriveCar = man.addSubRelation(humanDriveVehicle, "subDrive", car);
	// cache.flush();
	// new RollbackCatcher() {
	// @Override
	// public void intercept() {
	// humanDriveVehicle.remove();
	// }
	// }.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
	// assert humanDriveVehicle.isAlive();
	// assert human.isAlive();
	// assert man.isAlive();
	// assert vehicle.isAlive();
	// assert car.isAlive();
	// assert manSubDriveCar.isAlive();
	// }

	public void testRemoveInstance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type window = cache.newType("Window");
		Generic myWindow = window.newInstance("myWindow");
		myWindow.remove();
	}

	public void testRemoveTypeWithInstance() {
		new RollbackCatcher() {
			@Override
			public void intercept() {
				Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
				Type window = cache.newType("Window");
				window.newInstance("myWindow");
				window.remove();
			}
		}.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
	}

	public void testRemoveSystemGeneric() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Vehicle.class).start();
		cache.find(Vehicle.class).remove();
	}

	@SystemGeneric
	public static class Vehicle {

	}

}
