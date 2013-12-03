package org.genericsystem.impl;

import java.io.Serializable;

import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.exception.InstanceClassConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class ClassInstanceConstraintTest extends AbstractTest {

	@SystemGeneric
	public static class Wheel implements Serializable {

		private static final long serialVersionUID = 3631170157826372130L;
	}

	@SystemGeneric
	public static class Vehicle implements Serializable {

		private static final long serialVersionUID = 979677737037270498L;
	}

	public void simpleAttributeValueOK() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Wheel.class).start();
		Type vehicle = cache.addType("Vehicle");
		Generic myFiat = vehicle.addInstance("myFiat");
		Attribute wheelVehcile = vehicle.setAttribute("wheel");
		wheelVehcile.setConstraintClass(Wheel.class);
		assert wheelVehcile.getConstraintClass().equals(Wheel.class) : wheelVehcile.getConstraintClass();
		myFiat.setValue(wheelVehcile, new Wheel());
	}

	public void simpleAttributeValueKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Wheel.class).start();
		Type vehicle = cache.addType("Vehicle");
		Attribute wheelVehcile = vehicle.setAttribute("wheel");
		assert wheelVehcile.getConstraintClass().equals(Object.class) : wheelVehcile.getConstraintClass();
	}

	public void simpleAttributeValueKO2() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Wheel.class).start();
		Type vehicle = cache.addType("Vehicle");
		final Generic myFiat = vehicle.addInstance("myFiat");
		final Attribute wheelVehcile = vehicle.setAttribute("wheel");
		wheelVehcile.setConstraintClass(Wheel.class);
		assert Wheel.class.equals(wheelVehcile.getConstraintClass());

		new RollbackCatcher() {
			@Override
			public void intercept() {
				myFiat.setValue(wheelVehcile, 23);
			}
		}.assertIsCausedBy(InstanceClassConstraintViolationException.class);
	}

}
