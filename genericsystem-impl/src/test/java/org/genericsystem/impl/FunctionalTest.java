package org.genericsystem.impl;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.InstanceGenericClass;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.core.Cache;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Snapshot;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.exception.PropertyConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Type;
import org.genericsystem.impl.FunctionalTest.VehicleType.PowerAttribute;
import org.genericsystem.impl.FunctionalTest.VehicleType.PowerAttribute.UnitAttribute;
import org.genericsystem.impl.FunctionalTest.VehicleType.PowerAttribute.UnitAttribute.Power;
import org.genericsystem.impl.FunctionalTest.VehicleType.Vehicle;
import org.testng.annotations.Test;

@Test
public class FunctionalTest extends AbstractTest {

	// @Test
	// public void testMainSnapshot() {
	// final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
	// Type vehicle = cache.newType("Vehicle");
	// Type car = vehicle.newSubType("Car");
	//
	// final Attribute vehiculePower = vehicle.setProperty("Power");
	// car.setValue(vehiculePower, 80);
	// Generic myCar = car.newInstance("myCar");
	// myCar.setValue(vehiculePower, 233);
	// ((GenericImpl) myCar).mainSnaphot(vehiculePower, Statics.CONCRETE, Statics.BASE_POSITION, true).log();
	// ((GenericImpl) myCar).getHolders(vehiculePower).log();
	// ((GenericImpl) car).mainSnaphot(vehiculePower, Statics.CONCRETE, Statics.BASE_POSITION, false).log();
	// ((GenericImpl) car).getHolders(vehiculePower).log();
	// }

	// @Test
	// public void testMainSnapshot2() {
	// final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
	// Type vehicle = cache.newType("Vehicle");
	// Type car = vehicle.newSubType("Car");
	//
	// Generic myCar = car.newInstance("myCar");
	// Generic yourCar = car.newInstance("yourCar");
	// Relation larger = car.addRelation("larger", car);
	// myCar.bind(larger, yourCar);
	//
	// long time = System.currentTimeMillis();
	// for (int i = 0; i < 10000; i++)
	// for (Generic generic : ((GenericImpl) myCar).mainSnaphot(larger, Statics.STRUCTURAL, Statics.MULTIDIRECTIONAL, false));
	// log.info("time : " + (System.currentTimeMillis() - time));
	// time = System.currentTimeMillis();
	// for (int i = 0; i < 10000; i++)
	// for (Generic generic : ((GenericImpl) yourCar).getAttributes());
	// log.info("time : " + (System.currentTimeMillis() - time));
	//
	// assert ((GenericImpl) myCar).mainSnaphot(larger, Statics.CONCRETE, Statics.BASE_POSITION, false).equals(((GenericImpl) myCar).getHolders(larger)) : ((GenericImpl) myCar).mainSnaphot(larger, Statics.CONCRETE, Statics.BASE_POSITION,
	// false);
	// assert ((GenericImpl) yourCar).mainSnaphot(larger, Statics.CONCRETE, Statics.BASE_POSITION, false).equals(((GenericImpl) yourCar).getHolders(larger));
	// assert ((GenericImpl) car).mainSnaphot(larger, Statics.CONCRETE, Statics.BASE_POSITION, false).equals(((GenericImpl) car).getHolders(larger));
	//
	// assert ((GenericImpl) myCar).mainSnaphot(cache.getMetaAttribute(), Statics.STRUCTURAL, Statics.MULTIDIRECTIONAL, false).equals(((GenericImpl) myCar).getAttributes()) : ((GenericImpl) myCar).mainSnaphot(cache.getMetaAttribute(),
	// Statics.STRUCTURAL, Statics.MULTIDIRECTIONAL, false) + " " + ((GenericImpl) myCar).getAttributes();
	// assert ((GenericImpl) yourCar).mainSnaphot(cache.getMetaAttribute(), Statics.STRUCTURAL, Statics.MULTIDIRECTIONAL, false).equals(((GenericImpl) yourCar).getAttributes());
	// assert ((GenericImpl) car).mainSnaphot(cache.getMetaAttribute(), Statics.STRUCTURAL, Statics.MULTIDIRECTIONAL, false).equals(((GenericImpl) car).getAttributes());
	//
	// }

	@Test
	public void getCarInstancesWithPowerHigherThan90HP() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		final Attribute carPower = car.setProperty("Power");
		Generic myCar = car.addInstance("myCar");
		myCar.setValue(carPower, 233);
		Generic yourCar = car.addInstance("yourCar");
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
		Type vehicle = cache.addType("Vehicle");

		Snapshot<Generic> snapshot = vehicle.getAllSubTypes();
		assert snapshot.isEmpty();

		Type car = vehicle.addSubType("Car");
		assert snapshot.size() == 1;
		assert snapshot.contains(car);

		car.remove();
		assert snapshot.isEmpty();
		assert !snapshot.contains(car);
	}

	@SystemGeneric
	@InstanceGenericClass(Vehicle.class)
	public static class VehicleType extends GenericImpl {
		@SystemGeneric
		@Components(VehicleType.class)
		@InstanceGenericClass(Power.class)
		@SingularConstraint
		public static class PowerAttribute extends GenericImpl {

			@SystemGeneric
			@Components(PowerAttribute.class)
			@SingularConstraint
			public static class UnitAttribute extends GenericImpl {
				@SystemGeneric
				@Components(Vehicle.class)
				public static class Power extends GenericImpl {
					public String getUnit() {
						return getValue(getCurrentCache().<Attribute> find(UnitAttribute.class));
					}

					public void setUnit(String unit) {
						setValue(getCurrentCache().<Attribute> find(UnitAttribute.class), unit);
					}
				}
			}
		}

		public static class Vehicle extends GenericImpl {
			public Snapshot<Integer> getPowers() {
				return getValues(getCurrentCache().<Attribute> find(PowerAttribute.class));
			}

			public void setPower(int power, String unit) {
				this.<Power> setValue(getCurrentCache().<Attribute> find(PowerAttribute.class), power).setUnit(unit);
			}

			public String getPowerUnit() {
				return this.<Power> getHolder(getCurrentCache().<Attribute> find(PowerAttribute.class)).getUnit();
			}

		}

	}

	@Test
	public void testFrancois() {
		Engine engine = GenericSystem.newInMemoryEngine(VehicleType.class, Vehicle.class, PowerAttribute.class, UnitAttribute.class, Power.class);
		final Cache cache = engine.newCache().start();
		Type vehicle = cache.find(VehicleType.class);

		Vehicle myBmw = vehicle.setInstance("myBmw");
		myBmw.setPower(186, "KW");
		myBmw.setPower(186, "CV");
		assert vehicle.<Vehicle> getInstance("myBmw").getPowerUnit().equals("CV");
		myBmw.setPower(235, "KW");
		cache.flush();

		Cache cache2 = engine.newCache().start();
		vehicle = cache2.find(VehicleType.class);
		assert vehicle.<Vehicle> getInstance("myBmw").getPowers().contains(235);
		assert !vehicle.<Vehicle> getInstance("myBmw").getPowers().contains(186);
		assert vehicle.<Vehicle> getInstance("myBmw").getPowerUnit().equals("KW");
	}

	@Test
	public void testExistException() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		final Attribute carPower = car.setProperty("Power");
		final Generic myCar = car.addInstance("myCar");
		myCar.addHolder(carPower, 233);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				myCar.addHolder(carPower, 234);
			}
		}.assertIsCausedBy(PropertyConstraintViolationException.class);
	}
}
