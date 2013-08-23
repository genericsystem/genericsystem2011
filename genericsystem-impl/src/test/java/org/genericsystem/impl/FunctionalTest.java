package org.genericsystem.impl;

import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Snapshot;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.core.Statics;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class FunctionalTest extends AbstractTest {

//	@Test
//	public void testMainSnapshot() {
//		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		Type vehicle = cache.newType("Vehicle");
//		Type car = vehicle.newSubType("Car");
//
//		final Attribute vehiculePower = vehicle.setProperty("Power");
//		car.setValue(vehiculePower, 80);
//		Generic myCar = car.newInstance("myCar");
//		myCar.setValue(vehiculePower, 233);
//		((GenericImpl) myCar).mainSnaphot(vehiculePower, SystemGeneric.CONCRETE, Statics.BASE_POSITION, true).log();
//		((GenericImpl) myCar).getHolders(vehiculePower).log();
//		((GenericImpl) car).mainSnaphot(vehiculePower, SystemGeneric.CONCRETE, Statics.BASE_POSITION, false).log();
//		((GenericImpl) car).getHolders(vehiculePower).log();
//	}

//	@Test
//	public void testMainSnapshot2() {
//		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		Type vehicle = cache.newType("Vehicle");
//		Type car = vehicle.newSubType("Car");
//
//		Generic myCar = car.newInstance("myCar");
//		Generic yourCar = car.newInstance("yourCar");
//		Relation larger = car.addRelation("larger", car);
//		myCar.bind(larger, yourCar);
//
//		long time = System.currentTimeMillis();
//		for (int i = 0; i < 10000; i++)
//			for (Generic generic : ((GenericImpl) myCar).mainSnaphot(larger, SystemGeneric.STRUCTURAL, Statics.MULTIDIRECTIONAL, false));
//		log.info("time : " + (System.currentTimeMillis() - time));
//		time = System.currentTimeMillis();
//		for (int i = 0; i < 10000; i++)
//			for (Generic generic : ((GenericImpl) yourCar).getAttributes());
//		log.info("time : " + (System.currentTimeMillis() - time));
//
//		assert ((GenericImpl) myCar).mainSnaphot(larger, SystemGeneric.CONCRETE, Statics.BASE_POSITION, false).equals(((GenericImpl) myCar).getHolders(larger)) : ((GenericImpl) myCar).mainSnaphot(larger, SystemGeneric.CONCRETE, Statics.BASE_POSITION,
//				false);
//		assert ((GenericImpl) yourCar).mainSnaphot(larger, SystemGeneric.CONCRETE, Statics.BASE_POSITION, false).equals(((GenericImpl) yourCar).getHolders(larger));
//		assert ((GenericImpl) car).mainSnaphot(larger, SystemGeneric.CONCRETE, Statics.BASE_POSITION, false).equals(((GenericImpl) car).getHolders(larger));
//
//		assert ((GenericImpl) myCar).mainSnaphot(cache.getMetaAttribute(), SystemGeneric.STRUCTURAL, Statics.MULTIDIRECTIONAL, false).equals(((GenericImpl) myCar).getAttributes()) : ((GenericImpl) myCar).mainSnaphot(cache.getMetaAttribute(),
//				SystemGeneric.STRUCTURAL, Statics.MULTIDIRECTIONAL, false) + " " + ((GenericImpl) myCar).getAttributes();
//		assert ((GenericImpl) yourCar).mainSnaphot(cache.getMetaAttribute(), SystemGeneric.STRUCTURAL, Statics.MULTIDIRECTIONAL, false).equals(((GenericImpl) yourCar).getAttributes());
//		assert ((GenericImpl) car).mainSnaphot(cache.getMetaAttribute(), SystemGeneric.STRUCTURAL, Statics.MULTIDIRECTIONAL, false).equals(((GenericImpl) car).getAttributes());
//
//	}

	@Test
	public void getCarInstancesWithPowerHigherThan90HP() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		final Attribute carPower = car.setProperty("Power");
		Generic myCar = car.newInstance("myCar");
		myCar.setValue(carPower, 233);
		Generic yourCar = car.newInstance("yourCar");
		yourCar.setValue(carPower, 89);
		Snapshot<Generic> carInstancesWithPowerHigherThan90HP = car.getAllInstances().filter(new Filter<Generic>() {
			@Override
			public boolean isSelected(Generic generic) {
				return generic.<Integer> getValue(carPower) >= 90;
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

		Type car = vehicle.newSubType("Car");
		assert snapshot.size() == 1;
		assert snapshot.contains(car);

		car.remove();
		assert snapshot.isEmpty();
		assert !snapshot.contains(car);
	}
}
