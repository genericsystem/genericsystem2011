package org.genericsystem.impl;

import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.Supers;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.InstanceClassConstraint;
import org.genericsystem.api.annotation.constraints.NotNullConstraint;
import org.genericsystem.api.annotation.constraints.PropertyConstraint;
import org.genericsystem.api.annotation.constraints.SingularConstraint;
import org.genericsystem.api.annotation.constraints.SingularInstanceConstraint;
import org.genericsystem.api.annotation.constraints.UniqueConstraint;
import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.exception.ClassInstanceConstraintViolationException;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Type;
import org.genericsystem.api.generic.Holder;
import org.genericsystem.impl.constraints.InstanceClassConstraintImpl;
import org.genericsystem.impl.core.Statics;
import org.testng.annotations.Test;

@Test
public class ConstraintAnnotationsTest extends AbstractTest {

	public void instanceClass() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Unit.class, Car.class, ElectrikPower.class);
		final Attribute unit = cache.find(Unit.class);
		Type car = cache.find(Car.class);
		Attribute electrikPower = cache.find(ElectrikPower.class);
		final Generic myBMW = car.newInstance(cache, "myBMW");
		assert electrikPower.getBaseComponent() != null : electrikPower.info();
		final Holder electrikPowerMyBMW = myBMW.setValue(cache, electrikPower, 106);
		electrikPowerMyBMW.setValue(cache, unit, "Nm");
		assert unit.isSystemPropertyEnabled(cache, InstanceClassConstraintImpl.class);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				electrikPowerMyBMW.setValue(cache, unit, 27);
			}
		}.assertIsCausedBy(ClassInstanceConstraintViolationException.class);
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
	@NotNullConstraint
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
	@Supers(value = { GraphicComponent.class }, implicitSuper = GraphicComponent.class)
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
	@InstanceClassConstraint(String.class)
	public static class Unit {

	}

	@SystemGeneric
	// Everyone is unique !
	@UniqueConstraint
	public static class Human {
	}

	@SystemGeneric
	public static class Man extends Human {
	}

	@SystemGeneric
	// There can be only one time...
	@SingularInstanceConstraint
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
