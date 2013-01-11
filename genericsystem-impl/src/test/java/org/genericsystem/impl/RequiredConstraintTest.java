package org.genericsystem.impl;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.exception.RequiredConstraintViolationException;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Type;
import org.genericsystem.api.generic.Value;
import org.testng.annotations.Test;

@Test
public class RequiredConstraintTest extends AbstractTest {

	public void requiredAddedAndRemoved() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Generic myFiat = vehicle.newInstance(cache, "myFiat");
		Attribute wheel = vehicle.addAttribute(cache, "wheel");
		wheel.enableRequiredConstraint(cache);

		final Value wheelMyFiat = myFiat.addValue(cache, wheel, "BigWheel");
		cache.flush();
		wheelMyFiat.remove(cache);

		new RollbackCatcher() {

			@Override
			public void intercept() {
				cache.flush();
			}
		}.assertIsCausedBy(RequiredConstraintViolationException.class);
	}

	public void requiredNeverAdded() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		vehicle.newInstance(cache, "myFiat");
		vehicle.addAttribute(cache, "wheel").enableRequiredConstraint(cache);

		new RollbackCatcher() {

			@Override
			public void intercept() {
				cache.flush();
			}
		}.assertIsCausedBy(RequiredConstraintViolationException.class);
	}

	public void addOneRequired() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Generic myFiat = vehicle.newInstance(cache, "myFiat");

		Attribute vehicleWheel = vehicle.addAttribute(cache, "wheel").enableRequiredConstraint(cache);
		myFiat.addValue(cache, vehicleWheel, "BigWheel");

		cache.flush();
	}

	public void addSubOneRequired() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicleType = cache.newType("Vehicle");
		Generic vehicle = vehicleType.newInstance(cache, "myFiat");

		Attribute attribute = vehicleType.addAttribute(cache, "wheel");
		attribute.enableRequiredConstraint(cache);
		Attribute subAttribute = vehicleType.addSubAttribute(cache, attribute, "LittleWheel");
		vehicle.addValue(cache, subAttribute, "littlePinkWheel");
		cache.flush();
	}
}
