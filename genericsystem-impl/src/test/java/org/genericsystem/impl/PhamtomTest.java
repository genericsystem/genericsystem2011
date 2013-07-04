package org.genericsystem.impl;

import java.util.Arrays;
import java.util.Iterator;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Snapshot;
import org.genericsystem.exception.PhantomConstraintViolationException;
import org.genericsystem.exception.UniqueStructuralValueConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class PhamtomTest extends AbstractTest {

	public void test() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType("Car");
		Attribute power = vehicle.setAttribute("power");
		Holder defaultPower = car.setValue(power, "123");

		Generic myCar = car.newInstance("myCar");

		myCar.removeHolder(defaultPower);
		assert myCar.getValue(power) == null;

		myCar.setValue(power, "200");
		myCar.clearAllConcrete(power);
		assert myCar.getValue(power) == "123" : myCar.getValue(power);
	}

	public void test3() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType("Car");
		Attribute power = vehicle.setAttribute("power");
		Holder defaultPower = car.setValue(power, "123");

		Generic myCar = car.newInstance("myCar");

		myCar.removeHolder(defaultPower);
		assert myCar.getValue(power) == null;

		Holder holder100 = myCar.setValue(power, "100");
		Holder holder200 = myCar.setValue(power, "200");
		assert myCar.getValues(power).size() == 2 : myCar.getValues(power).size();
		assert myCar.getValues(power).containsAll(Arrays.asList("100", "200")) : myCar.getValues(power);

		myCar.removeHolder(holder100);
		assert myCar.getValues(power).size() == 1 : myCar.getValues(power).size();
		assert myCar.getValues(power).containsAll(Arrays.asList("200")) : myCar.getValues(power);

		myCar.removeHolder(holder200);
		assert myCar.getValues(power).size() == 0 : myCar.getValues(power).size();
		assert myCar.getValues(power).containsAll(Arrays.asList()) : myCar.getValues(power);
	}

	public void test4() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Attribute power = car.setAttribute("power");
		Holder defaultHolder = car.setValue(power, "123");

		Generic myCar = car.newInstance("myCar");
		assert myCar.getValue(power).equals("123");

		Holder holder200 = myCar.setValue(power, "200");

		Holder holder100 = myCar.setValue(defaultHolder, "100");
		assert myCar.getValues(power).containsAll(Arrays.asList("100", "200")) : myCar.getValues(power);

		myCar.removeHolder(holder200);
		assert myCar.getValue(power).equals("100") : myCar.getValues(power);

		myCar.removeHolder(holder100);
		assert myCar.getValue(power).equals("123") : myCar.getValues(power);
	}

	public void test5() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Attribute power = car.setAttribute("power");
		Holder defaultHolder = car.setValue(power, "123");

		Generic myCar = car.newInstance("myCar");
		assert myCar.getValue(power).equals("123");

		Holder holder200 = myCar.setValue(power, "200");

		Holder holder100 = myCar.setValue(defaultHolder, "100");
		assert myCar.getValues(power).containsAll(Arrays.asList("100", "200")) : myCar.getValues(power);

		myCar.removeHolder(holder100);
		assert myCar.getValues(power).containsAll(Arrays.asList("123", "200")) : myCar.getValues(power);

		myCar.removeHolder(holder200);
		assert myCar.getValue(power).equals("123") : myCar.getValues(power);
	}

	public void testAliveWithStructural() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType("Car");
		Attribute vehiclePower = vehicle.setAttribute("power");

		assert car.getAttributes().contains(vehiclePower);
		car.cancelAll(vehiclePower, false);
		Iterator<Generic> iterator = ((GenericImpl) car).attributesIterator(vehiclePower, true);
		Generic phantom = iterator.next();
		// car.restore( vehiclePower);
		car.clearAllStructural(vehiclePower);
		assert !phantom.isAlive();
	}

	public void testAliveWithConcrete() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute("power");
		Holder defaultPower = vehicle.setValue(vehiclePower, "123");
		Generic myVehicle = vehicle.newInstance("myVehicle");

		assert myVehicle.getValue(vehiclePower) == "123";
		Generic phantom = myVehicle.setValue(defaultPower, null);
		myVehicle.setValue(defaultPower, "123");
		assert !phantom.isAlive();

		phantom = myVehicle.setValue(defaultPower, null);
		myVehicle.setValue(defaultPower, "555");
		assert phantom.isAlive();

		vehiclePower.enableSingularConstraint();
		assert ((Type) defaultPower).isSingularConstraintEnabled();
		myVehicle.setValue(defaultPower, "235");
		assert myVehicle.getValue(vehiclePower) == "235";
	}

	public void cancelAttribute() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type vehicle = cache.newType("Vehicle");
		final Type car = vehicle.newSubType("Car");
		final Attribute vehiclePower = vehicle.setAttribute("power");

		assert vehicle.getAttributes().contains(vehiclePower);
		vehicle.setValue(vehiclePower, null);
		assert vehicle.getAttributes().contains(vehiclePower);
		((GenericImpl) car).setSubAttribute(vehiclePower, null);
		assert vehicle.getAttributes().contains(vehiclePower);
		assert !car.getAttributes().contains(vehiclePower);
		car.getAttribute(null).remove();
		assert vehicle.getAttributes().contains(vehiclePower);
		assert car.getAttributes().contains(vehiclePower);
	}

	public void cancelAttributeWithInheritsBase() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType("Car");
		Attribute vehiclePower = vehicle.setAttribute("power");

		assert car.getAttributes().contains(vehiclePower);
		((GenericImpl) car).setSubAttribute(vehiclePower, null);
		assert !car.getAttributes().contains(vehiclePower);
	}

	public void cancelAttributeWithInheritsAttribute() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		final Type car = vehicle.newSubType("Car");
		final Attribute vehiclePower = vehicle.setAttribute("power");

		assert car.getAttributes().contains(vehiclePower);
		Attribute carPower = car.setAttribute("power");
		assert car.getAttributes().contains(carPower);
		assert carPower.inheritsFrom(vehiclePower);

		new RollbackCatcher() {
			@Override
			public void intercept() {
				((GenericImpl) car).setSubAttribute(vehiclePower, null);
			}
		}.assertIsCausedBy(PhantomConstraintViolationException.class);
	}

	public void cancelAndRestoreRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation vehicleHuman = vehicle.setRelation("VehicleHuman", human);
		Type car = vehicle.newSubType("Car");

		assert vehicle.getRelations().size() == 1;
		assert vehicle.getRelations().contains(vehicleHuman);
		assert car.getRelations().size() == 1;
		assert car.getRelations().contains(vehicleHuman);

		// car.cancel( vehicleHuman);
		((GenericImpl) car).setSubAttribute(vehicleHuman, null, human);

		assert vehicle.getRelations().size() == 1;
		assert car.getRelations().isEmpty();

		// car.restore( vehicleHuman);
		car.getAttribute(vehicleHuman, null).remove();

		assert vehicle.getRelations().size() == 1;
		assert vehicle.getRelations().contains(vehicleHuman);
		assert car.getRelations().size() == 1 : car.getRelations();
		assert car.getRelations().contains(vehicleHuman) : car.getRelations();
	}

	public void testPhantomHierarchyRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation vehicleHuman = vehicle.setRelation("VehicleHuman", human);
		Type car = vehicle.newSubType("Car");
		Type convertible = car.newSubType("Convertible");

		assert vehicle.getRelations().contains(vehicleHuman);
		assert car.getRelations().contains(vehicleHuman);
		assert convertible.getRelations().contains(vehicleHuman);

		car.cancelAll(vehicleHuman, false);
		assert vehicle.getRelations().contains(vehicleHuman);
		assert car.getRelations().size() == 0;
		assert !car.getRelations().contains(vehicleHuman);
		assert convertible.getRelations().size() == 0;
		assert !convertible.getRelations().contains(vehicleHuman);

		car.clearAllStructural(vehicleHuman);
		assert vehicle.getRelations().contains(vehicleHuman);
		assert car.getRelations().contains(vehicleHuman);
		assert convertible.getRelations().contains(vehicleHuman);

		convertible.cancelAll(vehicleHuman, false);
		assert vehicle.getRelations().contains(vehicleHuman);
		assert car.getRelations().contains(vehicleHuman);
		assert convertible.getRelations().size() == 0;
		assert !convertible.getRelations().contains(vehicleHuman);
	}

	public void testPhantomMultiHierachyRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Relation vehicleColor = vehicle.setRelation("VehicleColor", color);
		Type car = vehicle.newSubType("Car");
		Type breakVehicle = car.newSubType("BreakVehicle");
		Type convertible = car.newSubType("Convertible");

		car.cancelAll(vehicleColor, false);
		assert vehicle.getRelations().size() == 1;
		assert car.getRelations().size() == 0;
		assert breakVehicle.getRelations().size() == 0;
		assert convertible.getRelations().size() == 0;

		car.clearAllStructural(vehicleColor);
		assert vehicle.getRelations().size() == 1;
		assert car.getRelations().get(0).equals(vehicleColor) : car.getRelations();
		assert breakVehicle.getRelations().get(0).equals(vehicleColor);
		assert convertible.getRelations().get(0).equals(vehicleColor);
	}

	public void testRelationsWithSameName() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		final Type color = cache.newType("Color");

		Relation r1 = vehicle.setRelation("VehicleHuman", human);
		cache.flush();
		new RollbackCatcher() {

			@Override
			public void intercept() {
				vehicle.setRelation("VehicleHuman", color);
			}
		}.assertIsCausedBy(UniqueStructuralValueConstraintViolationException.class);
		assert r1 == cache.getEngine().getSubType("VehicleHuman") : cache.getEngine().getSubType("VehicleHuman").info();
	}

	public void testAttributeWithGetInstances() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType("Car");
		Attribute vehiclePower = vehicle.setRelation("power");

		Generic myVehicle = vehicle.newInstance("myVehicle");
		Generic myCar = car.newInstance("myCar");

		myVehicle.setValue(vehiclePower, "123");
		myCar.setValue(vehiclePower, "256");

		assert vehiclePower.getInstances().filter(new Snapshot.Filter<Generic>() {

			@Override
			public boolean isSelected(Generic element) {
				return element.getValue() == null;
			}
		}).isEmpty();
		assert vehiclePower.getAllInstances().filter(new Snapshot.Filter<Generic>() {

			@Override
			public boolean isSelected(Generic element) {
				return element.getValue() == null;
			}
		}).isEmpty();
	}

	public void testAttributeWithGetDirectSubTypes() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType("Car");
		Attribute vehiclePower = vehicle.setRelation("power");

		Generic myVehicle = vehicle.newInstance("myVehicle");
		Generic myCar = car.newInstance("myCar");

		myVehicle.setValue(vehiclePower, "123");
		myCar.setValue(vehiclePower, "256");

		assert vehiclePower.getDirectSubTypes().isEmpty();
		assert vehiclePower.getSubTypes().isEmpty();
	}

	public void testAttributeWithGetInheritings() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType("Car");
		Attribute vehiclePower = vehicle.setRelation("power");

		Generic myVehicle = vehicle.newInstance("myVehicle");
		Generic myCar = car.newInstance("myCar");

		myVehicle.setValue(vehiclePower, "123");
		myCar.setValue(vehiclePower, "256");

		assert vehiclePower.getInheritings().filter(new Snapshot.Filter<Generic>() {
			@Override
			public boolean isSelected(Generic element) {
				return element.getValue() == null;
			}
		}).isEmpty();
	}

	public void cancelDefaultAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Attribute carPower = car.setProperty("power");
		Holder defaultPower = car.setValue(carPower, "233");
		Generic myCar = car.newInstance("myCar");
		assert myCar.getHolder(carPower).equals(defaultPower);

		Generic cancel = myCar.setValue(carPower, null);
		assert ((GenericImpl) myCar).getHolderByValue(defaultPower, null) == cancel;
		assert myCar.getHolder(carPower) == null : myCar.getHolder(carPower);

		myCar.setValue(carPower, "233");
		assert !cancel.isAlive();
		assert myCar.getValue(carPower).equals("233");
		assert ((GenericImpl) myCar).getHolderByValue(defaultPower, null) == null;
	}

	public void cancelDefaultAttributeKo() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		final Attribute carPower = car.setProperty("power");
		car.setValue(carPower, "233");
		final Generic mycar = car.newInstance("myCar");
		assert mycar.getValue(carPower).equals("233");
		mycar.setValue(carPower, null);
		assert mycar.getHolder(carPower) == null;
	}

	public void cancelDefaultRelation() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		final Relation carColor = car.setRelation("carColor", color);
		final Generic red = color.newInstance("red");
		final Link defaultColor = car.setLink(carColor, "defaultColor", red);
		assert defaultColor.isConcrete();
		final Generic myCar = car.newInstance("myCar");
		assert myCar.getTargets(carColor).contains(red);
		// new RollbackCatcher() {
		// @Override
		// public void intercept() {
		myCar.setLink(carColor, null, red);
		// }
		// }.assertIsCausedBy(PhantomConstraintViolationException.class);

		myCar.setLink(defaultColor, null, red);

		assert ((GenericImpl) myCar).getHolder(defaultColor, null, red) == null;
	}

	public void cancelDefaultRelationKo() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Generic myCar = car.newInstance("myCar");
		Type color = cache.newType("Color");
		Generic red = color.newInstance("red");
		Generic green = color.newInstance("green");
		Relation carColor = car.setRelation("carColor", color);

		car.bind(carColor, red);
		car.bind(carColor, green);
		assert myCar.getTargets(carColor).contains(red);
		assert myCar.getTargets(carColor).contains(green);

		try {
			myCar.setValue(carColor, null);
		} catch (IllegalStateException ignore) {

		}
		myCar.cancelAll(carColor, true, red);
		assert !myCar.getTargets(carColor).contains(red);
		assert myCar.getTargets(carColor).contains(green);
		myCar.bind(carColor, red);
		assert myCar.getTargets(carColor).contains(red);
		assert myCar.getTargets(carColor).contains(green);
		myCar.cancelAll(carColor, true);
		assert !myCar.getTargets(carColor).contains(green);
		assert !myCar.getTargets(carColor).contains(red);
		assert myCar.getTargets(carColor).isEmpty();
	}

	public void testTwoCancel() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Attribute carPower = car.setProperty("power");
		Holder defaultPower = car.setValue(carPower, "233");
		Generic mycar = car.newInstance("myCar");
		assert mycar.getValue(carPower).equals("233");
		mycar.cancelAll(defaultPower, true);
		assert mycar.getValue(carPower) == null;
		mycar.cancelAll(defaultPower, true);
		assert mycar.getValue(carPower) == null;
		mycar.clearAllConcrete(defaultPower);
		assert mycar.getValue(carPower).equals("233");
	}

	public void testTwoRestore() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Attribute carPower = car.setProperty("power");
		Generic myCar = car.newInstance("myCar");
		assert myCar.setValue(carPower, null) == null; // Do nothing;

		Holder defaultPower = car.setValue(carPower, "233");
		assert myCar.getValue(carPower).equals("233");
		myCar.setValue(defaultPower, null);
		assert myCar.getValue(carPower) == null;
		// ((GenericImpl) myCar).getHolderByValue( defaultPower, null).remove();
		myCar.clearAllConcrete(defaultPower);
		assert myCar.getValue(carPower).equals("233");
		myCar.clearAllConcrete(defaultPower);
		assert myCar.getValue(carPower).equals("233");
		myCar.cancelAll(defaultPower, true);
		assert myCar.getValue(carPower) == null;
	}

	public void testAnyCancelRestore() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Attribute carPower = car.setProperty("power");
		Holder defaultPower = car.setValue(carPower, "233");
		Generic mycar = car.newInstance("myCar");
		assert mycar.getValue(carPower).equals("233");
		mycar.cancelAll(defaultPower, true);
		mycar.clearAllConcrete(defaultPower);
		mycar.clearAllStructural(defaultPower);
		mycar.cancelAll(defaultPower, true);
		mycar.cancelAll(defaultPower, true);
		mycar.clearAllConcrete(defaultPower);
		assert mycar.getValue(carPower).equals("233");
	}

	public void testTwoCancelRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation("CarColor", color);
		Generic red = color.newInstance("Red");
		Link defaultCarColor = car.setLink(carColor, "defaultCarColor", red);
		Generic myCar = car.newInstance("myCar");
		assert myCar.getTargets(carColor).contains(red);
		myCar.cancelAll(defaultCarColor, true);
		assert myCar.getTargets(carColor).isEmpty();
		myCar.cancelAll(defaultCarColor, true);
		assert myCar.getTargets(carColor).isEmpty();
		myCar.clearAllConcrete(defaultCarColor);
		assert myCar.getTargets(carColor).contains(red);
	}

	public void testTwoRestoreRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setProperty("CarColor", color);
		Generic red = color.newInstance("Red");
		Link defaultCarColor = car.setLink(carColor, "defaultCarColor", red);
		Generic myCar = car.newInstance("myCar");
		assert myCar.getTargets(carColor).contains(red);
		// myCar.cancel( defaultCarColor);

		// myCar.setHolder( defaultCarColor, null, red);// phantomize
		// assert myCar.getTargets( carColor).isEmpty();
		//
		// ((GenericImpl) myCar).getHolderByValue( defaultCarColor, null, red).remove();// restore
		// assert myCar.getLink( carColor, red).equals(defaultCarColor);

		myCar.setLink(defaultCarColor, null, red); // phantomize
		assert myCar.getTargets(carColor).isEmpty();

		Link link = myCar.setLink(carColor, "toto", red);// restore
		assert myCar.getLink(carColor, red).equals(link);

	}

	public void testAnyCancelRestoreRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation("CarColor", color);
		Generic red = color.newInstance("Red");
		Link defaultCarColor = car.setLink(carColor, "defaultCarColor", red);
		Generic myCar = car.newInstance("myCar");
		assert myCar.getTargets(carColor).contains(red);
		myCar.cancelAll(defaultCarColor, true);
		myCar.clearAllConcrete(defaultCarColor);
		myCar.clearAllStructural(defaultCarColor);
		myCar.cancelAll(defaultCarColor, true);
		myCar.cancelAll(defaultCarColor, true);
		myCar.clearAllConcrete(defaultCarColor);
		assert myCar.getTargets(carColor).contains(red);
	}

}
