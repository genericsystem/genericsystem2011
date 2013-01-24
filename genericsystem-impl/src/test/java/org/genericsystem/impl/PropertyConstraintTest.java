package org.genericsystem.impl;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.exception.PropertyConstraintViolationException;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Link;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Type;
import org.genericsystem.api.generic.Value;
import org.genericsystem.impl.core.GenericImpl;
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
		Attribute equipment = vehicle.addAttribute(cache, "Equipment");
		equipment.enablePropertyConstraint(cache);
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		myVehicle.setValue(cache, equipment, "ABS");
	}
	
	public void testMultipleValuesAttribute() {
		Type vehicle = cache.newType("Vehicle");
		final Attribute equipment = vehicle.addAttribute(cache, "Equipment");
		equipment.enablePropertyConstraint(cache);
		final Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		Value abs = myVehicle.setValue(cache, equipment, "ABS");
		myVehicle.setValue(cache, equipment, "GPS");
		assert !abs.isAlive(cache);
	}
	
	public void testMultipleValuesAttributeWithoutConstraint() {
		Type vehicle = cache.newType("Vehicle");
		Attribute equipment = vehicle.addAttribute(cache, "Equipment");
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		myVehicle.setValue(cache, equipment, "ABS");
		myVehicle.setValue(cache, equipment, "GPS");
	}
	
	public void testMultipleValuesAttributeWithDisabledConstraint() {
		Type vehicle = cache.newType("Vehicle");
		Attribute equipment = vehicle.addAttribute(cache, "Equipment");
		equipment.disablePropertyConstraint(cache);
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		myVehicle.setValue(cache, equipment, "ABS");
		myVehicle.setValue(cache, equipment, "GPS");
	}
	
	public void testBinaryRelationDifferentTarget() {
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Relation vehicleColor = vehicle.addRelation(cache, "VehicleColor", color);
		vehicleColor.enablePropertyConstraint(cache);
		Generic myVehicle = vehicle.newInstance(cache, "MyVehicle");
		Generic red = color.newInstance(cache, "red");
		Generic blue = color.newInstance(cache, "blue");
		myVehicle.setLink(cache, vehicleColor, "VehicleColor", red);
		myVehicle.setLink(cache, vehicleColor, "VehicleColor", blue);
	}
	
	public void testBinaryRelationSameTarget() {
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		final Relation vehicleColor = vehicle.addRelation(cache, "VehicleColor", color);
		vehicleColor.enablePropertyConstraint(cache);
		final Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		final Generic red = color.newInstance(cache, "red");
		Link myVehicleRed = myVehicle.setLink(cache, vehicleColor, "myVehicleRed", red);
		Link myVehicleRedAgain = myVehicle.setLink(cache, vehicleColor, "myVehicleRedAgain", red);
		assert !myVehicleRed.isAlive(cache);
		assert myVehicleRedAgain.isAlive(cache);
	}
	
	public void testTernaryRelationDifferentTargets() {
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Type pilot = cache.newType("Pilot");
		Relation vehicleColor = vehicle.addRelation(cache, "VehicleColor", color, pilot);
		vehicleColor.enablePropertyConstraint(cache);
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		Generic red = color.newInstance(cache, "red");
		Generic myPilot = pilot.newInstance(cache, "myPilot");
		Generic anotherPilot = pilot.newInstance(cache, "anotherPilot");
		myVehicle.setLink(cache, vehicleColor, "myVehicleRed", red, myPilot);
		myVehicle.setLink(cache, vehicleColor, "myVehicleRed", red, anotherPilot);
	}
	
	public void testTernaryRelationSameTargets() {
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Type pilot = cache.newType("Pilot");
		final Relation vehicleColor = vehicle.addRelation(cache, "VehicleColor", color, pilot);
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
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Attribute equipment = vehicle.addAttribute(cache, "Equipment");
		equipment.enablePropertyConstraint(cache);
		Generic myCar = car.newInstance(cache, "myCar");
		myCar.setValue(cache, equipment, "ABS");
	}
	
	public void testMultipleValuesAttributeForSubtype() {
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		final Attribute equipment = vehicle.addAttribute(cache, "Equipment");
		equipment.enablePropertyConstraint(cache);
		final Generic myCar = car.newInstance(cache, "myCar");
		Value absValue = myCar.setValue(cache, equipment, "ABS");
		Value gpsValue = myCar.setValue(cache, equipment, "GPS");
		assert !absValue.isAlive(cache);
		assert gpsValue.isAlive(cache);
	}
	
	public void testMultipleValuesAttributeForSubtypeOtherWay() {
		Type vehicle = cache.newType("Vehicle");
		Type car = cache.newSubType("Car", vehicle);
		final Attribute equipment = vehicle.addAttribute(cache, "Equipment");
		equipment.enablePropertyConstraint(cache);
		final Generic myCar = car.newInstance(cache, "myCar");
		Value absValue = myCar.setValue(cache, equipment, "ABS");
		Value gpsValue = myCar.setValue(cache, equipment, "GPS");
		assert !absValue.isAlive(cache);
		assert gpsValue.isAlive(cache);
	}
	
	public void testSameTarget() {
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Type color = cache.newType("Color");
		final Relation vehicleColor = vehicle.addRelation(cache, "VehicleColor", color);
		vehicleColor.enablePropertyConstraint(cache);
		final Generic myCar = car.newInstance(cache, "myCar");
		final Generic red = color.newInstance(cache, "red");
		Link myVehiclePower = myCar.setLink(cache, vehicleColor, "myVehiclePower", red);
		Link myVehiclePower2 = myCar.setLink(cache, vehicleColor, "myVehiclePower2", red);
		assert !myVehiclePower.isAlive(cache);
		assert myVehiclePower2.isAlive(cache);
	}
	
	public void testBinaryRelationBetweenSubtypeAndSameTarget() {
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		final Type bike = car.newSubType(cache, "Bike");
		Type color = cache.newType("Color");
		final Relation vehicleColor = vehicle.addRelation(cache, "VehicleColor", color);
		vehicleColor.enablePropertyConstraint(cache);
		final Generic myBike = bike.newInstance(cache, "myBike");
		final Generic red = color.newInstance(cache, "red");
		Link myVehicleRed = myBike.setLink(cache, vehicleColor, "myVehicleRed", red);
		Link myVehicleRedAgain = myBike.setLink(cache, vehicleColor, "myVehicleRedAgain", red);
		assert !myVehicleRed.isAlive(cache);
		assert myVehicleRedAgain.isAlive(cache);
	}
	
	public void testUniqueInstance() {
		Type vehicle = cache.newType("Vehicle");
		vehicle.enablePropertyConstraint(cache);
		vehicle.newInstance(cache, "myVehicle");
	}
	
	public void testMutlipleInstances() {
		Type vehicle = cache.newType("Vehicle");
		Generic myVehicle = vehicle.newAnonymousInstance(cache);
		Generic myVehicle2 = vehicle.newAnonymousInstance(cache);
		assert myVehicle != myVehicle2 : myVehicle.info() + myVehicle2.info();
	}
	
	public void testMutlipleInstancesWithSubclass() {
		Type vehicle = cache.newType("Vehicle");
		final Type car = vehicle.newSubType(cache, "Car");
		vehicle.enablePropertyConstraint(cache);
		vehicle.newInstance(cache, "myVehicle");
		
		new RollbackCatcher() {
			
			@Override
			public void intercept() {
				car.newInstance(cache, "myVehicle");
			}
		}.assertIsCausedBy(PropertyConstraintViolationException.class);
	}
	
	public void testMultipleDefaultValuesAttribute1() {
		final Type vehicle = cache.newType("Vehicle");
		final Attribute equipment = vehicle.addAttribute(cache, "Equipment");
		equipment.enablePropertyConstraint(cache);
		final Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		Value absValue = vehicle.setValue(cache, equipment, "ABS");
		Value gpsValue = myVehicle.setValue(cache, equipment, "GPS");
		// Todo check remove old value
		assert absValue.isAlive(cache);
		assert myVehicle.getValue(cache, equipment).equals(gpsValue.getValue());
	}
	
	public void testMultipleDefaultValuesAttribute2() {
		final Type vehicle = cache.newType("Vehicle");
		final Attribute equipment = vehicle.addAttribute(cache, "Equipment");
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
}
