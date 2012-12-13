package org.genericsystem.impl;

import java.io.Serializable;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.exception.ClassInstanceConstraintViolationException;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Type;
import org.testng.annotations.Test;

@Test
public class ClassInstanceConstraintTest extends AbstractTest {

	public static class Wheel implements Serializable {
		private static final long serialVersionUID = -3996193478981218732L;
		private String name;

		public Wheel(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public static class Vehicle implements Serializable {
		private static final long serialVersionUID = -3996193478981218732L;
		private String name;

		public Vehicle(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public void simpleAttributeValueOK() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Generic myFiat = vehicle.newInstance(cache, "myFiat");
		Attribute wheelVehcile = vehicle.addAttribute(cache, "wheel");
		wheelVehcile.setConstraintClass(cache, Wheel.class);
		myFiat.addValue(cache, wheelVehcile, new Wheel("bigWheel"));
	}

	public void simpleAttributeValueKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		final Generic myFiat = vehicle.newInstance(cache, "myFiat");
		final Attribute wheelVehcile = vehicle.addAttribute(cache, "wheel");
		wheelVehcile.setConstraintClass(cache, Wheel.class);

		new RollbackCatcher() {
			@Override
			public void intercept() {
				myFiat.addValue(cache, wheelVehcile, 23);
			}
		}.assertIsCausedBy(ClassInstanceConstraintViolationException.class);
	}

	public void simpleTypeInstanceOK() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		vehicle.setConstraintClass(cache, Vehicle.class);
		vehicle.newInstance(cache, new Vehicle("myVehicle"));
	}

	public void simpleTypeInstanceKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		final Type vehicle = cache.newType("Vehicle");
		vehicle.setConstraintClass(cache, Vehicle.class);

		new RollbackCatcher() {
			@Override
			public void intercept() {
				vehicle.newInstance(cache, "myVehicle");
			}
		}.assertIsCausedBy(ClassInstanceConstraintViolationException.class);
	}

}
