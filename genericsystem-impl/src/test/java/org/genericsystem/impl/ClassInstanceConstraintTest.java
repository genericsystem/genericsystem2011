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
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Wheel.class);
		Type vehicle = cache.newType("Vehicle");
		Generic myFiat = vehicle.newInstance(cache, "myFiat");
		Attribute wheelVehcile = vehicle.setAttribute(cache, "wheel");
		wheelVehcile.setConstraintClass(cache, Wheel.class);
		assert wheelVehcile.getConstraintClass(cache).equals(Wheel.class) : wheelVehcile.getConstraintClass(cache);
		myFiat.setValue(cache, wheelVehcile, new Wheel());
	}

	public void simpleAttributeValueKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Wheel.class);
		Type vehicle = cache.newType("Vehicle");
		Attribute wheelVehcile = vehicle.setAttribute(cache, "wheel");
		assert wheelVehcile.getConstraintClass(cache).equals(Object.class) : wheelVehcile.getConstraintClass(cache);
	}

	public void simpleAttributeValueKO2() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Wheel.class);
		Type vehicle = cache.newType("Vehicle");
		final Generic myFiat = vehicle.newInstance(cache, "myFiat");
		final Attribute wheelVehcile = vehicle.setAttribute(cache, "wheel");
		wheelVehcile.setConstraintClass(cache, Wheel.class);
		assert Wheel.class.equals(wheelVehcile.getConstraintClass(cache));

		new RollbackCatcher() {
			@Override
			public void intercept() {
				myFiat.setValue(cache, wheelVehcile, 23);
			}
		}.assertIsCausedBy(InstanceClassConstraintViolationException.class);
	}

}
