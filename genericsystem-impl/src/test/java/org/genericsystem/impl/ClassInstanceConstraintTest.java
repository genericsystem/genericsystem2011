package org.genericsystem.impl;

import java.io.Serializable;
import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.exception.ClassInstanceConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Type;
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
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Wheel.class);
		Type vehicle = cache.newType("Vehicle");
		Generic myFiat = vehicle.newInstance(cache, "myFiat");
		Attribute wheelVehcile = vehicle.setAttribute(cache, "wheel");
		wheelVehcile.setConstraintClass(cache, Wheel.class);
		assert wheelVehcile.getConstraintClass(cache).equals(Wheel.class) : wheelVehcile.getConstraintClass(cache);
		myFiat.setValue(cache, wheelVehcile, new Wheel("bigWheel"));
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
		}.assertIsCausedBy(ClassInstanceConstraintViolationException.class);
	}
	
}
