package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
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
		assert myResizableWindow.getValue(resizableSize).equals(235);
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
		assert myResizableWindow.getValue(windowSize).equals(100);
		assert myResizableWindow.getValue(resizableSize).equals(100);
	}

	public void testMixin3() {
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

		assert resizableWindow.getAttribute("Size").equals(windowSize);

		Generic myResizableWindow = resizableWindow.addInstance("myResizableWindow");
		assert myResizableWindow.getValue(windowSize).equals(235);
		assert myResizableWindow.getValue(resizableSize).equals(235);
	}

	public void testMixin4() {
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

		assert resizableWindow.getAttribute("Size").equals(windowSize);

		Generic myResizableWindow = resizableWindow.addInstance("myResizableWindow");
		Generic myResizableWindow233 = myResizableWindow.setValue(windowSize, 150);
		// || Generic myResizableWindow233 = myResizableWindow.addValue(resizableSize,150);

		assert myResizableWindow.getValue(windowSize).equals(150);
		assert myResizableWindow.getValue(resizableSize).equals(150);
	}

	public void testMixin5() {
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

		assert resizableWindow.getAttribute("Size").equals(windowSize);

		Generic myResizableWindow = resizableWindow.addInstance("myResizableWindow");
		Generic myResizableWindow233 = myResizableWindow.setValue(windowSize, 150);
		// || Generic myResizableWindow233 = myResizableWindow.addValue(resizableSize,150);

		assert myResizableWindow.getValue(windowSize).equals(150);
		assert myResizableWindow.getValue(resizableSize).equals(150);
	}

	public void testMixin6() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.addAttribute("Power");
		Holder defaultVehiclePower = vehicle.addValue(vehiclePower, 235);
		Type car = vehicle.addSubType("Car");
		Attribute carPower = car.addAttribute("Power");
		assert car.getValue(vehiclePower).equals(235);
		assert car.getValue(carPower).equals(235);
		Generic myCar = car.addInstance("myCar");
		assert myCar.getValue(vehiclePower).equals(235);
		assert myCar.getValue(carPower).equals(235);

	}
}
