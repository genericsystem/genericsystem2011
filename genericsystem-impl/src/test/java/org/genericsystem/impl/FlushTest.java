package org.genericsystem.impl;

import java.util.Arrays;
import java.util.Objects;

import org.genericsystem.core.Cache;
import org.genericsystem.core.CacheImpl;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Snapshot;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class FlushTest extends AbstractTest {

	@Test
	public void testFlush() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Engine engine = cache.getEngine();
		Type human = cache.addType("Human");
		cache.flush();
		engine.close();

		cache = engine.newCache();
		assert engine.getInheritings().contains(human);
	}

	@Test
	public void testNoFlush() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Engine engine = cache.getEngine();
		Type animal = cache.addType("Animal");
		Snapshot<Generic> snapshot = animal.getInheritings();
		assert snapshot.isEmpty();
		Type human = animal.addSubType("Human");
		assert snapshot.size() == 1;
		assert snapshot.contains(human);

		engine.close();

		cache = engine.newCache().start();
		assert engine.getInheritings().filter(new Filter<Generic>() {

			@Override
			public boolean isSelected(Generic element) {
				return Objects.equals(element.getValue(), "Animal");
			}
		}).isEmpty();
	}

	@Test
	public void testPartialFlush() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Engine engine = cache.getEngine();
		Type human = cache.addType("Human");
		cache.flush();
		Type car = cache.addType("Car");
		engine.close();

		cache = engine.newCache().start();
		Snapshot<Generic> snapshot = engine.getInheritings();
		assert snapshot.contains(human) : snapshot;
		assert !snapshot.contains(car) : snapshot;
	}

	@Test
	public void testMultipleCache() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Engine engine = cache.getEngine();
		Type human = cache.addType("Human");
		cache.flush();
		Snapshot<Generic> snapshot = engine.getInheritings();
		assert snapshot.contains(human) : snapshot;
		// cache.deactivate();

		cache = engine.newCache().start();
		Type car = cache.addType("Car");
		cache.flush();
		engine.close();

		cache = engine.newCache().start();
		snapshot = engine.getInheritings();
		assert snapshot.containsAll(Arrays.asList(human, car)) : snapshot;
	}

	@Test
	public void testMultipleCache2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Engine engine = cache.getEngine();
		Type human = cache.addType("Human");
		cache.flush();
		Snapshot<Generic> snapshot = engine.getInheritings();
		assert snapshot.contains(human) : snapshot;

		cache = engine.newCache().start();
		Type car = cache.addType("Car");
		cache.flush();

		cache = engine.newCache();
		snapshot = engine.getInheritings();
		assert snapshot.containsAll(Arrays.asList(human, car)) : snapshot;

		engine.close();
	}

	@Test
	public void testMultipleCache3() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Engine engine = cache.getEngine();
		Type vehicle = cache.addType("Vehicle");

		Cache cache2 = engine.newCache().start();
		assert cache2.getType("Vehicle") == null;

		cache.start().flush();

		cache2.start();
		((CacheImpl) cache2).pickNewTs();
		assert cache2.getType("Vehicle").equals(vehicle);
	}

	public void testAutomatics() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type car = cache.addType("Car");
		Type color = cache.addType("Color");
		Relation carColor = car.setRelation("CarColor", color);
		carColor.enableSingularConstraint();

		Generic red = color.addInstance("Red");
		Generic grey = color.addInstance("Grey");
		assert car.setLink(carColor, "CarRed", red).isSingularConstraintEnabled(0); // default color of car

		final Generic bmw = car.addInstance("Bmw");
		Generic mercedes = car.addInstance("Mercedes");
		mercedes.bind(carColor, grey);
		assert red.getLinks(carColor).size() == 1;
		assert red.getLink(carColor).getBaseComponent().equals(bmw);
	}

	public void testAutomaticsNotFlushedOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type car = cache.addType("Car");
		Type color = cache.addType("Color");

		Relation carColor = car.setRelation("CarColor", color);
		carColor.enableSingularConstraint();

		Attribute intensity = carColor.setAttribute("Intensity");

		Generic red = color.addInstance("red");
		Generic grey = color.addInstance("grey");

		Link defaultCarColor = car.setLink(carColor, "carRed", red); // default color of car

		final Generic bmw = car.addInstance("bmw");
		Generic mercedes = car.addInstance("mercedes");
		final Generic audi = car.addInstance("audi");

		mercedes.bind(carColor, grey);

		Link link = red.getLink(carColor, audi);

		link.setValue(intensity, "60%");

		/* Link beetween audi and color is not the same as link between Car and color */
		assert !Objects.equals(audi.getLink(carColor, red), defaultCarColor);

		/* There are two links: bmw <-> Red; audi <-> Red */
		assert red.getLinks(carColor).size() == 2;

		Snapshot<Link> links = red.getLinks(carColor);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Link redToBMW = links.filter(new Filter() {

			@Override
			public boolean isSelected(Object element) {
				return ((Link) element).getComponents().contains(bmw);
			}
		}).get(0);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Link redToLada = links.filter(new Filter() {

			@Override
			public boolean isSelected(Object element) {
				return ((Link) element).getComponents().contains(audi);
			}
		}).get(0);

		/* Automatic link from red to bmw exists */
		assert redToBMW != null;

		/* Link from red to bmw is automatic */
		assert ((CacheImpl) cache).isAutomatic(redToBMW);

		/* Link from red to bmw is not flushable */
		// assert !((GenericImpl) redToBMW).isFlushable();

		/* Automatic link from red to lada exists */
		assert redToLada != null;

		/* Link from red to lada is automatic */
		assert !((CacheImpl) cache).isAutomatic(redToLada);

		/* Link from red to lada is flushable */
		// assert ((GenericImpl) redToLada).isFlushable();

		cache.flush();
		Cache cache2 = cache.getEngine().newCache().start();

		/* Cache 2 contains our types */
		assert cache2.getAllTypes().contains(color);

		Relation carColor2 = cache2.getType("Car").getRelation("CarColor");
		Link defColor = carColor2.getInstance("carRed");

		/* Automatic link between audi and red was restored from cache */
		assert cache2.getType("Car").getInstance("audi").getLinks(carColor2).size() == 1;
		assert !cache2.getType("Car").getInstance("audi").getLinks(carColor2).contains(defColor);

		/* Automatic link between bmw and red color was not restored from cache */
		assert cache2.getType("Car").getInstance("bmw").getLinks(carColor2).size() == 1;
		assert cache2.getType("Car").getInstance("bmw").getLinks(carColor2).contains(defColor);
	}
}
