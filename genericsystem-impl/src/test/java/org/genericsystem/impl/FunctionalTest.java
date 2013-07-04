package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Snapshot;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class FunctionalTest extends AbstractTest {

	@Test
	public void getCarInstancesWithPowerHigherThan90HP() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		final Attribute carPower = car.setProperty( "Power");
		Generic myCar = car.newInstance( "myCar");
		myCar.setValue( carPower, 233);
		Generic yourCar = car.newInstance( "yourCar");
		yourCar.setValue( carPower, 89);
		Snapshot<Generic> carInstancesWithPowerHigherThan90HP = car.getAllInstances().filter(new Filter<Generic>() {
			@Override
			public boolean isSelected(Generic generic) {
				return generic.<Integer> getValue( carPower) >= 90;
			}
		});
		assert carInstancesWithPowerHigherThan90HP.contains(myCar);
		assert !carInstancesWithPowerHigherThan90HP.contains(yourCar);
		assert carInstancesWithPowerHigherThan90HP.size() == 1;
	}

	@Test
	public void testSnaphotIsAware() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");

		Snapshot<Generic> snapshot = vehicle.getSubTypes();
		assert snapshot.isEmpty();

		Type car = vehicle.newSubType( "Car");
		assert snapshot.size() == 1;
		assert snapshot.contains(car);

		car.remove();
		assert snapshot.isEmpty();
		assert !snapshot.contains(car);
	}
}
