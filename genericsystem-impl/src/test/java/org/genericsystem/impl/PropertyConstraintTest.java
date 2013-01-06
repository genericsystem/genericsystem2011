package org.genericsystem.impl;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.exception.PropertyConstraintViolationException;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Type;
import org.genericsystem.api.generic.Value;
import org.genericsystem.impl.core.Statics;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class PropertyConstraintTest extends AbstractTest {

	private Cache cache;

	@BeforeMethod
	public void initCache() {
		cache = GenericSystem.newCacheOnANewInMemoryEngine();
	}

	public void testSingleValueAttribute() {
		Type vehicle = cache.newType("Vehicle");
		Attribute equipment = vehicle.addAttribute(cache,"Equipment");
		equipment.enablePropertyConstraint(cache);
		Generic myVehicle = vehicle.newInstance(cache,"myVehicle");
		myVehicle.addValue(cache,equipment, "ABS");
	}

	public void testMultipleValuesAttribute() {
		Type vehicle = cache.newType("Vehicle");
		final Attribute equipment = vehicle.addAttribute(cache,"Equipment");
		equipment.enablePropertyConstraint(cache);
		final Generic myVehicle = vehicle.newInstance(cache,"myVehicle");
		myVehicle.addValue(cache,equipment, "ABS");
		new RollbackCatcher() {

			@Override
			public void intercept() {
				myVehicle.addValue(cache,equipment, "GPS");
			}
		}.assertIsCausedBy(PropertyConstraintViolationException.class);
	}

	public void testMultipleValuesAttributeWithoutConstraint() {
		Type vehicle = cache.newType("Vehicle");
		Attribute equipment = vehicle.addAttribute(cache,"Equipment");
		Generic myVehicle = vehicle.newInstance(cache,"myVehicle");
		myVehicle.addValue(cache,equipment, "ABS");
		myVehicle.addValue(cache,equipment, "GPS");
	}

	public void testMultipleValuesAttributeWithDisabledConstraint() {
		Type vehicle = cache.newType("Vehicle");
		Attribute equipment = vehicle.addAttribute(cache,"Equipment");
		equipment.disablePropertyConstraint(cache);
		Generic myVehicle = vehicle.newInstance(cache,"myVehicle");
		myVehicle.addValue(cache,equipment, "ABS");
		myVehicle.addValue(cache,equipment, "GPS");
	}

	public void testBinaryRelationDifferentTarget() {
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Relation vehicleColor = vehicle.addRelation(cache,"VehicleColor", color);
		vehicleColor.enablePropertyConstraint(cache);
		Generic myVehicle = vehicle.newInstance(cache,"MyVehicle");
		Generic red = color.newInstance(cache,"red");
		Generic blue = color.newInstance(cache,"blue");
		myVehicle.addLink(cache,vehicleColor, "VehicleColor", red);
		myVehicle.addLink(cache,vehicleColor, "VehicleColor", blue);
	}

	public void testBinaryRelationSameTarget() {
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		final Relation vehicleColor = vehicle.addRelation(cache,"VehicleColor", color);
		vehicleColor.enablePropertyConstraint(cache);
		final Generic myVehicle = vehicle.newInstance(cache,"myVehicle");
		final Generic red = color.newInstance(cache,"red");

		myVehicle.addLink(cache,vehicleColor, "myVehicleRed", red);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				myVehicle.addLink(cache,vehicleColor, "myVehicleRedAgain", red);
			}
		}.assertIsCausedBy(PropertyConstraintViolationException.class);
	}

	public void testTernaryRelationDifferentTargets() {
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Type pilot = cache.newType("Pilot");
		Relation vehicleColor = vehicle.addRelation(cache,"VehicleColor", color, pilot);
		vehicleColor.enablePropertyConstraint(cache);
		Generic myVehicle = vehicle.newInstance(cache,"myVehicle");
		Generic red = color.newInstance(cache,"red");
		Generic myPilot = pilot.newInstance(cache,"myPilot");
		Generic anotherPilot = pilot.newInstance(cache,"anotherPilot");
		myVehicle.addLink(cache,vehicleColor, "myVehicleRed", red, myPilot);
		myVehicle.addLink(cache,vehicleColor, "myVehicleRed", red, anotherPilot);
	}

	public void testTernaryRelationSameTargets() {
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Type pilot = cache.newType("Pilot");
		final Relation vehicleColor = vehicle.addRelation(cache,"VehicleColor", color, pilot);
		vehicleColor.enablePropertyConstraint(cache);
		final Generic myVehicle = vehicle.newInstance(cache,"myVehicle");
		final Generic red = color.newInstance(cache,"red");
		final Generic myPilot = pilot.newInstance(cache,"myPilot");

		myVehicle.addLink(cache,vehicleColor, "myVehicleRed", red, myPilot);

		new RollbackCatcher() {

			@Override
			public void intercept() {
				myVehicle.addLink(cache,vehicleColor, "myVehicleRedAgain", red, myPilot);
			}
		}.assertIsCausedBy(PropertyConstraintViolationException.class);
	}

	public void testSingleValueAttributeForSubtype() {
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache,"Car");
		Attribute equipment = vehicle.addAttribute(cache,"Equipment");
		equipment.enablePropertyConstraint(cache);
		Generic myCar = car.newInstance(cache,"myCar");
		myCar.addValue(cache,equipment, "ABS");
	}

	public void testMultipleValuesAttributeForSubtype() {
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache,"Car");
		final Attribute equipment = vehicle.addAttribute(cache,"Equipment");
		equipment.enablePropertyConstraint(cache);
		final Generic myCar = car.newInstance(cache,"myCar");

		myCar.addValue(cache,equipment, "ABS");
		new RollbackCatcher() {

			@Override
			public void intercept() {
				myCar.addValue(cache,equipment, "GPS");
			}
		}.assertIsCausedBy(PropertyConstraintViolationException.class);
	}

	public void testMultipleValuesAttributeForSubtypeOtherWay() {
		Type vehicle = cache.newType("Vehicle");
		Type car = cache.newSubType("Car", vehicle);
		final Attribute equipment = vehicle.addAttribute(cache,"Equipment");
		equipment.enablePropertyConstraint(cache);
		final Generic myCar = car.newInstance(cache,"myCar");

		myCar.addValue(cache,equipment, "ABS");
		new RollbackCatcher() {

			@Override
			public void intercept() {
				myCar.addValue(cache,equipment, "GPS");
			}
		}.assertIsCausedBy(PropertyConstraintViolationException.class);
	}

	public void testSameTarget() {
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache,"Car");
		Type color = cache.newType("Color");
		final Relation vehicleColor = vehicle.addRelation(cache,"VehicleColor", color);
		vehicleColor.enablePropertyConstraint(cache);
		final Generic myCar = car.newInstance(cache,"myCar");
		final Generic red = color.newInstance(cache,"red");

		myCar.addLink(cache,vehicleColor, "myVehiclePower", red);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				myCar.addLink(cache,vehicleColor, "myVehiclePower2", red);
			}
		}.assertIsCausedBy(PropertyConstraintViolationException.class);
	}

	public void testBinaryRelationBetweenSubtypeAndSameTarget() {
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache,"Car");
		final Type bike = car.newSubType(cache,"Bike");
		Type color = cache.newType("Color");
		final Relation vehicleColor = vehicle.addRelation(cache,"VehicleColor", color);
		vehicleColor.enablePropertyConstraint(cache);
		final Generic myBike = bike.newInstance(cache,"myBike");
		final Generic red = color.newInstance(cache,"red");

		myBike.addLink(cache,vehicleColor, "myVehicleRed", red);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				myBike.addLink(cache,vehicleColor, "myVehicleRedAgain", red);
			}
		}.assertIsCausedBy(PropertyConstraintViolationException.class);
	}

	public void testUniqueInstance() {
		Type vehicle = cache.newType("Vehicle");
		vehicle.enablePropertyConstraint(cache);
		vehicle.newInstance(cache,"myVehicle");
	}

	public void testMutlipleInstances() {
		Type vehicle = cache.newType("Vehicle");
		Generic myVehicle = vehicle.newAnonymousInstance(cache);
		Generic myVehicle2 = vehicle.newAnonymousInstance(cache);
		assert myVehicle != myVehicle2 : myVehicle.info() + myVehicle2.info();
	}

	public void testMutlipleInstancesWithSubclass() {
		Type vehicle = cache.newType("Vehicle");
		final Type car = vehicle.newSubType(cache,"Car");
		vehicle.enablePropertyConstraint(cache);
		vehicle.newInstance(cache,"myVehicle");

		new RollbackCatcher() {

			@Override
			public void intercept() {
				car.newInstance(cache,"myVehicle");
			}
		}.assertIsCausedBy(PropertyConstraintViolationException.class);
	}

	public void testMultipleDefaultValuesAttribute1() {
		final Type vehicle = cache.newType("Vehicle");
		final Attribute equipment = vehicle.addAttribute(cache,"Equipment");
		equipment.enablePropertyConstraint(cache);
		final Generic myVehicle = vehicle.newInstance(cache,"myVehicle");

		vehicle.addValue(cache,equipment, "ABS");

		new RollbackCatcher() {

			@Override
			public void intercept() {
				myVehicle.addValue(cache,equipment, "GPS");
			}
		}.assertIsCausedBy(PropertyConstraintViolationException.class);
	}

	public void testMultipleDefaultValuesAttribute2() {
		final Type vehicle = cache.newType("Vehicle");
		final Attribute equipment = vehicle.addAttribute(cache,"Equipment");
		equipment.enablePropertyConstraint(cache);
		final Generic myVehicle = vehicle.newInstance(cache,"myVehicle");

		final Value v = myVehicle.addValue(cache,equipment, "ABS");

		assert vehicle.getAllInstances(cache).contains(myVehicle);

		new RollbackCatcher() {

			@Override
			public void intercept() {
				vehicle.addValue(cache,equipment, "GPS");
			}
		}.assertIsCausedBy(PropertyConstraintViolationException.class);
	}
}
