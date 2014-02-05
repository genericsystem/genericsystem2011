package org.genericsystem.impl;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.InstanceGenericClass;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.InstanceValueClassConstraint;
import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class InstanciationClassTest extends AbstractTest {

	@SystemGeneric
	@InstanceGenericClass(Car.class)
	public static class Cars extends GenericImpl {
	}

	@SystemGeneric
	@Components(Car.class)
	@InstanceValueClassConstraint(Integer.class)
	public static class Power extends GenericImpl {
	}

	public static class Car extends GenericImpl {

		public Integer getPower() {
			return getValue(getCurrentCache().<Attribute> find(Power.class));
		}

		public void setPower(Integer power) {
			setValue(getCurrentCache().<Attribute> find(Power.class), power);
		}
	}

	// -----------------------------------------------------------------------------------------------

	// @SystemGeneric
	// @InstanceGenericClass(Car.class)
	// public static class Cars extends GenericImpl {
	//
	// @SystemGeneric
	// @Components(Car.class)
	// @InstanceValueClassConstraint(Integer.class)
	// public static class Power extends GenericImpl {
	// }
	// }

	// public static class Car extends GenericImpl {
	//
	// @Attribute(Power.class)
	// private Integer power;
	// public Integer getPower() {
	// return getValue(getCurrentCache().<Attribute> find(Power.class));
	// }
	//
	// public void setPower(Integer power) {
	// setValue(getCurrentCache().<Attribute> find(Power.class), power);
	// }
	// }

	public void testCurrentFunctionGS() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Cars.class).start();
		Type cars = cache.find(Cars.class);
		Generic myBmw = cars.setInstance("myBmw");
		Generic myAudi = cars.setInstance("myAudi");
		assert cars.getClass().equals(Cars.class);
		assert myBmw.getClass().equals(Car.class);
		assert myAudi.getClass().equals(Car.class);
	}

	public void testFutureFunctionGS() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Cars.class).start();
		Type cars = cache.find(Cars.class);
		Generic myBmw = cars.setInstance("myBmw");
		Generic myAudi = cars.setInstance("myAudi");
		assert cars.getClass().equals(Cars.class);
		assert myBmw.getClass().equals(Car.class);
		assert myAudi.getClass().equals(Car.class);

	}

}
