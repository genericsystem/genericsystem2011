package org.genericsystem.impl;

import org.genericsystem.core.Cache;
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
import org.genericsystem.system.MultiDirectionalSystemProperty;
import org.testng.annotations.Test;

@Test
public class AutomaticTest extends AbstractTest {

	public void test() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		assert !((GenericImpl) cache.getEngine()).isAutomatic();
		assert ((GenericImpl) cache.getEngine()).isFlushable(cache);
		assert !((GenericImpl) cache.getEngine().getMetaAttribute()).isAutomatic();
		assert ((GenericImpl) cache.getEngine().getMetaAttribute()).isFlushable(cache);
		assert !((GenericImpl) cache.getEngine().getMetaRelation()).isAutomatic();
		assert ((GenericImpl) cache.getEngine().getMetaRelation()).isFlushable(cache);
		Generic multiDirectional = cache.find(MultiDirectionalSystemProperty.class);
		assert !((GenericImpl) multiDirectional).isAutomatic();
		assert ((GenericImpl) multiDirectional).isFlushable(cache);
		assert ((GenericImpl) multiDirectional.getImplicit()).isAutomatic();
		assert ((GenericImpl) multiDirectional.getImplicit()).isFlushable(cache);
	}

	public void testAttribut() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute(cache, "power");

		assert !((GenericImpl) vehicle).isAutomatic();
		assert ((GenericImpl) vehicle).isFlushable(cache);
		assert !((GenericImpl) vehiclePower).isAutomatic();
		assert ((GenericImpl) vehiclePower).isFlushable(cache);
		assert ((GenericImpl) vehiclePower.getImplicit()).isAutomatic();
		assert ((GenericImpl) vehiclePower.getImplicit()).isFlushable(cache);
	}

	public void testSubType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "car");

		assert !((GenericImpl) vehicle).isAutomatic();
		assert ((GenericImpl) vehicle).isFlushable(cache);
		assert !((GenericImpl) car).isAutomatic();
		assert ((GenericImpl) car).isFlushable(cache);
		assert ((GenericImpl) car.getImplicit()).isAutomatic();
		assert ((GenericImpl) car.getImplicit()).isFlushable(cache);
	}

	public void testMutipleInheritance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type graphicComponent = cache.newType("graphicComponent");
		Type window = graphicComponent.newSubType(cache, "Window");
		Type selectable = graphicComponent.newSubType(cache, "Selectable");
		Type selectableWindow = cache.newSubType("selectableWindow", selectable, window);

		assert !((GenericImpl) graphicComponent).isAutomatic();
		assert ((GenericImpl) graphicComponent).isFlushable(cache);
		assert !((GenericImpl) window).isAutomatic();
		assert ((GenericImpl) window).isFlushable(cache);
		assert ((GenericImpl) window.getImplicit()).isAutomatic();
		assert ((GenericImpl) window.getImplicit()).isFlushable(cache);
		assert !((GenericImpl) selectable).isAutomatic();
		assert ((GenericImpl) selectable).isFlushable(cache);
		assert ((GenericImpl) selectable.getImplicit()).isAutomatic();
		assert ((GenericImpl) selectable.getImplicit()).isFlushable(cache);
		assert !((GenericImpl) selectableWindow).isAutomatic();
		assert ((GenericImpl) selectableWindow).isFlushable(cache);
		assert ((GenericImpl) selectableWindow.getImplicit()).isAutomatic();
		assert ((GenericImpl) selectableWindow.getImplicit()).isFlushable(cache);
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
				assert !((GenericImpl) element).isFlushable(cache);
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
		assert ((GenericImpl) myBmwRedToday).isFlushable(cache);
		assert ((GenericImpl) myBmwRedTomorrow).isFlushable(cache);
	}

	public void ternaryFlush() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		// Type car = cache.newType("Car");
		// Type color = cache.newType("Color");
		// Type time = cache.newType("Time");
		// Relation carColorTime = car.setRelation(cache, "CarColorTime", color, time);
		// car.newInstance(cache, "myAudi");
		// Generic red = color.newInstance(cache, "red");
		// time.newInstance(cache, "today");
		// time.newInstance(cache, "tomorrow");
		// car.setLink(cache, carColorTime, "carRed", red, time);
		//
		// cache.flush();
		//
		// Snapshot<Link> links = red.getLinks(new Transaction(cache.getEngine()), carColorTime);
		// assert links.filter(new Filter<Link>() {
		// @Override
		// public boolean isSelected(Link element) {
		// return ((GenericImpl) element).isAutomatic();
		// }
		// }).isEmpty();
	}
}
