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
import org.genericsystem.core.Statics;
import org.genericsystem.exception.PropertyConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.genericsystem.impl.FunctionalTest.VehicleType.PowerAttribute;
import org.genericsystem.impl.FunctionalTest.VehicleType.PowerAttribute.UnitAttribute;
import org.genericsystem.impl.FunctionalTest.VehicleType.PowerAttribute.UnitAttribute.Power;
import org.genericsystem.impl.FunctionalTest.VehicleType.Vehicle;
import org.testng.annotations.Test;

@Test
public class FunctionalTest extends AbstractTest {

	@Test
	public void testBuilder() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.setType("Car");
		Type color = cache.setType("Color");
		Generic myBmw = car.addInstance("myBmw");
		Generic myAudi = car.addInstance("myAudi");
		Generic red = color.addInstance("red");
		Generic green = color.addInstance("green");

		final Relation carColor = car.setRelation("CarColor", color).enableSingularConstraint();

		myBmw.bind(carColor, red);
		car.bind(carColor, green);
		assert myBmw.getTargets(carColor).size() == 1;
		assert myBmw.getTargets(carColor).get(0).equals(red);
		assert myAudi.getTargets(carColor).size() == 1;
		assert myAudi.getTargets(carColor).get(0).equals(green);
	}

	@Test
	public void testBuilder2() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.setType("Car");
		Type color = cache.setType("Color");
		Generic myBmw = car.addInstance("myBmw");
		Generic red = color.addInstance("red");
		Generic green = color.addInstance("green");

		final Relation carColor = car.setRelation("Power", color).enableSingularConstraint();

		red.bind(carColor, myBmw).log();
		green.bind(carColor, car).log();
		assert myBmw.getLinks(carColor).size() == 1;
		assert myBmw.getTargets(carColor).get(0).equals(red);
	}

	@Test
	public void testBuilder3() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.setType("Car");
		Type color = cache.setType("Color");
		Generic myBmw = car.addInstance("myBmw");
		Generic red = color.addInstance("red");
		Generic green = color.addInstance("green");

		final Relation carColor = car.setRelation("Power", color).enableSingularConstraint();
		assert !carColor.isReferentialIntegrity(Statics.BASE_POSITION);
		assert carColor.isSingularConstraintEnabled();
		red.bind(carColor, myBmw).log();
		green.bind(carColor, myBmw).log();
		assert myBmw.getTargets(carColor).size() == 1;
		assert myBmw.getTargets(carColor).get(0).equals(green);
	}

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

	public void testSimpleHolderInheritance() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		final Attribute carPower = car.setAttribute("Power");
		final Generic myCar = car.addInstance("myCar");
		myCar.addHolder(carPower, 233);
		assert myCar.getHolder(carPower) != null;
	}

	public void testHolderInheritance() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		final Attribute vehiclePower = vehicle.setAttribute("Power");
		final Generic myCar = car.addInstance("myCar");
		myCar.addHolder(vehiclePower, 233);
		assert myCar.getHolder(vehiclePower) != null;
	}
}
