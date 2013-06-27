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
		human.newInstance(cache, "michael");
		final Cache cache2 = cache.getEngine().newCache().start();
		human.newInstance(cache2, "michael");
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
		car.setRelation(cache, "carColor", color);
		final Cache cache2 = cache.getEngine().newCache().start();
		car.setRelation(cache2, "carColor", color);
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
		Generic myCar = car.newInstance(cache, "myCar");
		Type color = cache.newType("Color");
		Generic red = color.newInstance(cache, "red");
		Relation carColor = car.setRelation(cache, "carColor", color);
		cache.flush();
		assert myCar.isAlive(cache);
		myCar.setLink(cache, carColor, "myCarRed", red);
		final Cache cache2 = cache.getEngine().newCache().start();
		myCar.setLink(cache2, carColor, "myCarRed", red);
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
