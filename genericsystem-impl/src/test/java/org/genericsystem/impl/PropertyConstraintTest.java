package org.genericsystem.impl;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.exception.PropertyConstraintViolationException;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Holder;
import org.genericsystem.api.generic.Link;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Type;
import org.genericsystem.impl.core.GenericImpl;
import org.testng.annotations.Test;

@Test
public class PropertyConstraintTest extends AbstractTest {

	public void testSingleValueAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute equipment = vehicle.setAttribute(cache, "Equipment");
		equipment.enablePropertyConstraint(cache);
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		myVehicle.setValue(cache, equipment, "ABS");
	}

	public void testMultipleValuesAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		final Attribute equipment = vehicle.setAttribute(cache, "Equipment");
		equipment.enablePropertyConstraint(cache);
		final Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		Holder abs = myVehicle.setValue(cache, equipment, "ABS");
		myVehicle.setValue(cache, equipment, "GPS");
		assert !abs.isAlive(cache);
	}

	public void testMultipleValuesAttributeWithoutConstraint() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute equipment = vehicle.setAttribute(cache, "Equipment");
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		myVehicle.setValue(cache, equipment, "ABS");
		myVehicle.setValue(cache, equipment, "GPS");
	}

	public void testMultipleValuesAttributeWithDisabledConstraint() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute equipment = vehicle.setAttribute(cache, "Equipment");
		equipment.disablePropertyConstraint(cache);
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		myVehicle.setValue(cache, equipment, "ABS");
		myVehicle.setValue(cache, equipment, "GPS");
	}

	public void testBinaryRelationDifferentTarget() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Relation vehicleColor = vehicle.setRelation(cache, "VehicleColor", color);
		vehicleColor.enablePropertyConstraint(cache);
		Generic myVehicle = vehicle.newInstance(cache, "MyVehicle");
		Generic red = color.newInstance(cache, "red");
		Generic blue = color.newInstance(cache, "blue");
		myVehicle.setLink(cache, vehicleColor, "myVehicleColor", red);
		myVehicle.setLink(cache, vehicleColor, "myVehicleColor", blue);
	}

	public void testBinaryRelationSameTarget() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		final Relation vehicleColor = vehicle.setRelation(cache, "VehicleColor", color);
		vehicleColor.enablePropertyConstraint(cache);
		final Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		final Generic red = color.newInstance(cache, "red");
		Link myVehicleRed = myVehicle.setLink(cache, vehicleColor, "myVehicleRed", red);
		Link myVehicleRedAgain = myVehicle.setLink(cache, vehicleColor, "myVehicleRedAgain", red);
		assert !myVehicleRed.isAlive(cache);
		assert myVehicleRedAgain.isAlive(cache);
	}

	public void testTernaryRelationDifferentTargets() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Type pilot = cache.newType("Pilot");
		Relation vehicleColor = vehicle.setRelation(cache, "VehicleColor", color, pilot);
		vehicleColor.enablePropertyConstraint(cache);
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		Generic red = color.newInstance(cache, "red");
		Generic myPilot = pilot.newInstance(cache, "myPilot");
		Generic anotherPilot = pilot.newInstance(cache, "anotherPilot");
		myVehicle.setLink(cache, vehicleColor, "myVehicleRed", red, myPilot);
		myVehicle.setLink(cache, vehicleColor, "myVehicleRed", red, anotherPilot);
	}

	public void testTernaryRelationSameTargets() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Type pilot = cache.newType("Pilot");
		final Relation vehicleColor = vehicle.setRelation(cache, "VehicleColor", color, pilot);
		vehicleColor.enablePropertyConstraint(cache);
		final Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		final Generic red = color.newInstance(cache, "red");
		final Generic myPilot = pilot.newInstance(cache, "myPilot");
		Link myVehicleRed = myVehicle.setLink(cache, vehicleColor, "myVehicleRed", red, myPilot);
		Link myVehicleRedAgain = myVehicle.setLink(cache, vehicleColor, "myVehicleRedAgain", red, myPilot);
		assert !myVehicleRed.isAlive(cache);
		assert myVehicleRedAgain.isAlive(cache);
	}

	public void testSingleValueAttributeForSubtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Attribute equipment = vehicle.setAttribute(cache, "Equipment");
		equipment.enablePropertyConstraint(cache);
		Generic myCar = car.newInstance(cache, "myCar");
		myCar.setValue(cache, equipment, "ABS");
	}

	public void testMultipleValuesAttributeForSubtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		final Attribute equipment = vehicle.setAttribute(cache, "Equipment");
		equipment.enablePropertyConstraint(cache);
		final Generic myCar = car.newInstance(cache, "myCar");
		Holder absValue = myCar.setValue(cache, equipment, "ABS");
		Holder gpsValue = myCar.setValue(cache, equipment, "GPS");
		assert !absValue.isAlive(cache);
		assert gpsValue.isAlive(cache);
	}

	public void testMultipleValuesAttributeForSubtypeOtherWay() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = cache.newSubType("Car", vehicle);
		final Attribute equipment = vehicle.setAttribute(cache, "Equipment");
		equipment.enablePropertyConstraint(cache);
		final Generic myCar = car.newInstance(cache, "myCar");
		Holder absValue = myCar.setValue(cache, equipment, "ABS");
		Holder gpsValue = myCar.setValue(cache, equipment, "GPS");
		assert !absValue.isAlive(cache);
		assert gpsValue.isAlive(cache);
	}

	public void testSameTarget() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Type color = cache.newType("Color");
		final Relation vehicleColor = vehicle.setRelation(cache, "VehicleColor", color);
		vehicleColor.enablePropertyConstraint(cache);
		final Generic myCar = car.newInstance(cache, "myCar");
		final Generic red = color.newInstance(cache, "red");
		Link myVehiclePower = myCar.setLink(cache, vehicleColor, "myVehiclePower", red);
		Link myVehiclePower2 = myCar.setLink(cache, vehicleColor, "myVehiclePower2", red);
		assert !myVehiclePower.isAlive(cache);
		assert myVehiclePower2.isAlive(cache);
	}

	public void testBinaryRelationBetweenSubtypeAndSameTarget() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		final Type bike = car.newSubType(cache, "Bike");
		Type color = cache.newType("Color");
		final Relation vehicleColor = vehicle.setRelation(cache, "VehicleColor", color);
		vehicleColor.enablePropertyConstraint(cache);
		final Generic myBike = bike.newInstance(cache, "myBike");
		final Generic red = color.newInstance(cache, "red");
		Link myVehicleRed = myBike.setLink(cache, vehicleColor, "myVehicleRed", red);
		Link myVehicleRedAgain = myBike.setLink(cache, vehicleColor, "myVehicleRedAgain", red);
		assert !myVehicleRed.isAlive(cache);
		assert myVehicleRedAgain.isAlive(cache);
	}

	public void testMutlipleInstances() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Generic myVehicle = vehicle.newAnonymousInstance(cache);
		Generic myVehicle2 = vehicle.newAnonymousInstance(cache);
		assert myVehicle != myVehicle2 : myVehicle.info() + myVehicle2.info();
	}

	public void testMultipleDefaultValuesAttribute1() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		final Type vehicle = cache.newType("Vehicle");
		final Attribute equipment = vehicle.setAttribute(cache, "Equipment");
		equipment.enablePropertyConstraint(cache);
		final Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		Holder absValue = vehicle.setValue(cache, equipment, "ABS");
		Holder gpsValue = myVehicle.setValue(cache, equipment, "GPS");
		// Todo check remove old value
		assert absValue.isAlive(cache);
		assert myVehicle.getValue(cache, equipment).equals(gpsValue.getValue());
	}

	public void testMultipleDefaultValuesAttribute2() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		final Type vehicle = cache.newType("Vehicle");
		final Attribute equipment = vehicle.setAttribute(cache, "Equipment");
		equipment.enablePropertyConstraint(cache);
		final Generic myVehicle = vehicle.newInstance(cache, "myVehicle");

		final Generic result = myVehicle.setValue(cache, equipment, "ABS");

		assert vehicle.getAllInstances(cache).contains(myVehicle);

		new RollbackCatcher() {

			@Override
			public void intercept() {
				vehicle.setValue(cache, equipment, "GPS");
				assert !result.isAlive(cache);
				assert ((GenericImpl) result).reFind(cache) != null;
			}
		}.assertIsCausedBy(PropertyConstraintViolationException.class);
	}

	public void testOK() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carOutsideColor = car.setRelation(cache, "outside", color);
		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic red = color.newInstance(cache, "red");
		carOutsideColor.enablePropertyConstraint(cache);
		// carOutsideColor.enableSingularConstraint(cache, Statics.BASE_POSITION);
		myBmw.setLink(cache, carOutsideColor, "20%", red);
		myBmw.setLink(cache, carOutsideColor, "40%", red);
		myBmw.getLink(cache, carOutsideColor, red);
	}

}
