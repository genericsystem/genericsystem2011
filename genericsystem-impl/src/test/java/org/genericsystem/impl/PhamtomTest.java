package org.genericsystem.impl;

import java.util.Iterator;
import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Snapshot;
import org.genericsystem.core.Statics;
import org.genericsystem.exception.UniqueStructuralValueConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class PhamtomTest extends AbstractTest {

	public void testAliveWithStructural() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Attribute vehiclePower = vehicle.setAttribute(cache, "power");

		assert car.getAttributes(cache).contains(vehiclePower);
		car.cancel(cache, vehiclePower);
		Iterator<Generic> iterator = ((GenericImpl) car).attributesIterator(cache, vehiclePower, true);
		Generic phantom = iterator.next();
		car.restore(cache, vehiclePower);
		assert !phantom.isAlive(cache);
	}

	public void testAliveWithConcrete() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute(cache, "power");
		Holder defaultPower = vehicle.setValue(cache, vehiclePower, "123");
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");

		assert myVehicle.getValue(cache, vehiclePower) == "123";
		myVehicle.cancel(cache, defaultPower);
		Iterator<Generic> iterator = ((GenericImpl) myVehicle).concreteIterator(cache, vehiclePower, Statics.BASE_POSITION, true);
		Generic phantom = iterator.next();
		myVehicle.restore(cache, defaultPower);
		assert !phantom.isAlive(cache);
	}

	public void cancelAttribute() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		final Type vehicle = cache.newType("Vehicle");
		final Attribute vehiclePower = vehicle.setAttribute(cache, "power");

		assert vehicle.getAttributes(cache).contains(vehiclePower);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				vehicle.cancel(cache, vehiclePower);
			}
		}.assertIsCausedBy(IllegalStateException.class);
		assert vehicle.getAttributes(cache).contains(vehiclePower);
	}

	public void cancelAttributeWithInheritsBase() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Attribute vehiclePower = vehicle.setAttribute(cache, "power");

		assert car.getAttributes(cache).contains(vehiclePower);
		car.cancel(cache, vehiclePower);
		assert !car.getAttributes(cache).contains(vehiclePower);
	}

	public void cancelAttributeWithInheritsAttribute() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		final Type car = vehicle.newSubType(cache, "Car");
		final Attribute vehiclePower = vehicle.setAttribute(cache, "power");

		assert car.getAttributes(cache).contains(vehiclePower);
		Attribute carPower = car.setAttribute(cache, "power");
		assert car.getAttributes(cache).contains(carPower);
		assert carPower.inheritsFrom(vehiclePower);

		new RollbackCatcher() {
			@Override
			public void intercept() {
				car.cancel(cache, vehiclePower);
			}
		}.assertIsCausedBy(IllegalStateException.class);
	}

	public void cancelAndRestoreRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation vehicleHuman = vehicle.setRelation(cache, "VehicleHuman", human);
		Type car = vehicle.newSubType(cache, "Car");

		assert vehicle.getRelations(cache).size() == 1;
		assert vehicle.getRelations(cache).contains(vehicleHuman);
		assert car.getRelations(cache).size() == 1;
		assert car.getRelations(cache).contains(vehicleHuman);

		// car.cancel(cache, vehicleHuman);
		((GenericImpl) car).setSubAttribute(cache, vehicleHuman, null, human);

		assert vehicle.getRelations(cache).size() == 1;
		assert car.getRelations(cache).isEmpty();

		// car.restore(cache, vehicleHuman);
		car.getAttribute(cache, vehicleHuman, null).remove(cache);

		assert vehicle.getRelations(cache).size() == 1;
		assert vehicle.getRelations(cache).contains(vehicleHuman);
		assert car.getRelations(cache).size() == 1 : car.getRelations(cache);
		assert car.getRelations(cache).contains(vehicleHuman) : car.getRelations(cache);
	}

	public void testPhantomHierarchyRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation vehicleHuman = vehicle.setRelation(cache, "VehicleHuman", human);
		Type car = vehicle.newSubType(cache, "Car");
		Type convertible = car.newSubType(cache, "Convertible");

		assert vehicle.getRelations(cache).contains(vehicleHuman);
		assert car.getRelations(cache).contains(vehicleHuman);
		assert convertible.getRelations(cache).contains(vehicleHuman);

		car.cancel(cache, vehicleHuman);
		assert vehicle.getRelations(cache).contains(vehicleHuman);
		assert car.getRelations(cache).size() == 0;
		assert !car.getRelations(cache).contains(vehicleHuman);
		assert convertible.getRelations(cache).size() == 0;
		assert !convertible.getRelations(cache).contains(vehicleHuman);

		car.restore(cache, vehicleHuman);
		assert vehicle.getRelations(cache).contains(vehicleHuman);
		assert car.getRelations(cache).contains(vehicleHuman);
		assert convertible.getRelations(cache).contains(vehicleHuman);

		convertible.cancel(cache, vehicleHuman);
		assert vehicle.getRelations(cache).contains(vehicleHuman);
		assert car.getRelations(cache).contains(vehicleHuman);
		assert convertible.getRelations(cache).size() == 0;
		assert !convertible.getRelations(cache).contains(vehicleHuman);
	}

	public void testPhantomMultiHierachyRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Relation vehicleColor = vehicle.setRelation(cache, "VehicleColor", color);
		Type car = vehicle.newSubType(cache, "Car");
		Type breakVehicle = car.newSubType(cache, "BreakVehicle");
		Type convertible = car.newSubType(cache, "Convertible");

		car.cancel(cache, vehicleColor);
		assert vehicle.getRelations(cache).size() == 1;
		assert car.getRelations(cache).size() == 0;
		assert breakVehicle.getRelations(cache).size() == 0;
		assert convertible.getRelations(cache).size() == 0;

		car.restore(cache, vehicleColor);
		assert vehicle.getRelations(cache).size() == 1;
		assert car.getRelations(cache).get(0).equals(vehicleColor);
		assert breakVehicle.getRelations(cache).get(0).equals(vehicleColor);
		assert convertible.getRelations(cache).get(0).equals(vehicleColor);
	}

	public void testRelationsWithSameName() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		final Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		final Type color = cache.newType("Color");
		vehicle.newSubType(cache, "Car");

		vehicle.setRelation(cache, "VehicleHuman", human);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				vehicle.setRelation(cache, "VehicleHuman", color);
			}
		}.assertIsCausedBy(UniqueStructuralValueConstraintViolationException.class);
	}

	public void testAttributeWithGetInstances() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Attribute vehiclePower = vehicle.setRelation(cache, "power");

		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		Generic myCar = car.newInstance(cache, "myCar");

		myVehicle.setValue(cache, vehiclePower, "123");
		myCar.setValue(cache, vehiclePower, "256");

		assert vehiclePower.getInstances(cache).filter(new Snapshot.Filter<Generic>() {

			@Override
			public boolean isSelected(Generic element) {
				return element.getValue() == null;
			}
		}).isEmpty();
		assert vehiclePower.getAllInstances(cache).filter(new Snapshot.Filter<Generic>() {

			@Override
			public boolean isSelected(Generic element) {
				return element.getValue() == null;
			}
		}).isEmpty();
	}

	public void testAttributeWithGetDirectSubTypes() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Attribute vehiclePower = vehicle.setRelation(cache, "power");

		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		Generic myCar = car.newInstance(cache, "myCar");

		myVehicle.setValue(cache, vehiclePower, "123");
		myCar.setValue(cache, vehiclePower, "256");

		assert vehiclePower.getDirectSubTypes(cache).isEmpty();
		assert vehiclePower.getSubTypes(cache).isEmpty();
	}

	public void testAttributeWithGetInheritings() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Attribute vehiclePower = vehicle.setRelation(cache, "power");

		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		Generic myCar = car.newInstance(cache, "myCar");

		myVehicle.setValue(cache, vehiclePower, "123");
		myCar.setValue(cache, vehiclePower, "256");

		assert vehiclePower.getInheritings(cache).filter(new Snapshot.Filter<Generic>() {
			@Override
			public boolean isSelected(Generic element) {
				return element.getValue() == null;
			}
		}).isEmpty();
	}

	public void cancelDefaultAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Attribute carPower = car.setProperty(cache, "power");
		Holder defaultPower = car.setValue(cache, carPower, "233");
		Generic myCar = car.newInstance(cache, "myCar");
		assert myCar.getHolder(cache, carPower).equals(defaultPower);

		// myCar.cancel(cache, defaultPower);
		Generic cancel = myCar.setValue(cache, carPower, null);
		assert ((GenericImpl) myCar).getHolderByValue(cache, defaultPower, null) == cancel;
		assert myCar.getHolder(cache, carPower) == null : myCar.getHolder(cache, carPower);

		// ((GenericImpl) myCar).getHolderByValue(cache, defaultPower, null).remove(cache);
		Holder newDefaultPower = myCar.setValue(cache, carPower, "233");
		// assert false : newDefaultPower.info();
		assert !cancel.isAlive(cache);
		assert myCar.getValue(cache, carPower).equals("233");
		assert ((GenericImpl) myCar).getHolderByValue(cache, defaultPower, null) == null;
	}

	public void cancelDefaultAttributeKo() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		final Attribute carPower = car.setProperty(cache, "power");
		car.setValue(cache, carPower, "233");
		final Generic mycar = car.newInstance(cache, "myCar");
		assert mycar.getValue(cache, carPower).equals("233");
		new RollbackCatcher() {

			@Override
			public void intercept() {
				mycar.cancel(cache, carPower);
			}
		}.assertIsCausedBy(IllegalStateException.class);
	}

	public void cancelDefaultRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation(cache, "carColor", color);
		Generic red = color.newInstance(cache, "red");
		Link defaultColor = car.setLink(cache, carColor, "defaultColor", red);
		assert defaultColor.isConcrete();
		Generic myCar = car.newInstance(cache, "myCar");
		assert myCar.getTargets(cache, carColor).contains(red);
		myCar.cancel(cache, defaultColor);
		assert myCar.getTargets(cache, carColor).isEmpty();
	}

	public void cancelDefaultRelationKo() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		final Relation carColor = car.setRelation(cache, "carColor", color);
		Generic red = color.newInstance(cache, "red");
		car.setLink(cache, carColor, "defaultColor", red);
		final Generic myCar = car.newInstance(cache, "myCar");
		assert myCar.getTargets(cache, carColor).contains(red);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				myCar.cancel(cache, carColor);
			}
		}.assertIsCausedBy(IllegalStateException.class);
	}

	public void testTwoCancel() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Attribute carPower = car.setProperty(cache, "power");
		Holder defaultPower = car.setValue(cache, carPower, "233");
		Generic mycar = car.newInstance(cache, "myCar");
		assert mycar.getValue(cache, carPower).equals("233");
		mycar.cancel(cache, defaultPower);
		assert mycar.getValue(cache, carPower) == null;
		mycar.cancel(cache, defaultPower);
		assert mycar.getValue(cache, carPower) == null;
		mycar.restore(cache, defaultPower);
		assert mycar.getValue(cache, carPower).equals("233");
	}

	public void testTwoRestore() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Attribute carPower = car.setProperty(cache, "power");
		Holder defaultPower = car.setValue(cache, carPower, "233");
		Generic mycar = car.newInstance(cache, "myCar");
		assert mycar.getValue(cache, carPower).equals("233");
		mycar.cancel(cache, defaultPower);
		assert mycar.getValue(cache, carPower) == null;
		mycar.restore(cache, defaultPower);
		assert mycar.getValue(cache, carPower).equals("233");
		mycar.restore(cache, defaultPower);
		assert mycar.getValue(cache, carPower).equals("233");
		mycar.cancel(cache, defaultPower);
		assert mycar.getValue(cache, carPower) == null;
	}

	public void testAnyCancelRestore() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Attribute carPower = car.setProperty(cache, "power");
		Holder defaultPower = car.setValue(cache, carPower, "233");
		Generic mycar = car.newInstance(cache, "myCar");
		assert mycar.getValue(cache, carPower).equals("233");
		mycar.cancel(cache, defaultPower);
		mycar.restore(cache, defaultPower);
		mycar.restore(cache, defaultPower);
		mycar.cancel(cache, defaultPower);
		mycar.cancel(cache, defaultPower);
		mycar.restore(cache, defaultPower);
		assert mycar.getValue(cache, carPower).equals("233");
	}

	public void testTwoCancelRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation(cache, "CarColor", color);
		Generic red = color.newInstance(cache, "Red");
		Link defaultCarColor = car.setLink(cache, carColor, "defaultCarColor", red);
		Generic myCar = car.newInstance(cache, "myCar");
		assert myCar.getTargets(cache, carColor).contains(red);
		myCar.cancel(cache, defaultCarColor);
		assert myCar.getTargets(cache, carColor).isEmpty();
		myCar.cancel(cache, defaultCarColor);
		assert myCar.getTargets(cache, carColor).isEmpty();
		myCar.restore(cache, defaultCarColor);
		assert myCar.getTargets(cache, carColor).contains(red);
	}

	public void testTwoRestoreRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation(cache, "CarColor", color);
		Generic red = color.newInstance(cache, "Red");
		Link defaultCarColor = car.setLink(cache, carColor, "defaultCarColor", red);
		Generic myCar = car.newInstance(cache, "myCar");
		assert myCar.getTargets(cache, carColor).contains(red);
		myCar.cancel(cache, defaultCarColor);
		assert myCar.getTargets(cache, carColor).isEmpty();
		myCar.restore(cache, defaultCarColor);
		assert myCar.getTargets(cache, carColor).contains(red);
		myCar.restore(cache, defaultCarColor);
		assert myCar.getTargets(cache, carColor).contains(red);
		myCar.cancel(cache, defaultCarColor);
		assert myCar.getTargets(cache, carColor).isEmpty();
	}

	public void testAnyCancelRestoreRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation(cache, "CarColor", color);
		Generic red = color.newInstance(cache, "Red");
		Link defaultCarColor = car.setLink(cache, carColor, "defaultCarColor", red);
		Generic myCar = car.newInstance(cache, "myCar");
		assert myCar.getTargets(cache, carColor).contains(red);
		myCar.cancel(cache, defaultCarColor);
		myCar.restore(cache, defaultCarColor);
		myCar.restore(cache, defaultCarColor);
		myCar.cancel(cache, defaultCarColor);
		myCar.cancel(cache, defaultCarColor);
		myCar.restore(cache, defaultCarColor);
		assert myCar.getTargets(cache, carColor).contains(red);
	}

}
