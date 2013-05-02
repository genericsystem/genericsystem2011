package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Statics;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.genericsystem.systemproperties.CascadeRemoveSystemProperty;
import org.genericsystem.systemproperties.MultiDirectionalSystemProperty;
import org.genericsystem.systemproperties.ReferentialIntegritySystemProperty;
import org.testng.annotations.Test;

@Test
public class SystemPropertyTest extends AbstractTest {

	public void multiDirectionalIsProperty() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		assert cache.<Type> find(MultiDirectionalSystemProperty.class).isSingularConstraintEnabled(cache);
	}

	public void relationMultiDirectionalTest() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type human = cache.newType("Human");
		Type vehicle = cache.newType("Vehicle");
		Relation humanPossessVehicle = human.setRelation(cache, "possess", vehicle);
		assert !humanPossessVehicle.isMultiDirectional(cache);
		humanPossessVehicle.enableMultiDirectional(cache);
		assert humanPossessVehicle.isMultiDirectional(cache);
		humanPossessVehicle.disableMultiDirectional(cache);
		assert !humanPossessVehicle.isMultiDirectional(cache);
	}

	// public void relationMultiDirectionalTestWithJump() {
	// Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
	// Type human = cache.newType("Human");
	// Type man = human.newSubType(cache, "Man");
	// Type vehicle = cache.newType("Vehicle");
	// Type car = vehicle.newSubType(cache, "Car");
	// Relation humanPossessVehicle = human.addRelation(cache, "possess", vehicle);
	// Relation manDriveCar = man.addSubRelation(cache, humanPossessVehicle, "drive", car);
	// humanPossessVehicle.enableMultiDirectional(cache);
	// assert manDriveCar.isMultiDirectional(cache);
	// humanPossessVehicle.disableMultiDirectional(cache);
	// assert !manDriveCar.isMultiDirectional(cache);
	// }

	// public void relationMultiDirectionalTestWithJump2() {
	// Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
	// Type human = cache.newType("Human");
	// Type man = human.newSubType(cache, "Man");
	// Type vehicle = cache.newType("Vehicle");
	// Type car = vehicle.newSubType(cache, "Car");
	// Relation humanPossessVehicle = human.addRelation(cache, "possess", vehicle);
	// Relation manDriveCar = man.addSubRelation(cache, humanPossessVehicle, "drive", car);
	// manDriveCar.enableMultiDirectional(cache);
	// assert !humanPossessVehicle.isMultiDirectional(cache);
	// manDriveCar.disableMultiDirectional(cache);
	// assert !humanPossessVehicle.isMultiDirectional(cache);
	// }

	public void cascadeRemoveTest() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type human = cache.newType("Human");
		Type vehicle = cache.newType("Vehicle");
		Relation humanPossessVehicle = human.setRelation(cache, "possess", vehicle);
		assert !humanPossessVehicle.isCascadeRemove(cache, 0);
		humanPossessVehicle.enableCascadeRemove(cache, 0);
		assert humanPossessVehicle.isCascadeRemove(cache, 0);
		humanPossessVehicle.disableCascadeRemove(cache, 0);
		assert !humanPossessVehicle.isCascadeRemove(cache, 0);
	}

	// public void cascadeRemoveWithJump() {
	// Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
	// Type human = cache.newType("Human");
	// Type man = human.newSubType(cache, "Man");
	// Type vehicle = cache.newType("Vehicle");
	// Type car = vehicle.newSubType(cache, "Car");
	//
	// Relation humanPossessVehicle = human.addRelation(cache, "possess", vehicle);
	// final Relation manDriveCar = man.addSubRelation(cache, humanPossessVehicle, "drive", car);
	//
	// humanPossessVehicle.enableCascadeRemove(cache, 0);
	// assert manDriveCar.isCascadeRemove(cache, 0);
	// manDriveCar.disableCascadeRemove(cache, 0);
	// assert humanPossessVehicle.isCascadeRemove(cache, 0);
	// assert !manDriveCar.isCascadeRemove(cache, 0);
	// }

	public void test() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Generic multiDirectionalSystemProperty = cache.find(MultiDirectionalSystemProperty.class);
		assert ((GenericImpl) multiDirectionalSystemProperty.getImplicit()).isPrimary();
		Type vehicle = cache.newType("Vehicle");
		assert !vehicle.isTree();
		assert vehicle.getAttributes(cache).contains(multiDirectionalSystemProperty) : vehicle.getAttributes(cache);
	}

	public void isEnabledSystemPropertyTest() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type human = cache.newType("Human");
		Type vehicle = cache.newType("Vehicle");
		Relation humanOwnVehicle = human.setRelation(cache, "ownership", vehicle);
		humanOwnVehicle.enableMultiDirectional(cache);
		assert humanOwnVehicle.isMultiDirectional(cache);
		assert !humanOwnVehicle.isCascadeRemove(cache, Statics.BASE_POSITION);
		humanOwnVehicle.enableCascadeRemove(cache, 0);
		assert humanOwnVehicle.isMultiDirectional(cache);
		assert humanOwnVehicle.isCascadeRemove(cache, Statics.BASE_POSITION);
		humanOwnVehicle.disableMultiDirectional(cache);
		assert cache.<Attribute> find(MultiDirectionalSystemProperty.class).isSingularConstraintEnabled(cache);
		assert !humanOwnVehicle.isMultiDirectional(cache);
		assert humanOwnVehicle.isCascadeRemove(cache, Statics.BASE_POSITION);
		humanOwnVehicle.disableCascadeRemove(cache, 0);
		assert !humanOwnVehicle.isMultiDirectional(cache);
		assert !humanOwnVehicle.isCascadeRemove(cache, Statics.BASE_POSITION);
	}

	public void isEnabledSystemPropertyAxedTest() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type human = cache.newType("Human");
		Type vehicle = cache.newType("Vehicle");
		Relation humanOwnVehicle = human.setRelation(cache, "ownership", vehicle);
		humanOwnVehicle.enableCascadeRemove(cache, 0);
		assert humanOwnVehicle.isCascadeRemove(cache, 0);
		assert !humanOwnVehicle.isCascadeRemove(cache, 2);
		humanOwnVehicle.disableCascadeRemove(cache, 0);
		assert !humanOwnVehicle.isCascadeRemove(cache, 0);
		assert !humanOwnVehicle.isCascadeRemove(cache, 2);
	}

	public void isEnabledSystemPropertyAxedTest2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type human = cache.newType("Human");
		Type vehicle = cache.newType("Vehicle");
		Relation humanOwnVehicle = human.setRelation(cache, "ownership", vehicle);
		humanOwnVehicle.enableCascadeRemove(cache, 0);
		assert humanOwnVehicle.isCascadeRemove(cache, 0);
		humanOwnVehicle.enableCascadeRemove(cache, 1);
		assert humanOwnVehicle.isCascadeRemove(cache, 0);
		humanOwnVehicle.disableCascadeRemove(cache, 1);
		assert humanOwnVehicle.isCascadeRemove(cache, 0);
		assert !humanOwnVehicle.isCascadeRemove(cache, 1);
	}

	public void testAskOnHimSelft() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		((GenericImpl) cache.find(CascadeRemoveSystemProperty.class)).isBooleanSystemPropertyEnabled(cache, CascadeRemoveSystemProperty.class);
		((GenericImpl) cache.find(ReferentialIntegritySystemProperty.class)).isBooleanSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class);
	}

	public void cascadeRemoveProperty() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation(cache, "carColor", color);
		carColor.enableCascadeRemove(cache, Statics.TARGET_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic red = color.newInstance(cache, "red");
		Link myBmwRed = myBmw.setLink(cache, carColor, "myBmwRed", red);

		myBmwRed.remove(cache);

		assert !myBmwRed.isAlive(cache);
		assert !red.isAlive(cache);
		assert myBmw.isAlive(cache);
		assert car.isAlive(cache);
		assert color.isAlive(cache);
		assert carColor.isAlive(cache);
	}

	public void cascadeRemovePropertyDisabled() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type motor = cache.newType("Motor");
		Relation powered = car.setRelation(cache, "powered by", motor);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic myMotor = motor.newInstance(cache, "myMotor");
		Link myBmwPoweredMyMotor = myBmw.setLink(cache, powered, "myBmwPoweredMyMotor", myMotor);

		myBmwPoweredMyMotor.remove(cache);

		assert !myBmwPoweredMyMotor.isAlive(cache);
		assert myMotor.isAlive(cache);
		assert myBmw.isAlive(cache);
		assert car.isAlive(cache);
		assert motor.isAlive(cache);
		assert powered.isAlive(cache);
	}

}
