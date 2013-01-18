package org.genericsystem.impl;

import java.util.Iterator;
import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.generic.Link;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Type;
import org.genericsystem.impl.core.GenericImpl;
import org.genericsystem.impl.iterator.AbstractPostTreeIterator;
import org.genericsystem.impl.iterator.AbstractPreTreeIterator;
import org.genericsystem.impl.snapshot.AbstractSnapshot;
import org.testng.annotations.Test;

@Test
public class IteratorTest extends AbstractTest {
	
	public void testPostTree() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type truck = cache.newType("Truck");
		Type car = vehicle.newSubType(cache, "Car");
		AbstractSnapshot<Generic> snapshot = new AbstractSnapshot<Generic>() {
			@Override
			public Iterator<Generic> iterator() {
				return new AbstractPostTreeIterator<Generic>(cache.getEngine()) {
					@Override
					protected Iterator<Generic> children(Generic node) {
						return ((GenericImpl) node).directInheritingsIterator(cache);
					}
				};
				
			}
		};
		assert snapshot.size() >= 4;
		assert snapshot.contains(car);
		assert snapshot.contains(vehicle);
		assert snapshot.contains(truck);
	}
	
	public void testPreTree() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Snapshot<Generic> snapshot = new AbstractSnapshot<Generic>() {
			@Override
			public Iterator<Generic> iterator() {
				return new AbstractPreTreeIterator<Generic>(cache.getEngine()) {
					@Override
					public Iterator<Generic> children(Generic node) {
						return ((GenericImpl) node).directInheritingsIterator(cache);
					}
				};
			}
		};
		assert snapshot.size() >= 2;
		assert snapshot.contains(car);
	}
	
	public void testPreTreeWithTypeAndSubType() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		car.newSubType(cache, "bmw");
		car.newSubType(cache, "audi");
		Type pilot = cache.newType("Pilot");
		pilot.newSubType(cache, "pilot1");
		pilot.newSubType(cache, "pilot2");
		Snapshot<Generic> snapshot = new AbstractSnapshot<Generic>() {
			@Override
			public Iterator<Generic> iterator() {
				return new AbstractPreTreeIterator<Generic>(cache.getEngine()) {
					@Override
					public Iterator<Generic> children(Generic node) {
						return ((GenericImpl) node).directInheritingsIterator(cache);
					}
				};
			}
		};
		assert snapshot.size() >= 7;
		assert snapshot.contains(car);
	}
	
	public void testConcreteSnapshot() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type human = cache.newType("Human");
		Relation humanDriveCar = human.addRelation(cache, "drive", car);
		Generic audi = car.newInstance(cache, "audi");
		Generic myck = human.newInstance(cache, "myck");
		Link link = myck.setLink(cache, humanDriveCar, "myckAudi", audi);
		Snapshot<Link> snapshot = myck.getLinks(cache, humanDriveCar);
		assert snapshot.size() == 1 : snapshot;
		assert snapshot.contains(link);
	}
	
}
