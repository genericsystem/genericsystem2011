package org.genericsystem.impl;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Type;
import org.genericsystem.api.generic.Value;
import org.testng.annotations.Test;

@Test
public class AttributeTest extends AbstractTest {

	public void testAncestorAndStructural() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.addAttribute(cache, "power");

		Type power = vehiclePower.getImplicit();
		assert power.isStructural();
		assert vehiclePower.isStructural();
		assert vehiclePower.getSupers().size() == 2 : vehiclePower.getSupers();
		assert vehiclePower.inheritsFrom(power);
	}

	public void testPropertyAncestorAndStructural() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.addProperty(cache, "power");

		Type power = vehiclePower.getImplicit();
		assert power.isStructural();
		assert vehiclePower.isStructural();
		assert vehiclePower.getSupers().size() == 2 : vehiclePower.getSupers();
		assert vehiclePower.inheritsFrom(power);
	}

	public void testDependency() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.addAttribute(cache, "power");

		Type power = vehiclePower.getImplicit();
		assert cache.getEngine().getInheritings(cache).contains(power);
		assert cache.getEngine().getInheritings(cache).contains(vehicle);
		assert power.getInheritings(cache).contains(vehiclePower);
		assert vehicle.getComposites(cache).contains(vehiclePower) : cache.getEngine().getComposites(cache);
	}

	public void testPropertyDependency() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.addProperty(cache, "power");

		Type power = vehiclePower.getImplicit();
		assert cache.getEngine().getInheritings(cache).contains(power);
		assert cache.getEngine().getInheritings(cache).contains(vehicle);
		assert power.getInheritings(cache).contains(vehiclePower);
		assert vehicle.getComposites(cache).contains(vehiclePower) : cache.getEngine().getComposites(cache);
	}

	public void testIsAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.addAttribute(cache, "power");
		assert vehiclePower.isAttribute();
		assert vehiclePower.isAttributeOf(vehicle);
	}

	public void testPropertyIsAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.addProperty(cache, "power");
		assert vehiclePower.isAttribute();
		assert vehiclePower.isAttributeOf(vehicle);
	}

	public void testDuplicateAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		assert vehicle.addAttribute(cache, "power") == vehicle.addAttribute(cache, "power");
	}

	public void testGetAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.addProperty(cache, "power");
		assert vehicle.getAttribute(cache, "power").equals(vehiclePower);
		assert vehicle.getAttribute(cache, "Pilot") == null;
	}

	public void testgetAttributes() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.addProperty(cache, "power");
		Attribute vehicleWheelsNumber = vehicle.addAttribute(cache, "WheelsNumber");
		assert vehicle.getAttributes(cache).contains(vehiclePower);
		assert vehicle.getAttributes(cache).contains(vehicleWheelsNumber);
		assert vehicle.getRelation(cache, "power") == null;
	}

	public void testGetAttributeWithValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.addAttribute(cache, "power");
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		Value myVehicle123 = myVehicle.setValue(cache, vehiclePower, "123");
		assert myVehicle123.isAttributeOf(myVehicle);
		assert myVehicle.getValueHolders(cache, vehiclePower).size() == 1 : myVehicle.getValueHolders(cache, vehiclePower);
		assert myVehicle.getValueHolders(cache, vehiclePower).contains(myVehicle123);
		Value myVehicle126 = myVehicle.setValue(cache, vehiclePower, "126");
		assert myVehicle.getValueHolders(cache, vehiclePower).size() == 2;
		assert myVehicle.getValueHolders(cache, vehiclePower).contains(myVehicle126);
		assert myVehicle.getValueHolders(cache, vehiclePower).contains(myVehicle123);
	}

	public void testPropertyGetAttributeWithValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.addProperty(cache, "power");
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		Value myVehicle123 = myVehicle.setValue(cache, vehiclePower, "123");
		assert myVehicle.getValueHolders(cache, vehiclePower).size() == 1;
		assert myVehicle.getValueHolders(cache, vehiclePower).contains(myVehicle123);
		Value myVehicle126 = myVehicle.setValue(cache, vehiclePower, "126");
		assert myVehicle.getValueHolders(cache, vehiclePower).size() == 1;
		assert myVehicle.getValueHolders(cache, vehiclePower).contains(myVehicle126);
	}

	public void testGetAttributeWithInheritance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.addAttribute(cache, "power");
		Type car = vehicle.newSubType(cache, "Car");
		assert (vehiclePower.equals(vehicle.getAttribute(cache, "power")));
		assert (vehiclePower.equals(car.getAttribute(cache, "power")));
	}

	public void testGetAttributesWithInheritance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.addAttribute(cache, "power");
		Attribute vehicleWheelsNumber = vehicle.addAttribute(cache, "WheelsNumber");
		Type car = vehicle.newSubType(cache, "Car");
		assert vehicle.getAttributes(cache).contains(vehiclePower);
		assert vehicle.getAttributes(cache).contains(vehicleWheelsNumber);
		assert car.getAttributes(cache).contains(vehiclePower);
		assert car.getAttributes(cache).contains(vehicleWheelsNumber);
	}

	public void testIsAttributeOf() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.addAttribute(cache, "power");
		Type car = vehicle.newSubType(cache, "Car");
		assert vehiclePower.isAttributeOf(vehicle);
		assert vehiclePower.isAttributeOf(car);
		assert vehiclePower.isAttribute();

		Type color = cache.newType("Color");
		Relation vehicleColor = vehicle.addRelation(cache, "vehicleColor", color);
		assert vehicleColor.isAttribute();
		assert vehiclePower.isAttributeOf(vehicle);
		assert vehiclePower.isAttributeOf(car);
		assert !vehicleColor.isReallyAttribute();
	}

	// public void testOverrideAttribute() {
	// Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
	// Type vehicle = cache.newType("Vehicle");
	// Type car = vehicle.newSubType(cache, "Car");
	// Attribute vehiclePower = vehicle.addAttribute(cache, "power");
	// Attribute carUltraPower = car.addSubAttribute(cache, vehiclePower, "ultraPower");
	//
	// assert carUltraPower.isAttribute();
	// assert vehicle.getAttributes(cache).contains(vehiclePower) : vehicle.getAttributes(cache);
	// assert car.getAttributes(cache).contains(carUltraPower) : car.getAttributes(cache);
	// assert carUltraPower.inheritsFrom(vehiclePower);
	// }

	public void testJumpOverrideAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Attribute vehiclePower = vehicle.addAttribute(cache, "power");
		assert vehicle.getAttributes(cache).contains(vehiclePower);
		assert car.newSubType(cache, "SuperCar").getAttributes(cache).contains(vehiclePower);
	}

	public void testOverrideValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Attribute vehiclePower = vehicle.addAttribute(cache, "power");
		Value p213 = vehicle.setValue(cache, vehiclePower, "213");
		Value p214 = vehicle.setValue(cache, vehiclePower, "214");

		assert vehiclePower.getInstances(cache).size() == 2 : vehiclePower.getInstances(cache);
		assert vehiclePower.getInstances(cache).contains(p213);
		assert vehiclePower.getInstances(cache).contains(p214);

		Value p333 = car.setValue(cache, (Attribute) p213, "333");

		assert !p333.inheritsFrom(p213);

		assert vehiclePower.getInstances(cache).size() == 3 : vehiclePower.getInstances(cache);
		assert vehiclePower.getInstances(cache).contains(p213);
		assert vehiclePower.getInstances(cache).contains(p214);
		assert vehiclePower.getInstances(cache).contains(p333);

		assert vehicle.getValueHolders(cache, vehiclePower).size() == 2;
		assert vehicle.getValueHolders(cache, vehiclePower).contains(p213);
		assert vehicle.getValueHolders(cache, vehiclePower).contains(p214);

		assert car.getValueHolders(cache, vehiclePower).size() == 2 : car.getValueHolders(cache, vehiclePower);
		assert car.getValueHolders(cache, vehiclePower).contains(p214);
		assert car.getValueHolders(cache, vehiclePower).contains(p333);
	}

	public void testOverrideValueWithInstances() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Attribute vehiclePower = vehicle.addAttribute(cache, "power");

		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		Generic myCar = car.newInstance(cache, "myCar");
		Generic myCar2 = car.newInstance(cache, "myCar2");

		Value p213 = vehicle.setValue(cache, vehiclePower, "213");
		Value p214 = vehicle.setValue(cache, vehiclePower, "214");
		Value p333 = myCar.setValue(cache, (Attribute) p213, "333");

		assert myVehicle.getValueHolders(cache, vehiclePower).size() == 2;
		assert myVehicle.getValueHolders(cache, vehiclePower).contains(p213);
		assert myVehicle.getValueHolders(cache, vehiclePower).contains(p214);

		assert myCar.getValueHolders(cache, vehiclePower).size() == 2;
		assert myCar.getValueHolders(cache, vehiclePower).contains(p333);
		assert myCar.getValueHolders(cache, vehiclePower).contains(p214);

		assert myCar2.getValueHolders(cache, vehiclePower).size() == 2;
		assert myCar2.getValueHolders(cache, vehiclePower).contains(p213);
		assert myCar2.getValueHolders(cache, vehiclePower).contains(p214);
	}

	// public void testOverrideThreeAttribute() {
	// Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
	// Type vehicle = cache.newType("Vehicle");
	// Type car = vehicle.newSubType(cache, "Car");
	// Type superCar = car.newSubType(cache, "SuperCar");
	// Attribute vehiclePower = vehicle.addAttribute(cache, "power");
	// Attribute carUltraPower = car.addSubAttribute(cache, vehiclePower, "UltraPower");
	//
	// Attribute superCarFullUltraPower = superCar.addSubAttribute(cache, carUltraPower, "FullUltraPower");
	// assert superCar.getAttributes(cache).contains(superCarFullUltraPower);
	// assert car.getAttributes(cache).contains(carUltraPower);
	// assert vehicle.getAttributes(cache).contains(vehiclePower);
	// }

	public void testAttributeOnAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Type car = vehicle.newSubType(cache, "Car");
		Relation carHuman = car.addRelation(cache, "carHuman", human);
		Attribute carPower = car.addAttribute(cache, "power");
		Attribute carPowerUnit = carPower.addAttribute(cache, "Unit");
		assert car.getStructurals(cache).contains(carHuman);
		assert car.getStructurals(cache).contains(carPower);
		assert car.getAttributes(cache).contains(carPower);
		assert carPower.getStructurals(cache).contains(carPowerUnit);
		assert carPower.getAttributes(cache).contains(carPowerUnit);
	}

	public void testMetaAttributeOnAttributeOnInstance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.addAttribute(cache, "power");
		Attribute vehiclePowerUnit = vehiclePower.addAttribute(cache, "Unit");

		assert vehicle.getMeta().equals(cache.getEngine());

		Value v235 = vehicle.setValue(cache, vehiclePower, "235");
		Value vHP = v235.setValue(cache, vehiclePowerUnit, "HP");

		assert v235.getMeta().equals(vehiclePower);
		assert vHP.getMeta().equals(vehiclePowerUnit) : vHP.getSupers();
	}

	public void testAttributeOnAttributeWithGetAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Type car = vehicle.newSubType(cache, "Car");
		human.addRelation(cache, "Drive", car);

		Attribute carPower = car.addAttribute(cache, "power");
		Attribute carPowerUnit = carPower.addAttribute(cache, "Unit");
		assert carPower != carPowerUnit;
		assert carPower.isAttributeOf(car);
		assert carPowerUnit.isAttributeOf(carPower);
		assert !carPowerUnit.isAttributeOf(vehicle);

		assert car.getAttributes(cache).contains(carPower) : car.getAttributes(cache);
		assert carPower.getAttributes(cache).contains(carPowerUnit) : carPower.getAttributes(cache);
		assert !carPower.isAttributeOf(carPowerUnit);
		assert carPowerUnit.isAttributeOf(carPower);
		assert !carPowerUnit.inheritsFrom(carPower) : carPowerUnit.info();
		assert !carPowerUnit.isAttributeOf(carPowerUnit);
	}

	public void testSimpleAttributeOnAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.addAttribute(cache, "power");

		Value v235 = vehicle.setValue(cache, vehiclePower, "235");
		Attribute vehiclePowerUnit = vehiclePower.addAttribute(cache, "Unit");
		assert vehicle.getValueHolders(cache, vehiclePower).size() == 1 : v235.info();
		assert vehicle.getValueHolders(cache, vehiclePower).contains(v235);

		Value vHP = v235.setValue(cache, vehiclePowerUnit, "HP");
		assert vHP.isAttributeOf(v235) : vHP.getBaseComponent();
		assert v235.getValueHolders(cache, vehiclePowerUnit).size() == 1 : vHP.info();
		assert v235.getValueHolders(cache, vehiclePowerUnit).contains(vHP);

		assert vehicle.getValueHolders(cache, vehiclePower).size() == 1;
		assert vehicle.getValueHolders(cache, vehiclePower).contains(v235);
	}

	public void testDefaultAttributeValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.addAttribute(cache, "power");
		Value value = vehicle.setValue(cache, vehiclePower, true);
		assert vehicle.getValueHolders(cache, vehiclePower).contains(value);
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		assert myVehicle.getValueHolders(cache, vehiclePower).contains(value);
	}

	public void testDefaultPropertyValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.addProperty(cache, "power");
		assert vehiclePower.equals(vehicle.getProperty(cache, "power")) : vehicle.getAttributes(cache);
		assert vehiclePower.isAlive(cache);
		assert vehicle.getLink(cache, (Relation) vehiclePower) == null;
		Value value = ((Attribute) vehicle).setValue(cache, vehiclePower, true);
		assert vehiclePower.isAlive(cache);

		assert value.getBaseComponent().equals(vehicle);
		assert !value.inheritsFrom(vehicle);
		assert value.isAttributeOf(vehicle);
		assert vehicle.getValueHolders(cache, vehiclePower).contains(value);
		assert Boolean.TRUE.equals(((Attribute) vehicle).getValue(cache, vehiclePower)) : ((Attribute) vehicle).getValue(cache, vehiclePower);
		assert vehiclePower.isAlive(cache);
		assert vehiclePower.equals(vehicle.getProperty(cache, "power")) : vehicle.getAttributes(cache);
	}

	public void testDefaultPropertyValueWithInheritance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Generic myBmw = car.newInstance(cache, "myBmw");
		Attribute vehiclePower = vehicle.addProperty(cache, "power");
		assert vehiclePower.equals(car.getProperty(cache, "power")) : car.getProperty(cache, "power");
		Value falseVehicle = ((Attribute) vehicle).setValue(cache, vehiclePower, false);
		assert falseVehicle.isAttributeOf(vehicle);
		assert Boolean.FALSE.equals(((Attribute) myBmw).getValue(cache, vehiclePower));
		Value trueCar = ((Attribute) car).setValue(cache, vehiclePower, true);
		assert trueCar.isAttributeOf(car);
		assert !trueCar.inheritsFrom(falseVehicle);
		assert Boolean.FALSE.equals(((Attribute) vehicle).getValue(cache, vehiclePower)) : ((Attribute) vehicle).getValue(cache, vehiclePower);
		assert Boolean.TRUE.equals(((Attribute) car).getValue(cache, vehiclePower));
		assert Boolean.TRUE.equals(((Attribute) myBmw).getValue(cache, vehiclePower));
	}

}
