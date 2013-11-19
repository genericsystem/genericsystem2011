package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Statics;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class SystemPropertyTest extends AbstractTest {

	// public void multiDirectionalIsProperty() {
	// Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
	// assert cache.<Type> find(MultiDirectionalSystemProperty.class).isSingularConstraintEnabled();
	// }

	// public void relationMultiDirectionalTest() {
	// Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
	// Type human = cache.newType("Human");
	// Type vehicle = cache.newType("Vehicle");
	// Relation humanPossessVehicle = human.setRelation("possess", vehicle);
	// assert !humanPossessVehicle.isMultiDirectional();
	// humanPossessVehicle.enableMultiDirectional();
	// assert humanPossessVehicle.isMultiDirectional();
	// humanPossessVehicle.disableMultiDirectional();
	// assert !humanPossessVehicle.isMultiDirectional();
	// }

	// public void relationMultiDirectionalTestWithJump() {
	// Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
	// Type human = cache.newType("Human");
	// Type man = human.newSubType( "Man");
	// Type vehicle = cache.newType("Vehicle");
	// Type car = vehicle.newSubType( "Car");
	// Relation humanPossessVehicle = human.addRelation( "possess", vehicle);
	// Relation manDriveCar = man.addSubRelation( humanPossessVehicle, "drive", car);
	// humanPossessVehicle.enableMultiDirectional();
	// assert manDriveCar.isMultiDirectional();
	// humanPossessVehicle.disableMultiDirectional();
	// assert !manDriveCar.isMultiDirectional();
	// }

	// public void relationMultiDirectionalTestWithJump2() {
	// Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
	// Type human = cache.newType("Human");
	// Type man = human.newSubType( "Man");
	// Type vehicle = cache.newType("Vehicle");
	// Type car = vehicle.newSubType( "Car");
	// Relation humanPossessVehicle = human.addRelation( "possess", vehicle);
	// Relation manDriveCar = man.addSubRelation( humanPossessVehicle, "drive", car);
	// manDriveCar.enableMultiDirectional();
	// assert !humanPossessVehicle.isMultiDirectional();
	// manDriveCar.disableMultiDirectional();
	// assert !humanPossessVehicle.isMultiDirectional();
	// }

	public void cascadeRemoveTest() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type human = cache.addType("Human");
		Type vehicle = cache.addType("Vehicle");
		Relation humanPossessVehicle = human.setRelation("possess", vehicle);
		assert !humanPossessVehicle.isCascadeRemove(0);
		humanPossessVehicle.enableCascadeRemove(0);
		assert humanPossessVehicle.isCascadeRemove(0);
		humanPossessVehicle.disableCascadeRemove(0);
		assert !humanPossessVehicle.isCascadeRemove(0);
	}

	// public void cascadeRemoveWithJump() {
	// Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
	// Type human = cache.newType("Human");
	// Type man = human.newSubType( "Man");
	// Type vehicle = cache.newType("Vehicle");
	// Type car = vehicle.newSubType( "Car");
	//
	// Relation humanPossessVehicle = human.addRelation( "possess", vehicle);
	// final Relation manDriveCar = man.addSubRelation( humanPossessVehicle, "drive", car);
	//
	// humanPossessVehicle.enableCascadeRemove( 0);
	// assert manDriveCar.isCascadeRemove( 0);
	// manDriveCar.disableCascadeRemove( 0);
	// assert humanPossessVehicle.isCascadeRemove( 0);
	// assert !manDriveCar.isCascadeRemove( 0);
	// }

	public void isEnabledSystemPropertyTest() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type human = cache.addType("Human");
		Type vehicle = cache.addType("Vehicle");
		Relation humanOwnVehicle = human.setRelation("ownership", vehicle);
		// humanOwnVehicle.enableMultiDirectional();
		// assert humanOwnVehicle.isMultiDirectional();
		assert !humanOwnVehicle.isCascadeRemove(Statics.BASE_POSITION);
		humanOwnVehicle.enableCascadeRemove(0);
		// assert humanOwnVehicle.isMultiDirectional();
		assert humanOwnVehicle.isCascadeRemove(Statics.BASE_POSITION);
		// humanOwnVehicle.disableMultiDirectional();
		// assert cache.<Attribute> find(MultiDirectionalSystemProperty.class).isSingularConstraintEnabled();
		// assert !humanOwnVehicle.isMultiDirectional();
		assert humanOwnVehicle.isCascadeRemove(Statics.BASE_POSITION);
		humanOwnVehicle.disableCascadeRemove(0);
		// assert !humanOwnVehicle.isMultiDirectional();
		assert !humanOwnVehicle.isCascadeRemove(Statics.BASE_POSITION);
	}

	public void isEnabledSystemPropertyAxedTest() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type human = cache.addType("Human");
		Type vehicle = cache.addType("Vehicle");
		Relation humanOwnVehicle = human.setRelation("ownership", vehicle);
		humanOwnVehicle.enableCascadeRemove(0);
		assert humanOwnVehicle.isCascadeRemove(0);
		assert !humanOwnVehicle.isCascadeRemove(2);
		humanOwnVehicle.disableCascadeRemove(0);
		assert !humanOwnVehicle.isCascadeRemove(0);
		assert !humanOwnVehicle.isCascadeRemove(2);
	}

	public void isEnabledSystemPropertyAxedTest2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type human = cache.addType("Human");
		Type vehicle = cache.addType("Vehicle");
		Relation humanOwnVehicle = human.setRelation("ownership", vehicle);
		humanOwnVehicle.enableCascadeRemove(0);
		assert humanOwnVehicle.isCascadeRemove(0);
		humanOwnVehicle.enableCascadeRemove(1);
		assert humanOwnVehicle.isCascadeRemove(0);
		humanOwnVehicle.disableCascadeRemove(1);
		assert humanOwnVehicle.isCascadeRemove(0);
		assert !humanOwnVehicle.isCascadeRemove(1);
	}

	// TODO clean
	// public void testAskOnHimSelft() {
	// Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
	// ((GenericImpl) cache.find(CascadeRemoveSystemProperty.class)).isBooleanSystemPropertyEnabled(CascadeRemoveSystemProperty.class);
	// ((GenericImpl) cache.find(NoReferentialIntegritySystemProperty.class)).isBooleanSystemPropertyEnabled(NoReferentialIntegritySystemProperty.class);
	// }

	public void cascadeRemoveProperty() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type color = cache.addType("Color");
		Relation carColor = car.setRelation("carColor", color);
		carColor.enableCascadeRemove(Statics.TARGET_POSITION);

		Generic myBmw = car.newInstance("myBmw");
		Generic red = color.newInstance("red");
		Link myBmwRed = myBmw.setLink(carColor, "myBmwRed", red);

		myBmwRed.remove();
		cache.flush();

		assert !myBmwRed.isAlive();
		assert !red.isAlive();
		assert myBmw.isAlive();
		assert car.isAlive();
		assert color.isAlive();
		assert carColor.isAlive();
	}

	public void cascadeRemovePropertyDisabled() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type motor = cache.addType("Motor");
		Relation powered = car.setRelation("powered by", motor);

		Generic myBmw = car.newInstance("myBmw");
		Generic myMotor = motor.newInstance("myMotor");
		Link myBmwPoweredMyMotor = myBmw.setLink(powered, "myBmwPoweredMyMotor", myMotor);

		myBmwPoweredMyMotor.remove();

		assert !myBmwPoweredMyMotor.isAlive();
		assert myMotor.isAlive();
		assert myBmw.isAlive();
		assert car.isAlive();
		assert motor.isAlive();
		assert powered.isAlive();
	}

}
