package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.CacheImpl;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Snapshot;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.core.Statics;
import org.genericsystem.core.Transaction;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Node;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Tree;
import org.genericsystem.generic.Type;
import org.genericsystem.systemproperties.MultiDirectionalSystemProperty;
import org.testng.annotations.Test;

@Test
public class AutomaticTest extends AbstractTest {

	public void test() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		assert !((GenericImpl) cache.getEngine()).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(cache.getEngine());
		assert !((GenericImpl) cache.getEngine().getMetaAttribute()).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(cache.getEngine());
		assert !((GenericImpl) cache.getEngine().getMetaRelation()).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(cache.getEngine());
		Generic multiDirectional = cache.find(MultiDirectionalSystemProperty.class);
		assert !((GenericImpl) multiDirectional).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(multiDirectional);
		assert ((CacheImpl) cache).isFlushable(multiDirectional.getImplicit());
	}

	public void testAttribut() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute(cache, "power");

		assert !((GenericImpl) vehicle).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(vehicle);
		assert !((GenericImpl) vehiclePower).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(vehiclePower);
		assert ((GenericImpl) vehiclePower.getImplicit()).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(vehiclePower.getImplicit());
	}

	public void testRemoveAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute(cache, "power");
		Generic power = vehiclePower.getImplicit();

		assert !((GenericImpl) vehicle).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(vehicle);
		assert !((GenericImpl) vehiclePower).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(vehiclePower);
		assert ((GenericImpl) power).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(power);

		vehiclePower.remove(cache);

		assert ((GenericImpl) power).isAutomatic();
		assert !((CacheImpl) cache).isFlushable(power);
	}

	public void testSubType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "car");

		assert !((GenericImpl) vehicle).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(vehicle);
		assert !((GenericImpl) car).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(car);
		assert ((GenericImpl) car.getImplicit()).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(car.getImplicit());
	}

	public void testMutipleInheritance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type graphicComponent = cache.newType("graphicComponent");
		Type window = graphicComponent.newSubType(cache, "Window");
		Type selectable = graphicComponent.newSubType(cache, "Selectable");
		Type selectableWindow = cache.newSubType("selectableWindow", selectable, window);

		assert !((GenericImpl) graphicComponent).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(graphicComponent);
		assert !((GenericImpl) window).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(window);
		assert ((GenericImpl) window.getImplicit()).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(window.getImplicit());
		assert !((GenericImpl) selectable).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(selectable);
		assert ((GenericImpl) selectable.getImplicit()).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(selectable.getImplicit());
		assert !((GenericImpl) selectableWindow).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(selectableWindow);
		assert ((GenericImpl) selectableWindow.getImplicit()).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(selectableWindow.getImplicit());
	}

	public void testAttributWithValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		Attribute vehiclePower = vehicle.setAttribute(cache, "power");
		Holder myVehicle123 = myVehicle.setValue(cache, vehiclePower, "123");

		assert !((GenericImpl) vehicle).isAutomatic();
		assert !((GenericImpl) vehiclePower).isAutomatic();
		assert ((GenericImpl) vehiclePower.getImplicit()).isAutomatic();

		assert !((GenericImpl) myVehicle).isAutomatic();
		assert !((GenericImpl) myVehicle123).isAutomatic();
		assert ((GenericImpl) myVehicle123.getImplicit()).isAutomatic();
	}

	public void testRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation(cache, "CarColor", color);

		assert !((GenericImpl) carColor).isAutomatic();
		assert ((GenericImpl) carColor.getImplicit()).isAutomatic();
	}

	public void testRelationWithLink() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Generic myCar = car.newInstance(cache, "myCar");
		Type color = cache.newType("Color");
		Generic red = color.newInstance(cache, "red");
		Relation carColor = car.setRelation(cache, "CarColor", color);
		Link myCarRed = myCar.bind(cache, carColor, red);

		assert !((GenericImpl) myCarRed).isAutomatic();
		assert ((GenericImpl) myCarRed.getImplicit()).isAutomatic();
	}

	public void testRelationWithDefaultLink() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		car.newInstance(cache, "myCar");
		Type color = cache.newType("Color");
		Generic red = color.newInstance(cache, "red");
		Relation carColor = car.setRelation(cache, "CarColor", color);
		Link carRed = car.bind(cache, carColor, red);

		assert !((GenericImpl) carRed).isAutomatic();
		assert ((GenericImpl) carRed.getImplicit()).isAutomatic();
	}

	public void testTree() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Tree tree = cache.newTree("tree");
		Node root = tree.newRoot(cache, "root");
		Node children = root.addNode(cache, "children");
		assert !((GenericImpl) tree).isAutomatic();
		assert ((GenericImpl) tree.getImplicit()).isAutomatic();
		assert !((GenericImpl) root).isAutomatic();
		assert ((GenericImpl) root.getImplicit()).isAutomatic();
		assert !((GenericImpl) children).isAutomatic();
		assert ((GenericImpl) children.getImplicit()).isAutomatic();
	}

	public void deduct() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation(cache, "CarColor", color);
		Generic myAudi = car.newInstance(cache, "myAudi");
		Generic red = color.newInstance(cache, "red");
		car.setLink(cache, carColor, "carRed", red);

		assert myAudi.getTargets(cache, carColor).size() == 1;
		assert myAudi.getTargets(cache, carColor).contains(red);
		assert red.getTargets(cache, carColor, Statics.BASE_POSITION).size() == 1;
		assert red.getTargets(cache, carColor, Statics.BASE_POSITION).contains(myAudi) : red.getTargets(cache, carColor, Statics.BASE_POSITION);
	}

	public void ternaryDeduct() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Type time = cache.newType("Time");
		Relation carColorTime = car.setRelation(cache, "CarColorTime", color, time);
		car.newInstance(cache, "myAudi");
		Generic red = color.newInstance(cache, "red");
		Generic today = time.newInstance(cache, "today");
		Generic tomorrow = time.newInstance(cache, "tomorrow");
		Link carRed = car.setLink(cache, carColorTime, "carRed", red, time);
		Snapshot<Link> links = red.getLinks(cache, carColorTime);
		assert !links.contains(carRed) : links;
		assert links.filter(new Filter<Link>() {

			@Override
			public boolean isSelected(Link element) {
				assert !((CacheImpl) cache).isFlushable(element);
				return !((GenericImpl) element).isAutomatic();
			}
		}).isEmpty();

		Generic myBmw = car.newInstance(cache, "myBmw");
		Link myBmwRedToday = myBmw.setLink(cache, carColorTime, "carRed", red, today);
		Link myBmwRedTomorrow = myBmw.setLink(cache, carColorTime, "carRed", red, tomorrow);
		Snapshot<Link> filter = red.getLinks(cache, carColorTime).filter(new Filter<Link>() {

			@Override
			public boolean isSelected(Link element) {
				return !((GenericImpl) element).isAutomatic();
			}
		});
		assert filter.size() == 2 : filter;
		assert filter.contains(myBmwRedToday);
		assert filter.contains(myBmwRedTomorrow);
		assert ((CacheImpl) cache).isFlushable(myBmwRedToday);
		assert ((CacheImpl) cache).isFlushable(myBmwRedTomorrow);
	}

	public void ternaryFlush() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Type time = cache.newType("Time");
		Relation carColorTime = car.setRelation(cache, "CarColorTime", color, time);
		Generic myAudi = car.newInstance(cache, "myAudi");
		Generic red = color.newInstance(cache, "red");
		time.newInstance(cache, "today");
		time.newInstance(cache, "tomorrow");
		car.setLink(cache, carColorTime, "carRed", red, time);

		assert myAudi.getTargets(cache, carColorTime).contains(red);
		assert red.getTargets(cache, carColorTime, Statics.BASE_POSITION).contains(myAudi) : red.getTargets(cache, carColorTime, Statics.BASE_POSITION);

		// assert myAudi.getComposites(cache).isEmpty() : myAudi.getComposites(cache);

		cache.flush();

		assert red.getLinks(new Transaction(cache.getEngine()), carColorTime).filter(new Filter<Link>() {
			@Override
			public boolean isSelected(Link element) {
				return ((GenericImpl) element).isAutomatic();
			}
		}).isEmpty();
		assert red.getTargets(cache.getEngine().newCache(), carColorTime, Statics.BASE_POSITION).contains(myAudi) : red.getTargets(new Transaction(cache.getEngine()), carColorTime, Statics.BASE_POSITION);
		assert myAudi.getTargets(cache, carColorTime).contains(red);
		assert red.getTargets(cache, carColorTime, Statics.BASE_POSITION).contains(myAudi) : red.getTargets(cache, carColorTime, Statics.BASE_POSITION);
	}

	public void test2() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation(cache, "CarColor", color);
		Generic myAudi = car.newInstance(cache, "myAudi");
		Generic red = color.newInstance(cache, "red");
		carColor.enableRequiredConstraint(cache);
	}
}
