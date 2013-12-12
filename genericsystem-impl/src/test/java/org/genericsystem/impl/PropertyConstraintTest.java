package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Statics;
import org.genericsystem.exception.SingularConstraintViolationException;
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
		Type vehicle = cache.addType("Vehicle");
		Attribute equipment = vehicle.setAttribute("Equipment");
		equipment.enablePropertyConstraint();
		Generic myVehicle = vehicle.addInstance("myVehicle");
		myVehicle.setValue(equipment, "ABS");
	}

	public void testMultipleValuesAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		final Attribute equipment = vehicle.setAttribute("Equipment");
		equipment.enablePropertyConstraint();
		final Generic myVehicle = vehicle.addInstance("myVehicle");
		Holder abs = myVehicle.setValue(equipment, "ABS");
		myVehicle.setValue(equipment, "GPS");
		assert myVehicle.getValues(equipment).get(0).equals("GPS");
		assert myVehicle.getValues(equipment).size() == 1;
		assert !abs.isAlive();
	}

	public void testMultipleValuesAttributeWithoutConstraint() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute equipment = vehicle.setAttribute("Equipment");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		myVehicle.setValue(equipment, "ABS");
		myVehicle.setValue(equipment, "GPS");
		assert myVehicle.getValues(equipment).get(0).equals("ABS");
		assert myVehicle.getValues(equipment).get(1).equals("GPS");
		assert myVehicle.getValues(equipment).size() == 2;
	}

	public void testMultipleValuesAttributeWithDisabledConstraint() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute equipment = vehicle.setAttribute("Equipment");
		equipment.disablePropertyConstraint();
		Generic myVehicle = vehicle.addInstance("myVehicle");
		myVehicle.setValue(equipment, "ABS");
		myVehicle.setValue(equipment, "GPS");
		assert myVehicle.getValues(equipment).get(0).equals("ABS");
		assert myVehicle.getValues(equipment).get(1).equals("GPS");
		assert myVehicle.getValues(equipment).size() == 2;
	}

	public void testBinaryRelationDifferentTarget() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type color = cache.addType("Color");
		Relation vehicleColor = vehicle.setRelation("VehicleColor", color);

		vehicleColor.enablePropertyConstraint();
		Generic myVehicle = vehicle.addInstance("MyVehicle");
		Generic red = color.addInstance("red");
		Generic blue = color.addInstance("blue");
		Link myVehicleRed = myVehicle.setLink(vehicleColor, "myVehicleRed", red);
		myVehicle.setLink(vehicleColor, "myVehicleBlue", blue);
		assert myVehicleRed.isAlive();
	}

	public void testBinaryRelationSameTarget() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type color = cache.addType("Color");
		final Relation vehicleColor = vehicle.setRelation("VehicleColor", color);
		vehicleColor.enablePropertyConstraint();
		final Generic myVehicle = vehicle.addInstance("myVehicle");
		final Generic red = color.addInstance("red");
		Link myVehicleRed = myVehicle.setLink(vehicleColor, "myVehicleRed", red);
		Link myVehicleRedAgain = myVehicle.setLink(vehicleColor, "myVehicleRedAgain", red);
		assert !myVehicleRed.isAlive();
		assert myVehicleRedAgain.isAlive();
	}

	public void testTernaryRelationDifferentTargets() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type color = cache.addType("Color");
		Type pilot = cache.addType("Pilot");
		Relation vehicleColor = vehicle.setRelation("VehicleColor", color, pilot);
		vehicleColor.enablePropertyConstraint();
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic red = color.addInstance("red");
		Generic myPilot = pilot.addInstance("myPilot");
		Generic anotherPilot = pilot.addInstance("anotherPilot");
		myVehicle.setLink(vehicleColor, "myVehicleRed", red, myPilot);
		myVehicle.setLink(vehicleColor, "myVehicleRed", red, anotherPilot);
	}

	public void testTernaryRelationSameTargets() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type color = cache.addType("Color");
		Type pilot = cache.addType("Pilot");
		final Relation vehicleColor = vehicle.setRelation("VehicleColor", color, pilot);
		vehicleColor.enablePropertyConstraint();
		final Generic myVehicle = vehicle.addInstance("myVehicle");
		final Generic red = color.addInstance("red");
		final Generic myPilot = pilot.addInstance("myPilot");
		Link myVehicleRed = myVehicle.setLink(vehicleColor, "myVehicleRed", red, myPilot);
		Link myVehicleRedAgain = myVehicle.setLink(vehicleColor, "myVehicleRedAgain", red, myPilot);
		assert !myVehicleRed.isAlive();
		assert myVehicleRedAgain.isAlive();
	}

	public void testSingleValueAttributeForSubtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		Attribute equipment = vehicle.setAttribute("Equipment");
		equipment.enablePropertyConstraint();
		Generic myCar = car.addInstance("myCar");
		myCar.setValue(equipment, "ABS");
	}

	public void testMultipleValuesAttributeForTypeAndSubtypeKO() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");

		final Attribute equipment = vehicle.setAttribute("Equipment");
		equipment.enablePropertyConstraint();

		final Attribute equipment2 = car.setAttribute("Equipment");

		final Generic myCar = car.addInstance("myCar");
		myCar.setValue(equipment2, "ABS");
		myCar.setValue(equipment2, "GPS");

		assert myCar.getValues(equipment2).get(0).equals("GPS");
		assert myCar.getValues(equipment2).size() == 1;
	}

	public void testMultipleValuesAttributeForTypeAndSubtypeOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");

		final Attribute equipment = vehicle.setAttribute("Equipment");
		equipment.enablePropertyConstraint();

		final Attribute equipment2 = car.setAttribute("Equipment");
		equipment2.disablePropertyConstraint();

		Generic myVehicle = vehicle.addInstance("myVehicle");
		myVehicle.setValue(equipment, "ABS");
		myVehicle.setValue(equipment, "CLIM");

		final Generic myCar = car.addInstance("myCar");
		myCar.setValue(equipment, "ABS");
		myCar.setValue(equipment, "GPS");

		assert myVehicle.getValues(equipment).get(0).equals("CLIM");
		assert myVehicle.getValues(equipment).size() == 1;

		assert myCar.getValues(equipment2).get(0).equals("ABS");
		assert myCar.getValues(equipment2).get(1).equals("GPS");
		assert myCar.getValues(equipment2).size() == 2;
	}

	public void testMultipleValuesAttributeForSubtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		final Attribute equipment = vehicle.setAttribute("Equipment");
		equipment.enablePropertyConstraint();
		final Generic myCar = car.addInstance("myCar");
		Holder absValue = myCar.setValue(equipment, "ABS");
		Holder gpsValue = myCar.setValue(equipment, "GPS");
		assert !absValue.isAlive();
		assert gpsValue.isAlive();
	}

	public void testMultipleValuesAttributeForSubtypeOtherWay() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = cache.addType("Car", vehicle);
		final Attribute equipment = vehicle.setAttribute("Equipment");
		equipment.enablePropertyConstraint();
		final Generic myCar = car.addInstance("myCar");
		Holder absValue = myCar.setValue(equipment, "ABS");
		Holder gpsValue = myCar.setValue(equipment, "GPS");
		assert !absValue.isAlive();
		assert gpsValue.isAlive();
	}

	public void testSameTarget() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		Type color = cache.addType("Color");
		final Relation vehicleColor = vehicle.setRelation("VehicleColor", color);
		vehicleColor.enablePropertyConstraint();
		final Generic myCar = car.addInstance("myCar");
		final Generic red = color.addInstance("red");
		Link myVehiclePower = myCar.setLink(vehicleColor, "myVehiclePower", red);
		Link myVehiclePower2 = myCar.setLink(vehicleColor, "myVehiclePower2", red);
		assert !myVehiclePower.isAlive();
		assert myVehiclePower2.isAlive();
	}

	public void testBinaryRelationBetweenSubtypeAndSameTarget() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		final Type bike = car.addSubType("Bike");
		Type color = cache.addType("Color");
		final Relation vehicleColor = vehicle.setRelation("VehicleColor", color);
		vehicleColor.enablePropertyConstraint();
		final Generic myBike = bike.addInstance("myBike");
		final Generic red = color.addInstance("red");
		Link myVehicleRed = myBike.setLink(vehicleColor, "myVehicleRed", red);
		Link myVehicleRedAgain = myBike.setLink(vehicleColor, "myVehicleRedAgain", red);
		assert !myVehicleRed.isAlive();
		assert myVehicleRedAgain.isAlive();
	}

	public void testMutlipleInstances() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Generic myVehicle = vehicle.addAnonymousInstance();
		Generic myVehicle2 = vehicle.addAnonymousInstance();
		assert myVehicle != myVehicle2 : myVehicle.info() + myVehicle2.info();
	}

	public void testMultipleDefaultValuesAttribute1() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type vehicle = cache.addType("Vehicle");
		final Attribute equipment = vehicle.setAttribute("Equipment");
		equipment.enablePropertyConstraint();
		final Generic myVehicle = vehicle.addInstance("myVehicle");
		Holder absValue = vehicle.setValue(equipment, "ABS");
		Holder gpsValue = myVehicle.setValue(equipment, "GPS");
		// Todo check remove old value
		assert absValue.isAlive();
		assert myVehicle.getValue(equipment).equals(gpsValue.getValue());
		assert vehicle.getValue(equipment).equals(absValue.getValue());
	}

	public void testMultipleDefaultValuesAttribute2() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type vehicle = cache.addType("Vehicle");
		final Attribute power = vehicle.setAttribute("Power");
		power.enablePropertyConstraint();
		final Generic myVehicle = vehicle.addInstance("myVehicle");
		Holder myVehicle235 = myVehicle.setValue(power, 235);
		Holder vehicle233 = vehicle.setValue(power, 233);
		assert !myVehicle235.isAlive();
		assert vehicle233.isAlive();
		assert myVehicle.getHolder(power).inheritsFrom(vehicle233);
		assert myVehicle.getValue(power).equals(235);
		assert vehicle.getValue(power).equals(233);
	}

	public void testOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type color = cache.addType("Color");
		Relation carOutsideColor = car.setRelation("outside", color);
		Generic myBmw = car.addInstance("myBmw");
		Generic red = color.addInstance("red");
		carOutsideColor.enablePropertyConstraint();
		myBmw.setLink(carOutsideColor, "20%", red);
		myBmw.setLink(carOutsideColor, "40%", red);
		assert myBmw.getLink(carOutsideColor, red).getValue().equals("40%");
	}

	public void testOK2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type color = cache.addType("Color");
		Relation carOutsideColor = car.setRelation("outside", color);
		Generic myBmw = car.addInstance("myBmw");
		Generic red = color.addInstance("red");
		carOutsideColor.enableSingularConstraint(Statics.BASE_POSITION);
		myBmw.setLink(carOutsideColor, "20%", red);
		myBmw.setLink(carOutsideColor, "40%", red);
		assert myBmw.getLink(carOutsideColor, red).getValue().equals("40%");
	}

	public void testOK3() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type color = cache.addType("Color");
		final Relation carOutsideColor = car.setRelation("outside", color);
		final Generic myBmw = car.addInstance("myBmw");
		final Generic red = color.addInstance("red");
		carOutsideColor.enableSingularConstraint(Statics.TARGET_POSITION);
		myBmw.setLink(carOutsideColor, "20%", red);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				myBmw.setLink(carOutsideColor, "40%", red);

			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);

		// myBmw.getLink(carOutsideColor, red);
	}

}
