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
		cache.newType("Human");
		final Cache cache2 = cache.getEngine().newCache().start();
		cache2.newType("Human");
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
		Type human = cache.newType("Human");
		cache.flush();
		human.newInstance("michael");
		final Cache cache2 = cache.getEngine().newCache().start();
		human.newInstance("michael");
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
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
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
		Type car = cache.newType("Car");
		Generic myCar = car.newInstance("myCar");
		Type color = cache.newType("Color");
		Generic red = color.newInstance("red");
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
