package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.exception.PropertyConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class PropertyConstraintTest extends AbstractTest {

	public void testSingleValueAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Attribute equipment = vehicle.setAttribute( "Equipment");
		equipment.enablePropertyConstraint();
		Generic myVehicle = vehicle.newInstance( "myVehicle");
		myVehicle.setValue( equipment, "ABS");
	}

	public void testMultipleValuesAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		final Attribute equipment = vehicle.setAttribute( "Equipment");
		equipment.enablePropertyConstraint();
		final Generic myVehicle = vehicle.newInstance( "myVehicle");
		Holder abs = myVehicle.setValue( equipment, "ABS");
		myVehicle.setValue( equipment, "GPS");
		assert !abs.isAlive();
	}

	public void testMultipleValuesAttributeWithoutConstraint() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Attribute equipment = vehicle.setAttribute( "Equipment");
		Generic myVehicle = vehicle.newInstance( "myVehicle");
		myVehicle.setValue( equipment, "ABS");
		myVehicle.setValue( equipment, "GPS");
	}

	public void testMultipleValuesAttributeWithDisabledConstraint() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Attribute equipment = vehicle.setAttribute( "Equipment");
		equipment.disablePropertyConstraint();
		Generic myVehicle = vehicle.newInstance( "myVehicle");
		myVehicle.setValue( equipment, "ABS");
		myVehicle.setValue( equipment, "GPS");
	}

	public void testBinaryRelationDifferentTarget() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Relation vehicleColor = vehicle.setRelation( "VehicleColor", color);
		vehicleColor.enablePropertyConstraint();
		Generic myVehicle = vehicle.newInstance( "MyVehicle");
		Generic red = color.newInstance( "red");
		Generic blue = color.newInstance( "blue");
		myVehicle.setLink( vehicleColor, "myVehicleColor", red);
		myVehicle.setLink( vehicleColor, "myVehicleColor", blue);
	}

	public void testBinaryRelationSameTarget() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		final Relation vehicleColor = vehicle.setRelation( "VehicleColor", color);
		vehicleColor.enablePropertyConstraint();
		final Generic myVehicle = vehicle.newInstance( "myVehicle");
		final Generic red = color.newInstance( "red");
		Link myVehicleRed = myVehicle.setLink( vehicleColor, "myVehicleRed", red);
		Link myVehicleRedAgain = myVehicle.setLink( vehicleColor, "myVehicleRedAgain", red);
		assert !myVehicleRed.isAlive();
		assert myVehicleRedAgain.isAlive();
	}

	public void testTernaryRelationDifferentTargets() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Type pilot = cache.newType("Pilot");
		Relation vehicleColor = vehicle.setRelation( "VehicleColor", color, pilot);
		vehicleColor.enablePropertyConstraint();
		Generic myVehicle = vehicle.newInstance( "myVehicle");
		Generic red = color.newInstance( "red");
		Generic myPilot = pilot.newInstance( "myPilot");
		Generic anotherPilot = pilot.newInstance( "anotherPilot");
		myVehicle.setLink( vehicleColor, "myVehicleRed", red, myPilot);
		myVehicle.setLink( vehicleColor, "myVehicleRed", red, anotherPilot);
	}

	public void testTernaryRelationSameTargets() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Type pilot = cache.newType("Pilot");
		final Relation vehicleColor = vehicle.setRelation( "VehicleColor", color, pilot);
		vehicleColor.enablePropertyConstraint();
		final Generic myVehicle = vehicle.newInstance( "myVehicle");
		final Generic red = color.newInstance( "red");
		final Generic myPilot = pilot.newInstance( "myPilot");
		Link myVehicleRed = myVehicle.setLink( vehicleColor, "myVehicleRed", red, myPilot);
		Link myVehicleRedAgain = myVehicle.setLink( vehicleColor, "myVehicleRedAgain", red, myPilot);
		assert !myVehicleRed.isAlive();
		assert myVehicleRedAgain.isAlive();
	}

	public void testSingleValueAttributeForSubtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType( "Car");
		Attribute equipment = vehicle.setAttribute( "Equipment");
		equipment.enablePropertyConstraint();
		Generic myCar = car.newInstance( "myCar");
		myCar.setValue( equipment, "ABS");
	}

	public void testMultipleValuesAttributeForSubtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType( "Car");
		final Attribute equipment = vehicle.setAttribute( "Equipment");
		equipment.enablePropertyConstraint();
		final Generic myCar = car.newInstance( "myCar");
		Holder absValue = myCar.setValue( equipment, "ABS");
		Holder gpsValue = myCar.setValue( equipment, "GPS");
		assert !absValue.isAlive();
		assert gpsValue.isAlive();
	}

	public void testMultipleValuesAttributeForSubtypeOtherWay() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = cache.newSubType("Car", vehicle);
		final Attribute equipment = vehicle.setAttribute( "Equipment");
		equipment.enablePropertyConstraint();
		final Generic myCar = car.newInstance( "myCar");
		Holder absValue = myCar.setValue( equipment, "ABS");
		Holder gpsValue = myCar.setValue( equipment, "GPS");
		assert !absValue.isAlive();
		assert gpsValue.isAlive();
	}

	public void testSameTarget() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType( "Car");
		Type color = cache.newType("Color");
		final Relation vehicleColor = vehicle.setRelation( "VehicleColor", color);
		vehicleColor.enablePropertyConstraint();
		final Generic myCar = car.newInstance( "myCar");
		final Generic red = color.newInstance( "red");
		Link myVehiclePower = myCar.setLink( vehicleColor, "myVehiclePower", red);
		Link myVehiclePower2 = myCar.setLink( vehicleColor, "myVehiclePower2", red);
		assert !myVehiclePower.isAlive();
		assert myVehiclePower2.isAlive();
	}

	public void testBinaryRelationBetweenSubtypeAndSameTarget() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType( "Car");
		final Type bike = car.newSubType( "Bike");
		Type color = cache.newType("Color");
		final Relation vehicleColor = vehicle.setRelation( "VehicleColor", color);
		vehicleColor.enablePropertyConstraint();
		final Generic myBike = bike.newInstance( "myBike");
		final Generic red = color.newInstance( "red");
		Link myVehicleRed = myBike.setLink( vehicleColor, "myVehicleRed", red);
		Link myVehicleRedAgain = myBike.setLink( vehicleColor, "myVehicleRedAgain", red);
		assert !myVehicleRed.isAlive();
		assert myVehicleRedAgain.isAlive();
	}

	public void testMutlipleInstances() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Generic myVehicle = vehicle.newAnonymousInstance();
		Generic myVehicle2 = vehicle.newAnonymousInstance();
		assert myVehicle != myVehicle2 : myVehicle.info() + myVehicle2.info();
	}

	public void testMultipleDefaultValuesAttribute1() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type vehicle = cache.newType("Vehicle");
		final Attribute equipment = vehicle.setAttribute( "Equipment");
		equipment.enablePropertyConstraint();
		final Generic myVehicle = vehicle.newInstance( "myVehicle");
		Holder absValue = vehicle.setValue( equipment, "ABS");
		Holder gpsValue = myVehicle.setValue( equipment, "GPS");
		// Todo check remove old value
		assert absValue.isAlive();
		assert myVehicle.getValue( equipment).equals(gpsValue.getValue());
	}

	public void testMultipleDefaultValuesAttribute2() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type vehicle = cache.newType("Vehicle");
		final Attribute equipment = vehicle.setAttribute( "Equipment");
		equipment.enablePropertyConstraint();
		final Generic myVehicle = vehicle.newInstance( "myVehicle");

		final Generic result = myVehicle.setValue( equipment, "ABS");

		assert vehicle.getAllInstances().contains(myVehicle);

		new RollbackCatcher() {

			@Override
			public void intercept() {
				vehicle.setValue( equipment, "GPS");
				assert !result.isAlive();
				assert ((GenericImpl) result).reFind() != null;
			}
		}.assertIsCausedBy(PropertyConstraintViolationException.class);
	}

	public void testOK() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carOutsideColor = car.setRelation( "outside", color);
		Generic myBmw = car.newInstance( "myBmw");
		Generic red = color.newInstance( "red");
		carOutsideColor.enablePropertyConstraint();
		// carOutsideColor.enableSingularConstraint( Statics.BASE_POSITION);
		myBmw.setLink( carOutsideColor, "20%", red);
		myBmw.setLink( carOutsideColor, "40%", red);
		myBmw.getLink( carOutsideColor, red);
	}

}
