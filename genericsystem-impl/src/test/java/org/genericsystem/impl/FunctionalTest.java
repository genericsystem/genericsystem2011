package org.genericsystem.impl;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.core.Snapshot.Filter;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Type;
import org.testng.annotations.Test;

@Test
public class FunctionalTest extends AbstractTest {

	@Test
	public void getCarInstancesWithPowerHigherThan90HP() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		final Attribute carPower = car.addProperty(cache, "Power");
		Generic myCar = car.newInstance(cache, "myCar");
		myCar.setValue(cache, carPower, 233);
		Generic yourCar = car.newInstance(cache, "yourCar");
		yourCar.setValue(cache, carPower, 89);
		Snapshot<Generic> carInstancesWithPowerHigherThan90HP = car.getAllInstances(cache).filter(new Filter<Generic>() {
			@Override
			public boolean isSelected(Generic generic) {
				return generic.<Integer> getValue(cache, carPower) >= 90;
			}
		});
		assert carInstancesWithPowerHigherThan90HP.contains(myCar);
		assert !carInstancesWithPowerHigherThan90HP.contains(yourCar);
		assert carInstancesWithPowerHigherThan90HP.size() == 1;
		carInstancesWithPowerHigherThan90HP.log();
	}
}
