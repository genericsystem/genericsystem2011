package org.genericsystem.impl;

import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.core.Cache;
import org.genericsystem.core.CacheImpl;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Snapshot;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Node;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Tree;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class AutomaticTest extends AbstractTest {

	public void test() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		// assert !((GenericImpl) cache.getEngine()).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(cache.getEngine());
		// assert !((GenericImpl) cache.getEngine().getMetaAttribute()).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(cache.getEngine());
		// assert !((GenericImpl) cache.getEngine().getMetaRelation()).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(cache.getEngine());
	}

	public void testAttribut() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute("power");

		assert !((GenericImpl) vehicle).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(vehicle);
		assert !((GenericImpl) vehiclePower).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(vehiclePower);
		assert ((GenericImpl) vehiclePower.getImplicit()).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(vehiclePower.getImplicit());
	}

	public void testRemoveAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute("power");
		Generic power = vehiclePower.getImplicit();

		assert !((GenericImpl) vehicle).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(vehicle);
		assert !((GenericImpl) vehiclePower).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(vehiclePower);
		assert ((GenericImpl) power).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(power);

		vehiclePower.remove();

		assert !power.isAlive();
	}

	public void testSubType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType("car");

		assert !((GenericImpl) vehicle).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(vehicle);
		assert !((GenericImpl) car).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(car);
		assert ((GenericImpl) car.getImplicit()).isAutomatic();
		assert ((CacheImpl) cache).isFlushable(car.getImplicit());
	}

	public void testMutipleInheritance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type graphicComponent = cache.newType("graphicComponent");
		Type window = graphicComponent.newSubType("Window");
		Type selectable = graphicComponent.newSubType("Selectable");
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
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Generic myVehicle = vehicle.newInstance("myVehicle");
		Attribute vehiclePower = vehicle.setAttribute("power");
		Holder myVehicle123 = myVehicle.setValue(vehiclePower, "123");

		assert !((GenericImpl) vehicle).isAutomatic();
		assert !((GenericImpl) vehiclePower).isAutomatic();
		assert ((GenericImpl) vehiclePower.getImplicit()).isAutomatic();

		assert !((GenericImpl) myVehicle).isAutomatic();
		assert !((GenericImpl) myVehicle123).isAutomatic();
		assert ((GenericImpl) myVehicle123.getImplicit()).isAutomatic();
	}

	public void testRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation("CarColor", color);

		assert !((GenericImpl) carColor).isAutomatic();
		assert ((GenericImpl) carColor.getImplicit()).isAutomatic();
	}

	public void testRelationWithLink() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Generic myCar = car.newInstance("myCar");
		Type color = cache.newType("Color");
		Generic red = color.newInstance("red");
		Relation carColor = car.setRelation("CarColor", color);
		Link myCarRed = myCar.bind(carColor, red);

		assert !((GenericImpl) myCarRed).isAutomatic();
		assert ((GenericImpl) myCarRed.getImplicit()).isAutomatic();
	}

	public void testRelationWithDefaultLink() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		car.newInstance("myCar");
		Type color = cache.newType("Color");
		Generic red = color.newInstance("red");
		Relation carColor = car.setRelation("CarColor", color);
		Link carRed = car.bind(carColor, red);

		assert !((GenericImpl) carRed).isAutomatic();
		assert ((GenericImpl) carRed.getImplicit()).isAutomatic();
	}

	public void testTree() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Tree tree = cache.newTree("tree");
		Node root = tree.newRoot("root");
		Node children = root.setNode("children");
		assert !((GenericImpl) tree).isAutomatic();
		assert ((GenericImpl) tree.getImplicit()).isAutomatic();
		assert !((GenericImpl) root).isAutomatic();
		assert ((GenericImpl) root.getImplicit()).isAutomatic();
		assert !((GenericImpl) children).isAutomatic();
		assert ((GenericImpl) children.getImplicit()).isAutomatic();
	}

	public void deduct() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation("CarColor", color);
		Generic myAudi = car.newInstance("myAudi");
		Generic red = color.newInstance("red");
		car.setLink(carColor, "carRed", red);

		assert myAudi.getTargets(carColor).size() == 1;
		assert myAudi.getTargets(carColor).contains(red);
		assert red.getTargets(carColor, 1, 0).size() == 1;
		assert red.getTargets(carColor, 1, 0).contains(myAudi) : red.getTargets(carColor);
	}

	public void ternaryDeduct() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Type time = cache.newType("Time");
		Relation carColorTime = car.setRelation("CarColorTime", color, time);
		car.newInstance("myAudi");
		Generic red = color.newInstance("red");
		Generic today = time.newInstance("today");
		Generic tomorrow = time.newInstance("tomorrow");
		Link carRed = car.setLink(carColorTime, "carRed", red, time);
		Snapshot<Link> links = red.getLinks(carColorTime);
		assert !links.contains(carRed) : links;
		assert links.filter(new Filter<Link>() {

			@Override
			public boolean isSelected(Link element) {
				assert !((CacheImpl) cache).isFlushable(element);
				return !((GenericImpl) element).isAutomatic();
			}
		}).isEmpty();

		Generic myBmw = car.newInstance("myBmw");
		Link myBmwRedToday = myBmw.setLink(carColorTime, "carRed", red, today);
		Link myBmwRedTomorrow = myBmw.setLink(carColorTime, "carRed", red, tomorrow);
		Snapshot<Link> filter = red.getLinks(carColorTime).filter(new Filter<Link>() {

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
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Type time = cache.newType("Time");
		Relation carColorTime = car.setRelation("CarColorTime", color, time);
		Generic myAudi = car.newInstance("myAudi");
		Generic red = color.newInstance("red");
		time.newInstance("today");
		time.newInstance("tomorrow");
		car.setLink(carColorTime, "carRed", red, time);

		assert myAudi.getTargets(carColorTime).contains(red);
		assert red.getTargets(carColorTime, 1, 0).contains(myAudi) : red.getTargets(carColorTime);

		// assert myAudi.getComposites().isEmpty() : myAudi.getComposites();

		cache.flush();

		// assert red.getLinks(cache.getEngine().newCache().start(), carColorTime).filter(new Filter<Link>() {
		// @Override
		// public boolean isSelected(Link element) {
		// return ((GenericImpl) element).isAutomatic();
		// }
		// }).isEmpty();
		cache.getEngine().newCache().start();
		assert red.getTargets(carColorTime, 1, 0).contains(myAudi);
		cache.start();
		assert myAudi.getTargets(carColorTime).contains(red);
		assert red.getTargets(carColorTime, 1, 0).contains(myAudi) : red.getTargets(carColorTime);
	}

	public void rebuild() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType("Car");
		Attribute carPower = car.addAttribute("power");
		Attribute vehiclePower = vehicle.setAttribute("power");
		assert !carPower.isAlive();
		assert carPower.getImplicit().isAlive();
		assert carPower.getImplicit() == vehiclePower.getImplicit();
	}

	public void rebind() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType("Car");
		Attribute carPower = car.addAttribute("power");
		Attribute carPowerUnit = carPower.addAttribute("unit");
		Attribute vehiclePower = vehicle.setAttribute("power");
		assert !carPower.isAlive();
		assert carPower.getImplicit().isAlive();
		assert !carPowerUnit.isAlive();
		assert carPowerUnit.getImplicit().isAlive();
		assert carPower.getImplicit() == vehiclePower.getImplicit();
		((GenericImpl) carPowerUnit).reBind().remove();
	}

	public void testAnnotation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(MyVehicle.class).start();
		Generic vehicle = cache.find(Vehicle.class);
		Generic myVehicle = cache.find(MyVehicle.class);
		assert !vehicle.isAutomatic();
		assert !myVehicle.isAutomatic();
		assert myVehicle.inheritsFrom(vehicle);
		assert vehicle.getInheritings().contains(myVehicle);
	}

	@SystemGeneric
	public static class Vehicle {}

	@SystemGeneric
	@Extends(meta = Vehicle.class)
	public static class MyVehicle {}

}
