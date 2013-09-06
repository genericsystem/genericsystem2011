package org.genericsystem.impl;

import java.util.Iterator;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Snapshot;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.genericsystem.iterator.AbstractPostTreeIterator;
import org.genericsystem.iterator.AbstractPreTreeIterator;
import org.genericsystem.snapshot.AbstractSnapshot;
import org.testng.annotations.Test;

@Test
public class IteratorTest extends AbstractTest {
	
	public void testPostTree() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type truck = cache.newType("Truck");
		Type car = vehicle.newSubType( "Car");
		AbstractSnapshot<Generic> snapshot = new AbstractSnapshot<Generic>() {
			@Override
			public Iterator<Generic> iterator() {
				return new AbstractPostTreeIterator<Generic>(cache.getEngine()) {
					@Override
					protected Iterator<Generic> children(Generic node) {
						return ((GenericImpl) node).directInheritingsIterator();
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
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Snapshot<Generic> snapshot = new AbstractSnapshot<Generic>() {
			@Override
			public Iterator<Generic> iterator() {
				return new AbstractPreTreeIterator<Generic>(cache.getEngine()) {
					@Override
					public Iterator<Generic> children(Generic node) {
						return ((GenericImpl) node).directInheritingsIterator();
					}
				};
			}
		};
		assert snapshot.size() >= 2;
		assert snapshot.contains(car);
	}
	
	public void testPreTreeWithTypeAndSubType() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		car.newSubType( "bmw");
		car.newSubType( "audi");
		Type pilot = cache.newType("Pilot");
		pilot.newSubType( "pilot1");
		pilot.newSubType( "pilot2");
		Snapshot<Generic> snapshot = new AbstractSnapshot<Generic>() {
			@Override
			public Iterator<Generic> iterator() {
				return new AbstractPreTreeIterator<Generic>(cache.getEngine()) {
					@Override
					public Iterator<Generic> children(Generic node) {
						return ((GenericImpl) node).directInheritingsIterator();
					}
				};
			}
		};
		assert snapshot.size() >= 7;
		assert snapshot.contains(car);
	}
	
	public void testConcreteSnapshot() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type human = cache.newType("Human");
		Relation humanDriveCar = human.setRelation( "drive", car);
		Generic audi = car.newInstance( "audi");
		Generic myck = human.newInstance( "myck");
		Link link = myck.setLink( humanDriveCar, "myckAudi", audi);
		Snapshot<Link> snapshot = myck.getLinks( humanDriveCar);
		assert snapshot.size() == 1 : snapshot;
		assert snapshot.contains(link);
	}
	
}
