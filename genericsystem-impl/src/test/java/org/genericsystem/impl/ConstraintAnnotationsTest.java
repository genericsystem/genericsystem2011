package org.genericsystem.impl;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.InstanceValueClassConstraint;
import org.genericsystem.annotation.constraints.PropertyConstraint;
import org.genericsystem.annotation.constraints.SingletonConstraint;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.constraints.UniqueValueConstraint;
import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Statics;
import org.genericsystem.exception.InstanceClassConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class ConstraintAnnotationsTest extends AbstractTest {

	public void instanceClass() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Unit.class, Car.class, ElectrikPower.class).start();
		final Attribute unit = cache.find(Unit.class);
		assert String.class.equals(unit.getConstraintClass());
		Type car = cache.find(Car.class);
		Attribute electrikPower = cache.find(ElectrikPower.class);
		final Generic myBMW = car.addInstance("myBMW");
		assert electrikPower.getBaseComponent() != null : electrikPower.info();
		final Holder electrikPowerMyBMW = myBMW.setValue(electrikPower, 106);
		electrikPowerMyBMW.setValue(unit, "Nm");
		new RollbackCatcher() {
			@Override
			public void intercept() {
				electrikPowerMyBMW.setValue(unit, 27);
			}
		}.assertIsCausedBy(InstanceClassConstraintViolationException.class);
	}

	@SystemGeneric
	public static class Objet {

	}

	public static class Mamifere extends Objet {

	}

	@SystemGeneric
	public static class Sorcier extends Mamifere {

	}

	@SystemGeneric
	public static class GraphicComponent {

	}

	@SystemGeneric
	@Components(GraphicComponent.class)
	public static class Size {

	}

	@SystemGeneric
	public static class Window extends GraphicComponent {

	}

	@SystemGeneric
	@PropertyConstraint
	@Extends(GraphicComponent.class)
	public static class Selectable {

	}

	@SystemGeneric
	@Components(Selectable.class)
	public static class Selected {

	}

	@SystemGeneric
	public static class Vehicle {

	}

	@SystemGeneric
	@Components(Vehicle.class)
	public static class Power {

	}

	@SystemGeneric
	public static class Car extends Vehicle {

	}

	@SystemGeneric
	@Components(Car.class)
	public static class ElectrikPower extends Power {

	}

	@SystemGeneric
	@Components(ElectrikPower.class)
	@InstanceValueClassConstraint(String.class)
	public static class Unit {

	}

	@SystemGeneric
	// Everyone is unique !
	@UniqueValueConstraint
	public static class Human {
	}

	@SystemGeneric
	public static class Man extends Human {
	}

	@SystemGeneric
	// There can be only one time...
	@SingletonConstraint
	public static class Time {
	}

	@SystemGeneric
	@Components({ Human.class, Vehicle.class })
	@SingularConstraint(Statics.TARGET_POSITION)
	public static class HumanPossessVehicle {
	}

	@SystemGeneric
	@Components({ Human.class, Car.class })
	public static class HumanPossessCar extends HumanPossessVehicle {
	}

	@SystemGeneric
	@Components({ Man.class, Car.class })
	@SingularConstraint(Statics.BASE_POSITION)
	public static class ManPossessCar extends HumanPossessVehicle {
	}

	@SystemGeneric
	@Components({ Human.class, Vehicle.class, Time.class })
	public static class HumanPossessVehicleTime {
	}
}
