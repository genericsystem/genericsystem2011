package org.genericsystem.impl;

import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.core.Cache;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class OverrideAnnotationTest extends AbstractTest {

	public void test() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Car.class, Man.class, OutsideColor.class).start();
		Type vehicle = cache.find(Vehicle.class);
		Type car = cache.find(Car.class);
		Type human = cache.find(Human.class);
		Type man = cache.find(Man.class);
		Type outsideColor = cache.find(OutsideColor.class);
		assert car.inheritsFrom(vehicle);
		assert !car.inheritsFrom(human) : car.info();
		assert man.inheritsFrom(human);
		assert outsideColor.supers().size() == 1 : outsideColor.supers();
		assert outsideColor.supers().contains(cache.find(Color.class));
		assert !outsideColor.supers().contains(cache.getEngine());
	}

	@SystemGeneric
	public static class Vehicle {

	}

	@SystemGeneric
	@Extends(Vehicle.class)
	public static class Car extends Human {

	}

	@SystemGeneric
	public static class Human {}

	@SystemGeneric
	public static class Man extends Human {}

	@SystemGeneric
	public static class Color {

	}

	@SystemGeneric
	@Extends(Color.class)
	public static class OutsideColor {

	}

}
