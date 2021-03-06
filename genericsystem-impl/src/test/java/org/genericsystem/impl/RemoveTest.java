package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.RemoveStrategy;
import org.genericsystem.exception.NotRemovableException;
import org.genericsystem.exception.ReferentialIntegrityConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.genericsystem.impl.ApiTest.Vehicle;
import org.testng.annotations.Test;

@Test
public class RemoveTest extends AbstractTest {

	public void testRemoveType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		vehicle.remove();
		assert !vehicle.isAlive();
		assert !cache.getEngine().getInheritings().contains(vehicle);
	}

	public void testRemoveTypeWithSubType() {
		new RollbackCatcher() {
			@Override
			public void intercept() {
				Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
				Type vehicle = cache.addType("Vehicle");
				vehicle.addSubType("Car");
				vehicle.remove();
			}
		}.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
	}

	public void testRemoveAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
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
		Type window = cache.addType("Window");
		Attribute height = window.setProperty("Height");
		Generic myWindow = window.addInstance("MyWindow");
		Holder myHeight1 = ((Attribute) myWindow).setValue(height, 165);
		myHeight1.remove();
		assert cache.getEngine().getInheritings().contains(window);
		assert !cache.getEngine().getInheritings().contains(height);
		assert !cache.getEngine().getAllSubTypes().contains(myHeight1);
	}

	public void testRemoveRelationWithSubRelation() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type human = cache.addType("Human");
		Type man = human.addSubType("Man");
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		final Relation humanDriveVehicle = human.addRelation("drive", vehicle);
		Relation manSubDriveCar = ((GenericImpl) man).addSubRelation(humanDriveVehicle, "subDrive", car);
		cache.flush();
		new RollbackCatcher() {
			@Override
			public void intercept() {
				humanDriveVehicle.remove();
			}
		}.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
		assert humanDriveVehicle.isAlive();
		assert human.isAlive();
		assert man.isAlive();
		assert vehicle.isAlive();
		assert car.isAlive();
		assert manSubDriveCar.isAlive();
	}

	public void testRemoveInstance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type window = cache.addType("Window");
		Generic myWindow = window.addInstance("myWindow");
		myWindow.remove();
	}

	public void testRemoveTypeWithInstance() {
		new RollbackCatcher() {
			@Override
			public void intercept() {
				Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
				Type window = cache.addType("Window");
				window.addInstance("myWindow");
				window.remove();
			}
		}.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
	}

	public void testRemoveSystemGeneric() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Vehicle.class).start();

		new RollbackCatcher() {

			@Override
			public void intercept() {
				cache.find(Vehicle.class).remove();
			}

		}.assertIsCausedBy(NotRemovableException.class);
	}

	public void testRemoveLinkWithItsAutomaticsOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type car = cache.addType("Car");
		Type color = cache.addType("Color");
		Relation carColor = car.setRelation("CarColor", color);
		carColor.enableSingularConstraint();
		Attribute intensity = carColor.setAttribute("Intensity");

		Generic red = color.addInstance("Red");
		Generic grey = color.addInstance("Grey");
		Link defaultCarColor = car.setLink(carColor, "DefaultCarColor", red); // default color of car

		final Generic bmw = car.addInstance("Bmw");
		Generic mercedes = car.addInstance("Mercedes");
		final Generic lada = car.addInstance("Lada");
		mercedes.bind(carColor, grey);

		red.getLink(carColor, lada).setValue(intensity, "60%");

		defaultCarColor.remove(RemoveStrategy.FORCE);

		/* Links BMW <-> Red and Lada <-> Red disappear and Mercedes <-> Grey stay */
		assert bmw.getLink(carColor, red) == null;
		assert mercedes.getLink(carColor, grey) == null;
		assert lada.getLink(carColor, red) == null;

		/* No more lisnks from Red */
		assert red.getLinks(carColor).size() == 0;
	}

}
