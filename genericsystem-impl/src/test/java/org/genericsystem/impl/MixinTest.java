package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.exception.AmbiguousSelectionException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class MixinTest extends AbstractTest {
	public void testMixin() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type window = cache.addType("Window");
		Attribute windowSize = window.addAttribute("Size");
		Holder defaultWindowSize = window.addValue(windowSize, 235);

		Type resizable = cache.addType("Resizable");
		Attribute resizableSize = resizable.addAttribute("Size");
		// Holder defaultVehicleSize = resizable.addValue(resizableSize, 100);

		/* -------------------------------------------------------------------------------------- */

		Type resizableWindow = cache.addType("resizableWindow", new Type[] { window, resizable });
		// Attribute resizableWindowSize = resizableWindow.addAttribute("Size");

		// assert resizableWindow.getAttribute("Size").equals(windowSize);//ISSUE

		Generic myResizableWindow = resizableWindow.addInstance("myResizableWindow");
		assert myResizableWindow.getValue(windowSize).equals(235);
		assert myResizableWindow.getValue(resizableSize) == null;
	}

	public void testMixin2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type window = cache.addType("Window");
		Attribute windowSize = window.addAttribute("Size");
		// Holder defaultWindowSize = window.addValue(windowSize, 235);

		Type resizable = cache.addType("Resizable");
		Attribute resizableSize = resizable.addAttribute("Size");
		Holder defaultVehicleSize = resizable.addValue(resizableSize, 100);

		/* -------------------------------------------------------------------------------------- */

		Type resizableWindow = cache.addType("resizableWindow", new Type[] { window, resizable });
		// Attribute resizableWindowSize = resizableWindow.addAttribute("Size");

		// assert resizableWindow.getAttribute("Size").equals(windowSize); ISSUE

		Generic myResizableWindow = resizableWindow.addInstance("myResizableWindow");
		assert myResizableWindow.getValue(windowSize) == null;
		assert myResizableWindow.getValue(resizableSize).equals(100) : myResizableWindow.getValue(resizableSize);
	}

	public void testMixin3() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type window = cache.addType("Window");
		Attribute windowSize = window.addAttribute("Size");
		Holder defaultWindowSize = window.addValue(windowSize, 235);

		Type resizable = cache.addType("Resizable");
		Attribute resizableSize = resizable.addAttribute("Size");
		Holder defaultVehicleSize = resizable.addValue(resizableSize, 100);

		/* -------------------------------------------------------------------------------------- */

		Type resizableWindow = cache.addType("resizableWindow", new Type[] { window, resizable });
		// Attribute resizableWindowSize = resizableWindow.addAttribute("Size");

		// assert resizableWindow.getAttribute("Size").equals(windowSize);

		Generic myResizableWindow = resizableWindow.addInstance("myResizableWindow");
		assert myResizableWindow.getValue(windowSize).equals(235);
		assert myResizableWindow.getValue(resizableSize).equals(100);
	}

	public void testMixin4() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type window = cache.addType("Window");
		final Attribute windowSize = window.addAttribute("Size");
		Holder defaultWindowSize = window.addValue(windowSize, 235);

		Type resizable = cache.addType("Resizable");
		Attribute resizableSize = resizable.addAttribute("Size");
		Holder defaultVehicleSize = resizable.addValue(resizableSize, 100);

		/* -------------------------------------------------------------------------------------- */

		final Type resizableWindow = cache.addType("resizableWindow", new Type[] { window, resizable });
		// Attribute resizableWindowSize = resizableWindow.addAttribute("Size");

		new RollbackCatcher() {

			@Override
			public void intercept() {
				assert resizableWindow.getAttribute("Size").equals(windowSize);
			}
		}.assertIsCausedBy(AmbiguousSelectionException.class);

		// Generic myResizableWindow = resizableWindow.addInstance("myResizableWindow");
		// assert myResizableWindow.getValue(windowSize).equals(235);
		// assert myResizableWindow.getValue(resizableSize).equals(100);
	}

	public void testMixin5() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type window = cache.addType("Window");
		Attribute windowSize = window.addAttribute("Size");
		Holder defaultWindowSize = window.addValue(windowSize, 235);

		Type resizable = cache.addType("Resizable");
		Attribute resizableSize = resizable.addAttribute("Size");
		// Holder defaultVehicleSize = resizable.addValue(resizableSize, 100);

		/* -------------------------------------------------------------------------------------- */

		Type resizableWindow = cache.addType("resizableWindow", new Type[] { window, resizable });
		Attribute resizableWindowSize = resizableWindow.addAttribute("Size");

		assert resizableWindow.getAttribute("Size").equals(resizableWindowSize);

		Generic myResizableWindow = resizableWindow.addInstance("myResizableWindow");
		// Generic myResizableWindow233 = myResizableWindow.setValue(windowSize, 150);
		// || Generic myResizableWindow233 = myResizableWindow.addValue(resizableSize,150);

		assert myResizableWindow.getValue(windowSize).equals(235);
		assert myResizableWindow.getValue(resizableWindowSize).equals(235);
		assert myResizableWindow.getValue(resizableSize).equals(235);
	}

	public void testMixin6() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.addAttribute("Power");
		Holder defaultVehiclePower = vehicle.addValue(vehiclePower, 235);
		Type car = vehicle.addSubType("Car");
		Attribute carPower = ((GenericImpl) car).addSubAttribute(vehiclePower, "Power");

		assert car.getValue(vehiclePower).equals(235);

		Holder defaultCarPower = car.setValue(carPower, 235);
		assert defaultCarPower.inheritsFrom(defaultVehiclePower);

		Generic myCar = car.addInstance("myCar");
		assert myCar.getValue(vehiclePower).equals(235);

		assert car.getValue(vehiclePower).equals(235);
		assert car.getValue(carPower).equals(235);
		assert myCar.getValue(carPower).equals(235);
	}

	public void testMixin7() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.addAttribute("Power");
		Holder defaultVehiclePower = vehicle.addValue(vehiclePower, 235);
		Type car = vehicle.addSubType("Car");
		Attribute carPower = ((GenericImpl) car).addSubAttribute(vehiclePower, "Power2");
		assert carPower.inheritsFrom(vehiclePower);

		Holder defaultCarPower = car.addValue(carPower, 235);

		assert ((GenericImpl) defaultCarPower).inheritsFrom(((GenericImpl) defaultVehiclePower).vertex());
		assert defaultCarPower.inheritsFrom(defaultVehiclePower) : defaultCarPower.info() + defaultVehiclePower.info();

		Generic myCar = car.addInstance("myCar");
		// assert myCar.getValue(vehiclePower).equals(235);

		assert car.getValue(vehiclePower).equals(235);
		assert car.getValue(carPower).equals(235);
		assert myCar.getValue(carPower).equals(235);
	}

	public void testMixin8() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.addAttribute("Power");
		Holder defaultVehiclePower = vehicle.addValue(vehiclePower, 123);
		Type car = vehicle.addSubType("Car");
		Attribute carPower = ((GenericImpl) car).addSubAttribute(vehiclePower, "Power");

		assert car.getValue(vehiclePower).equals(123);

		// Holder defaultCarPower = car.addValue(carPower, 123);// automatic
		assert car.getHolder(carPower).inheritsFrom(defaultVehiclePower);

		Generic myCar = car.addInstance("myCar");
		assert myCar.getValue(vehiclePower).equals(123);

		assert car.getValue(vehiclePower).equals(123);
		assert car.getValue(carPower).equals(123);
		assert myCar.getValue(carPower).equals(123);
	}

}
