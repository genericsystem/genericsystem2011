package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.exception.UnduplicateBindingConstraintViolationException;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class UnduplicateBindingConstraintTest extends AbstractTest {

	public void testType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		cache.addType("Human");
		final Cache cache2 = cache.getEngine().newCache().start();
		cache2.addType("Human");
		cache.start();
		cache.flush();
		new RollbackCatcher() {

			@Override
			public void intercept() {
				cache2.start();
				cache2.flush();
			}

		}.assertIsCausedBy(UnduplicateBindingConstraintViolationException.class);
	}

	public void testInstance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type human = cache.addType("Human");
		cache.flush();
		human.addInstance("michael");
		final Cache cache2 = cache.getEngine().newCache().start();
		human.addInstance("michael");
		cache.start();
		cache.flush();
		new RollbackCatcher() {

			@Override
			public void intercept() {
				cache2.start();
				cache2.flush();
			}

		}.assertIsCausedBy(UnduplicateBindingConstraintViolationException.class);
	}

	public void testRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type color = cache.addType("Color");
		cache.flush();
		car.setRelation("carColor", color);
		final Cache cache2 = cache.getEngine().newCache().start();
		car.setRelation("carColor", color);
		cache.start();
		cache.flush();
		new RollbackCatcher() {

			@Override
			public void intercept() {
				cache2.start();
				cache2.flush();
			}

		}.assertIsCausedBy(UnduplicateBindingConstraintViolationException.class);
	}

	public void testLink() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Generic myCar = car.addInstance("myCar");
		Type color = cache.addType("Color");
		Generic red = color.addInstance("red");
		Relation carColor = car.setRelation("carColor", color);
		cache.flush();
		assert myCar.isAlive();
		myCar.setLink(carColor, "myCarRed", red);
		final Cache cache2 = cache.getEngine().newCache().start();
		myCar.setLink(carColor, "myCarRed", red);
		cache.start();
		cache.flush();
		new RollbackCatcher() {

			@Override
			public void intercept() {
				cache2.start();
				cache2.flush();
			}

		}.assertIsCausedBy(UnduplicateBindingConstraintViolationException.class);
	}
}
