package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class AttributeTest extends AbstractTest {

	public void testAncestorAndStructural() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute("power");
		assert vehiclePower.isStructural();
		assert vehiclePower.getSupers().size() == 2 : vehiclePower.getSupers();
		assert vehiclePower.inheritsFrom(cache.getEngine());
	}

	public void testPropertyAncestorAndStructural() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.setProperty("power");

		assert vehiclePower.isStructural();
		assert vehiclePower.getSupers().size() == 2 : vehiclePower.getSupers();
		assert vehiclePower.inheritsFrom(cache.getEngine());
	}

	public void testDependency() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute("power");

		assert cache.getEngine().getInheritings().contains(vehiclePower);
		assert cache.getEngine().getInheritings().contains(vehicle);
		assert vehicle.getComposites().contains(vehiclePower) : cache.getEngine().getComposites();
	}

	public void testPropertyDependency() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.setProperty("power");

		assert cache.getEngine().getInheritings().contains(vehiclePower);
		assert cache.getEngine().getInheritings().contains(vehicle);
		assert vehicle.getComposites().contains(vehiclePower) : cache.getEngine().getComposites();
	}

	public void testIsAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute("power");
		assert vehiclePower.isAttribute();
		assert vehiclePower.isAttributeOf(vehicle);
	}

	public void testPropertyIsAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.setProperty("power");
		assert vehiclePower.isAttribute();
		assert vehiclePower.isAttributeOf(vehicle);
	}

	public void testDuplicateAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		assert vehicle.setAttribute("power") == vehicle.setAttribute("power");
	}

	public void testGetAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.setProperty("power");
		assert vehicle.getAttribute("power").equals(vehiclePower);
		assert vehicle.getAttribute("Pilot") == null;
	}

	public void testgetAttributes() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.setProperty("power");
		Attribute vehicleWheelsNumber = vehicle.setAttribute("WheelsNumber");
		assert vehicle.getAttributes().contains(vehiclePower);
		assert vehicle.getAttributes().contains(vehicleWheelsNumber);
		assert vehicle.getRelation("power") == null;
	}

	public void testGetAttributeWithValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute("power");
		Generic myVehicle = vehicle.newInstance("myVehicle");
		Holder myVehicle123 = myVehicle.setValue(vehiclePower, "123");
		assert myVehicle123.isAttributeOf(myVehicle);
		assert myVehicle.getHolders(vehiclePower).size() == 1 : myVehicle.getHolders(vehiclePower);
		assert myVehicle.getHolders(vehiclePower).contains(myVehicle123);
		Holder myVehicle126 = myVehicle.setValue(vehiclePower, "126");
		assert myVehicle.getHolders(vehiclePower).size() == 2;
		assert myVehicle.getHolders(vehiclePower).contains(myVehicle126);
		assert myVehicle.getHolders(vehiclePower).contains(myVehicle123);
	}

	public void testPropertyGetAttributeWithValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.setProperty("power");
		Generic myVehicle = vehicle.newInstance("myVehicle");
		Holder myVehicle123 = myVehicle.setValue(vehiclePower, "123");
		assert myVehicle.getHolders(vehiclePower).size() == 1;
		assert myVehicle.getHolders(vehiclePower).contains(myVehicle123);
		Holder myVehicle126 = myVehicle.setValue(vehiclePower, "126");
		assert myVehicle.getHolders(vehiclePower).size() == 1;
		assert myVehicle.getHolders(vehiclePower).contains(myVehicle126);
	}

	public void testGetAttributeWithInheritance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute("power");
		Type car = vehicle.newSubType("Car");
		assert (vehiclePower.equals(vehicle.getAttribute("power")));
		assert (vehiclePower.equals(car.getAttribute("power")));
	}

	public void testGetAttributesWithInheritance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute("power");
		Attribute vehicleWheelsNumber = vehicle.setAttribute("WheelsNumber");
		Type car = vehicle.newSubType("Car");
		assert vehicle.getAttributes().contains(vehiclePower);
		assert vehicle.getAttributes().contains(vehicleWheelsNumber);
		assert car.getAttributes().contains(vehiclePower);
		assert car.getAttributes().contains(vehicleWheelsNumber);
	}

	public void testIsAttributeOf() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute("power");
		Type car = vehicle.newSubType("Car");
		assert vehiclePower.isAttributeOf(vehicle);
		assert vehiclePower.isAttributeOf(car);
		assert vehiclePower.isAttribute();

		Type color = cache.newType("Color");
		Relation vehicleColor = vehicle.setRelation("vehicleColor", color);
		assert vehicleColor.isAttribute();
		assert vehiclePower.isAttributeOf(vehicle);
		assert vehiclePower.isAttributeOf(car);
		assert !vehicleColor.isReallyAttribute();
	}

	// public void testOverrideAttribute() {
	// Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
	// Type vehicle = cache.newType("Vehicle");
	// Type car = vehicle.newSubType( "Car");
	// Attribute vehiclePower = vehicle.addAttribute( "power");
	// Attribute carUltraPower = car.addSubAttribute( vehiclePower, "ultraPower");
	//
	// assert carUltraPower.isAttribute();
	// assert vehicle.getAttributes().contains(vehiclePower) : vehicle.getAttributes();
	// assert car.getAttributes().contains(carUltraPower) : car.getAttributes();
	// assert carUltraPower.inheritsFrom(vehiclePower);
	// }

	public void testJumpOverrideAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType("Car");
		Attribute vehiclePower = vehicle.setAttribute("power");
		assert vehicle.getAttributes().contains(vehiclePower);
		assert car.newSubType("SuperCar").getAttributes().contains(vehiclePower);
	}

	public void testOverrideValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType("Car");
		Attribute vehiclePower = vehicle.setAttribute("power");
		Holder p213 = vehicle.setValue(vehiclePower, "213");
		Holder p214 = vehicle.setValue(vehiclePower, "214");

		assert vehiclePower.getInstances().size() == 2 : vehiclePower.getInstances();
		assert vehiclePower.getInstances().contains(p213);
		assert vehiclePower.getInstances().contains(p214);

		Holder p333 = car.setValue((Attribute) p213, "333");

		assert p333.inheritsFrom(p213) : p333.info();
		assert !p333.inheritsFrom(p214) : p333.info();

		assert vehiclePower.getAllInstances().size() == 3 : vehiclePower.getAllInstances();
		assert vehiclePower.getAllInstances().contains(p213);
		assert vehiclePower.getAllInstances().contains(p214);
		assert vehiclePower.getAllInstances().contains(p333);

		assert vehicle.getHolders(vehiclePower).size() == 2;
		assert vehicle.getHolders(vehiclePower).contains(p213);
		assert vehicle.getHolders(vehiclePower).contains(p214);

		assert car.getHolders(vehiclePower).size() == 2 : car.getHolders(vehiclePower);
		assert car.getHolders(vehiclePower).contains(p214);
		assert !car.getHolders(vehiclePower).contains(p213);
		assert car.getHolders(vehiclePower).contains(p333);
	}

	public void testOverrideValueWithInstances() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType("Car");
		Attribute vehiclePower = vehicle.setAttribute("power");

		Generic myVehicle = vehicle.newInstance("myVehicle");
		Generic myCar = car.newInstance("myCar");
		Generic myCar2 = car.newInstance("myCar2");

		Holder p213 = vehicle.setValue(vehiclePower, "213");
		Holder p214 = vehicle.setValue(vehiclePower, "214");
		Holder p333 = myCar.setValue((Attribute) p213, "333");

		assert myVehicle.getHolders(vehiclePower).size() == 2;
		assert myVehicle.getHolders(vehiclePower).contains(p213);
		assert myVehicle.getHolders(vehiclePower).contains(p214);

		assert myCar.getHolders(vehiclePower).size() == 2;
		assert myCar.getHolders(vehiclePower).contains(p214);
		assert myCar.getHolders(vehiclePower).contains(p333);

		assert myCar2.getHolders(vehiclePower).size() == 2;
		assert myCar2.getHolders(vehiclePower).contains(p213);
		assert myCar2.getHolders(vehiclePower).contains(p214);
	}

	// public void testOverrideThreeAttribute() {
	// Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
	// Type vehicle = cache.newType("Vehicle");
	// Type car = vehicle.newSubType( "Car");
	// Type superCar = car.newSubType( "SuperCar");
	// Attribute vehiclePower = vehicle.addAttribute( "power");
	// Attribute carUltraPower = car.addSubAttribute( vehiclePower, "UltraPower");
	//
	// Attribute superCarFullUltraPower = superCar.addSubAttribute( carUltraPower, "FullUltraPower");
	// assert superCar.getAttributes().contains(superCarFullUltraPower);
	// assert car.getAttributes().contains(carUltraPower);
	// assert vehicle.getAttributes().contains(vehiclePower);
	// }

	public void testAttributeOnAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Type car = vehicle.newSubType("Car");
		Relation carHuman = car.setRelation("carHuman", human);
		Attribute carPower = car.setAttribute("power");
		Attribute carPowerUnit = carPower.setAttribute("Unit");
		assert car.getAttributes().contains(carHuman);
		assert car.getAttributes().contains(carPower);
		assert car.getAttributes().contains(carPower);
		assert carPower.getAttributes().contains(carPowerUnit);
		assert carPower.getAttributes().contains(carPowerUnit);
	}

	public void testMetaAttributeOnAttributeOnInstance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute("power");
		Attribute vehiclePowerUnit = vehiclePower.setAttribute("Unit");

		assert vehicle.getMeta().equals(cache.getEngine());

		Holder v235 = vehicle.setValue(vehiclePower, "235");
		Holder vHP = v235.setValue(vehiclePowerUnit, "HP");

		assert v235.getMeta().equals(vehiclePower);
		assert vHP.getMeta().equals(vehiclePowerUnit) : vHP.getSupers();
	}

	public void testAttributeOnAttributeWithGetAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Type car = vehicle.newSubType("Car");
		human.setRelation("Drive", car);

		Attribute carPower = car.setAttribute("power");
		Attribute carPowerUnit = carPower.setAttribute("Unit");
		assert carPower != carPowerUnit;
		assert carPower.isAttributeOf(car);
		assert carPowerUnit.isAttributeOf(carPower);
		assert !carPowerUnit.isAttributeOf(vehicle);

		assert car.getAttributes().contains(carPower) : car.getAttributes();
		assert carPower.getAttributes().contains(carPowerUnit) : carPower.getAttributes();
		assert !carPower.isAttributeOf(carPowerUnit);
		assert carPowerUnit.isAttributeOf(carPower);
		assert !carPowerUnit.inheritsFrom(carPower) : carPowerUnit.info();
		assert !carPowerUnit.isAttributeOf(carPowerUnit);
	}

	public void testSimpleAttributeOnAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute("power");

		Holder v235 = vehicle.setValue(vehiclePower, "235");
		Attribute vehiclePowerUnit = vehiclePower.setAttribute("Unit");
		assert vehicle.getHolders(vehiclePower).size() == 1 : v235.info();
		assert vehicle.getHolders(vehiclePower).contains(v235);

		Holder vHP = v235.setValue(vehiclePowerUnit, "HP");
		assert vHP.isAttributeOf(v235) : vHP.getBaseComponent();
		assert v235.getHolders(vehiclePowerUnit).size() == 1 : vHP.info();
		assert v235.getHolders(vehiclePowerUnit).contains(vHP);

		assert vehicle.getHolders(vehiclePower).size() == 1;
		assert vehicle.getHolders(vehiclePower).contains(v235);
	}

	public void testDefaultAttributeValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute("power");
		Holder value = vehicle.setValue(vehiclePower, true);
		assert vehicle.getHolders(vehiclePower).contains(value);
		Generic myVehicle = vehicle.newInstance("myVehicle");
		assert myVehicle.getHolders(vehiclePower).contains(value);
	}

	public void testDefaultPropertyValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.setProperty("power");
		assert vehiclePower.equals(vehicle.getProperty("power")) : vehicle.getAttributes();
		assert vehiclePower.isAlive();
		assert vehicle.getLink((Relation) vehiclePower) == null;
		Holder value = ((Attribute) vehicle).setValue(vehiclePower, true);
		assert vehiclePower.isAlive();

		assert value.getBaseComponent().equals(vehicle);
		assert !value.inheritsFrom(vehicle);
		assert value.isAttributeOf(vehicle);
		assert vehicle.getHolders(vehiclePower).contains(value);
		assert Boolean.TRUE.equals(((Attribute) vehicle).getValue(vehiclePower)) : ((Attribute) vehicle).getValue(vehiclePower);
		assert vehiclePower.isAlive();
		assert vehiclePower.equals(vehicle.getProperty("power")) : vehicle.getAttributes();
	}

	public void testDefaultPropertyValueWithInheritance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType("Car");
		Generic myBmw = car.newInstance("myBmw");
		Attribute vehiclePower = vehicle.setProperty("power");
		assert vehiclePower.equals(car.getProperty("power")) : car.getProperty("power");
		Holder falseVehicle = ((Attribute) vehicle).setValue(vehiclePower, false);
		assert falseVehicle.isAttributeOf(vehicle);
		assert Boolean.FALSE.equals(((Attribute) myBmw).getValue(vehiclePower));
		Holder trueCar = ((Attribute) car).setValue(vehiclePower, true);
		assert trueCar.isAttributeOf(car);
		// assert trueCar.inheritsFrom(falseVehicle);
		assert Boolean.FALSE.equals(((Attribute) vehicle).getValue(vehiclePower)) : ((Attribute) vehicle).getValue(vehiclePower);
		assert Boolean.TRUE.equals(((Attribute) car).getValue(vehiclePower));
		assert Boolean.TRUE.equals(((Attribute) myBmw).getValue(vehiclePower));
	}

}
