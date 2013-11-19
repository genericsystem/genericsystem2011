package org.genericsystem.impl;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.core.Cache;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Snapshot;
import org.genericsystem.core.Statics;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class ApiTest extends AbstractTest {

	@SystemGeneric
	public static class Vehicle extends GenericImpl {

	}

	@SystemGeneric
	@Components(Vehicle.class)
	public static class Power extends GenericImpl {

	}

	public void specializeGeneric() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Vehicle.class).start();
		Type vehicle = cache.find(Vehicle.class);
		// assert ((CacheImpl) cache).isFlushable(vehicle);
		assert vehicle.isAlive();
		assert vehicle instanceof Vehicle;
	}

	public void specializeAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Vehicle.class, Power.class);
		Attribute power = cache.find(Power.class);
		assert power instanceof Power;
	}

	public void testUpdate() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		assert vehicle.setValue("Vehicle2").getValue().equals("Vehicle2");
		assert !vehicle.isAlive();
	}

	public void testUpdateWithSubType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.newSubType("Car");
		Type vehicle2 = vehicle.setValue("Vehicle2");
		assert vehicle2.getValue().equals("Vehicle2");
		assert !vehicle.isAlive();
		assert !car.isAlive();
		assert vehicle2.getSubType("Car").isAlive();
	}

	public void testUpdateWithSubRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type color = cache.addType("Color");
		Relation vehicleColor = vehicle.setRelation("VehicleColor", color);
		Relation vehicleColor2 = vehicleColor.setValue("VehicleColor2");
		assert vehicleColor2.getValue().equals("VehicleColor2");
		assert !vehicleColor.isAlive();
		assert vehicle.getRelation("VehicleColor2").isAlive();
	}

	public void testUpdateWithSubRelation2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.newSubType("Car");
		Type color = cache.addType("Color");
		Type matColor = color.newSubType("MatColor");
		Relation vehicleColor = vehicle.setRelation("VehicleColor", color);
		car.setRelation("CarMatColor", matColor);
		Relation vehicleColor2 = vehicleColor.setValue("VehicleColor2");
		assert vehicleColor2.getValue().equals("VehicleColor2");
		assert !vehicleColor.isAlive();
		assert car.getRelation("CarMatColor").isAlive();
	}

	public void testRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type color = cache.addType("Color");
		Relation carColor = car.setRelation("CarColor", color);
		Generic myAudi = car.newInstance("myAudi");
		Generic red = color.newInstance("red");
		Link carRed = car.bind(carColor, red);
		Link myAudiRed = myAudi.bind(carColor, red);
		assert myAudiRed.inheritsFrom(carRed);
		assert !red.getLinks(carColor).contains(carRed) : red.getLinks(carColor);
	}

	public void deduct() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type color = cache.addType("Color");
		Relation carColor = car.setRelation("CarColor", color);
		car.newInstance("myAudi");
		car.newInstance("myMercedes");
		car.newInstance("myLada");
		Generic red = color.newInstance("red");
		Link carRed = car.setLink(carColor, "carRed", red);
		assert red.getLinks(carColor).size() == 3;
		assert !red.getLinks(carColor).contains(carRed);
	}

	public void ternaryDeduct() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type color = cache.addType("Color");
		Type time = cache.addType("Time");
		Relation carColorTime = car.setRelation("CarColorTime", color, time);
		car.newInstance("myAudi");
		car.newInstance("myBmw");
		Generic red = color.newInstance("red");
		time.newInstance("today");
		time.newInstance("tomorrow");
		Link carRed = car.setLink(carColorTime, "carRed", red, time);
		assert !red.getLinks(carColorTime).contains(carRed) : red.getLinks(carColorTime);
	}

	public void deductWithInherits() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.newSubType("Car");
		Type color = cache.addType("Color");
		Relation vehicleColor = vehicle.setRelation("VehicleColor", color);
		car.newInstance("myAudi");
		Generic red = color.newInstance("red");
		Link vehicleRed = vehicle.setLink(vehicleColor, "vehicleRed", red);
		assert !red.getLinks(vehicleColor).contains(vehicleRed) : red.getLinks(vehicleColor);
	}

	public void ternaryDeductWithInherits() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.newSubType("Car");
		Type color = cache.addType("Color");
		Type time = cache.addType("Time");
		Relation vehicleColor = vehicle.setRelation("VehicleColor", color, time);
		car.newInstance("myAudi");
		Generic red = color.newInstance("red");
		time.newInstance("today");
		time.newInstance("tomorrow");
		Link vehicleRed = vehicle.setLink(vehicleColor, "vehicleRed", red, time);
		assert !red.getLinks(vehicleColor).contains(vehicleRed) : red.getLinks(vehicleColor);
	}

	public void deductWithTwoInstances() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type color = cache.addType("Color");
		Relation carColor = car.setRelation("CarColor", color);
		car.newInstance("myAudi");
		Generic myBmw = car.newInstance("myBmw");
		Generic red = color.newInstance("red");
		Link carRed = car.bind(carColor, red);
		Link myBmwRed = myBmw.bind(carRed, red);
		assert !red.getLinks(carColor).contains(carRed) : red.getLinks(carColor);
		assert red.getLinks(carColor).contains(myBmwRed) : red.getLinks(carColor);
	}

	public void ternaryDeductWithTwoInstances() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type color = cache.addType("Color");
		Type time = cache.addType("Time");
		Relation carColorTime = car.setRelation("CarColorTime", color, time);
		car.newInstance("myAudi");
		Generic myBmw = car.newInstance("myBmw");
		Generic red = color.newInstance("red");
		Generic today = time.newInstance("today");
		time.newInstance("tomorrow");
		Link carRedTime = car.bind(carColorTime, red, time);
		Link myBmwRed = myBmw.bind(carRedTime, red, today);
		assert !red.getLinks(carColorTime).contains(carRedTime);
		assert red.getLinks(carColorTime).contains(myBmwRed) : red.getLinks(carColorTime);
	}

	public void testOrderedGenerics() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle1 = cache.addType("Vehicle1");
		Engine engine = cache.getEngine();
		Cache otherCache = engine.newCache().start();
		Type vehicle2 = otherCache.addType("Vehicle2");
		Set<Generic> orderedGenerics = new TreeSet<>();
		orderedGenerics.add(vehicle2);
		orderedGenerics.add(vehicle1);
		Iterator<Generic> iterator = orderedGenerics.iterator();
		assert iterator.next().equals(vehicle1);
		assert iterator.next().equals(vehicle2);
	}

	public void testOrderedGenericsWithCommit() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle1 = cache.addType("Vehicle1");
		Engine engine = cache.getEngine();
		Cache otherCache = engine.newCache().start();
		Type vehicle2 = otherCache.addType("Vehicle2");
		otherCache.flush();
		Set<Generic> orderedGenerics = new TreeSet<>();
		orderedGenerics.add(vehicle1);
		orderedGenerics.add(vehicle2);
		Iterator<Generic> iterator = orderedGenerics.iterator();
		assert iterator.next().equals(vehicle2);
		assert iterator.next().equals(vehicle1);
	}

	public void testCyclicInherits() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type a = cache.addType("A");
		a.newSubType("B");
		final Type b = cache.addType("B");
		// new RollbackCatcher() {
		//
		// @Override
		// public void intercept() {
		b.newSubType("A");
		// }
		// }.assertIsCausedBy(FunctionalConsistencyViolationException.class);
	}

	public void testGetReferentialAndIsRemovable() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute power = vehicle.setProperty("Power");
		power.enableReferentialIntegrity(Statics.BASE_POSITION);
		// assert vehicle.getRefenrentialIntegrities().contains(power) : vehicle.getRefenrentialIntegrities();
		assert !vehicle.isRemovable();
	}

	public void testGetReferentialAndIsRemovable2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.newSubType("Car");
		Type superCar = car.newSubType("SuperCar");
		Attribute power = vehicle.setProperty("Power");
		power.enableReferentialIntegrity(Statics.BASE_POSITION);
		// assert vehicle.getRefenrentialIntegrities().contains(power) : vehicle.getRefenrentialIntegrities();
		// assert car.getRefenrentialIntegrities().contains(superCar) : car.getRefenrentialIntegrities();
		// assert superCar.getRefenrentialIntegrities().isEmpty();
		assert !vehicle.isRemovable();
		assert !car.isRemovable();
		assert superCar.isRemovable();
	}

	public void testGetReferentialAndIsRemovableWithSubTypes() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.newSubType("Car");
		car.enableReferentialIntegrity(Statics.BASE_POSITION);
		// assert vehicle.getRefenrentialIntegrities().contains(car) : vehicle.getRefenrentialIntegrities();
		assert !vehicle.isRemovable();
	}

	public void testGetReferentialAndIsRemovableWithSubTypes2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.newSubType("Car");
		Type superCar = car.newSubType("SuperCar");
		superCar.enableReferentialIntegrity(Statics.BASE_POSITION);
		// assert vehicle.getRefenrentialIntegrities().contains(superCar) : vehicle.getRefenrentialIntegrities();
		assert !vehicle.isRemovable();
	}

	@Test(enabled = false)
	public void test_simple_api() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Attribute power = car.setProperty("Power");
		Type color = cache.addType("Color");
		Relation carColor = car.setRelation("CarColor", color);

		Generic myBmw = car.newInstance("myBmw");
		Generic red = color.newInstance("red");
		myBmw.setValue(power, "123");
		myBmw.setLink(carColor, "myBmwRed", red);

		Link carRed = car.setLink(carColor, "CarRed", red);

		Generic myFiat = car.newInstance("myFiat");
		assert myFiat.getLink(carColor) != null;
		assert Objects.equals(myFiat.getLink(carColor), carRed);
		assert myFiat.getTargets(carColor).contains(red);
		// assert myFiat.getTargets( carColor).contains(red);
	}

	// getType() tests

	public void test_get_existing_type() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type expected = cache.addType("Car");

		Type actual = cache.getType("Car");
		assert Objects.equals(actual, expected);
	}

	public void test_get_non_existing_type() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		assert cache.getType("Moto") == null;
	}

	public void test_get_multiple_existing_types() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type expectedCar = cache.addType("Car");
		Type expectedMoto = cache.addType("Moto");
		Type expectedBus = cache.addType("Bus");

		Type actualCar = cache.getType("Car");
		Type actualMoto = cache.getType("Moto");
		Type actualBus = cache.getType("Bus");

		assert Objects.equals(actualCar, expectedCar);
		assert Objects.equals(actualMoto, expectedMoto);
		assert Objects.equals(actualBus, expectedBus);
	}

	public void test_get_null_type() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type expected = null;
		Type actual = cache.getType(null);

		assert Objects.equals(actual, expected);
	}

	public void testNewTypeWithNullValue() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type nullType = cache.addType(null);
		assert nullType.equals(cache.getType(null));
		// nullType.equals(cache.getType(null));
	}

	public void test_get_type_with_hierarchy() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type superCar = car.newSubType("SuperCar");

		assert cache.getType("SuperCar") == superCar;
	}

	// getSubType() tests

	public void test_get_existing_subtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type expected = car.newSubType("Audi");

		Generic actual = car.getSubType("Audi");
		assert Objects.equals(actual, expected);
	}

	public void test_get_non_existing_subtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type expected = null;

		Generic actual = car.getSubType("Audi");
		assert Objects.equals(actual, expected);
	}

	public void test_get_multiple_existing_subtypes() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type expectedAudi = car.newSubType("Audi");
		Type expectedMercedes = car.newSubType("Mercedes");

		Generic actualAudi = car.getSubType("Audi");
		Generic actualMercedes = car.getSubType("Mercedes");

		assert Objects.equals(actualAudi, expectedAudi);
		assert Objects.equals(actualMercedes, expectedMercedes);
	}

	public void test_get_null_subtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");

		Type expected = null;
		Generic actual = car.getSubType("Audi");

		assert Objects.equals(actual, expected);
	}

	public void test_get_subtype_with_null_value() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type car = cache.addType("Car");
		car.newSubType(null);
	}

	// getAllTypes() tests
	public void test_get_all_existing_types() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type bus = cache.addType("Bus");
		Type moto = cache.addType("Moto");

		Type superCar = car.newSubType("SuperCar");

		Snapshot<Type> types = cache.getAllTypes();
		assert types.size() >= 4;
		assert types.containsAll(Arrays.asList(car, bus, moto, superCar));
	}

	// getInstance tests
	public void test_get_existing_instances() {

		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Generic myBmw = car.newInstance("myBmw");
		Generic myAudi = car.newInstance("myAudi");
		Generic myMercedes = car.newInstance("myMercedes");
		Generic myPeugeot = car.newInstance("myPeugeot");

		assert car.getInstance("myBmw") == myBmw;
		assert car.getInstance("myAudi") == myAudi;
		assert car.getInstance("myMercedes") == myMercedes;
		assert car.getInstance("myPeugeot") == myPeugeot;
	}

	public void test_get_non_existing_instances() {

		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");

		assert car.getInstance("myAudi") == null;
	}

	// getLink() tests
	@Test(enabled = false)
	public void test_dummy() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type color = cache.addType("Color");
		Relation carColor = car.setRelation("CarColor", color);

		Generic myBmw = car.newInstance("myBmw");
		Generic red = color.newInstance("red");
		Generic yellow = color.newInstance("yellow");

		Link myBmwYellow = myBmw.setLink(carColor, "myBmwYellow", yellow);
		Link carRed = car.setLink(carColor, "CarRed", red);

		Generic myFiat = car.newInstance("myFiat");

		assert myBmw.getLink(carColor) != null;
		assert myBmw.getLinks(carColor).size() == 2;
		assert myBmw.getLinks(carColor).contains(myBmwYellow);
		assert myFiat.getLink(carColor) != null;
		assert Objects.equals(myFiat.getLink(carColor), carRed);
		assert myFiat.getTargets(carColor).contains(red);
	}

	public void test_retrieve_a_father_child_relation_by_the_father() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type human = cache.addType("Homme");

		Relation family = human.setRelation("family", human);

		Generic father = human.newInstance("father");
		Generic son = human.newInstance("son");
		Generic daughter = human.newInstance("daughter");

		Link fatherSon = father.setLink(family, "fatherSon", son);
		Link fatherDaughter = father.setLink(family, "fatherDaughter", daughter);

		// assert father.getLink( family, 0) == fatherSon;
		assert son.getLink(family, 1) == fatherSon;
		assert daughter.getLink(family, 1) == fatherDaughter;
	}

	public void testTwoDefault() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.addAttribute("power");
		Holder vehicle20 = vehicle.setValue(vehiclePower, 20);
		Holder vehicle30 = vehicle.setValue(vehiclePower, 30);
		Generic myVehicle = vehicle.newInstance("myVehicle");
		Holder myVehicle20 = myVehicle.setValue(vehicle30, 20);
		assert myVehicle20.inheritsFrom(vehicle30);
		assert !myVehicle20.inheritsFrom(vehicle20);
		Holder myVehicle30 = myVehicle.setValue(vehicle20, 30);
		assert myVehicle30.inheritsFrom(vehicle20);
		assert !myVehicle30.inheritsFrom(vehicle30);
	}

	public void testTreeDefault() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.addAttribute("power");
		Holder vehicle10 = vehicle.setValue(vehiclePower, 10);
		Holder vehicle20 = vehicle.setValue(vehiclePower, 20);
		Holder vehicle30 = vehicle.setValue(vehiclePower, 30);

		Type car = vehicle.newSubType("Car");
		Holder car20 = car.setValue(vehicle10, 20);
		Holder car30 = car.setValue(vehicle20, 30);
		Holder car10 = car.setValue(vehicle30, 10);

		Generic superCar = car.newSubType("superCar");
		Holder superCar30 = car.setValue(car20, 30);
		Holder superCar10 = car.setValue(car30, 10);
		Holder superCar20 = car.setValue(car10, 20);

		assert superCar30.inheritsFrom(car20);
		assert !superCar30.inheritsFrom(car30);
		assert !superCar30.inheritsFrom(car10);

		assert !superCar10.inheritsFrom(car20);
		assert superCar10.inheritsFrom(car30);
		assert !superCar10.inheritsFrom(car10);

		assert !superCar20.inheritsFrom(car20);
		assert !superCar20.inheritsFrom(car30);
		assert superCar20.inheritsFrom(car10);

		assert superCar30.inheritsFrom(vehicle10);
		assert !superCar30.inheritsFrom(vehicle20);
		assert !superCar30.inheritsFrom(vehicle30);

		assert !superCar10.inheritsFrom(vehicle10);
		assert superCar10.inheritsFrom(vehicle20);
		assert !superCar10.inheritsFrom(vehicle30);

		assert !superCar20.inheritsFrom(vehicle10);
		assert !superCar20.inheritsFrom(vehicle20);
		assert superCar20.inheritsFrom(vehicle30);
	}

	public void testOverrideAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.addAttribute("power");
		Type car = vehicle.newSubType("Car");
		Relation carPower = ((GenericImpl) car).addSubAttribute(vehiclePower, "power");
		Generic myCar = car.newInstance("myCar");
		Holder myCar233 = myCar.setValue(vehiclePower, 233);
		assert myCar233.inheritsFrom(carPower);
	}

	public void testOverrideAttributeOverrideName() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.addAttribute("power");
		Type car = vehicle.newSubType("Car");
		Relation carPower = ((GenericImpl) car).addSubAttribute(vehiclePower, "power2");
		Generic myCar = car.newInstance("myCar");
		Holder myCar233 = myCar.setValue(vehiclePower, 233);
		assert myCar233.inheritsFrom(carPower) : myCar233.info();
	}

	public void testOverrideProperty() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.addProperty("power");
		Type car = vehicle.newSubType("Car");
		Relation carPower = ((GenericImpl) car).addSubProperty(vehiclePower, "power");
		Generic myCar = car.newInstance("myCar");
		Holder myCar233 = myCar.setValue(vehiclePower, 233);
		assert myCar233.inheritsFrom(carPower) : myCar233.info();
	}

	public void testOverridePropertyOverrideName() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.addProperty("power");
		Type car = vehicle.newSubType("Car");
		Relation carPower = ((GenericImpl) car).addSubProperty(vehiclePower, "power2");
		Generic myCar = car.newInstance("myCar");
		Holder myCar233 = myCar.setValue(vehiclePower, 233);
		assert myCar233.inheritsFrom(carPower) : myCar233.info();
	}

	public void testMultipleInheritance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.addProperty("power");
		Type robot = cache.addType("Robot");
		Attribute robotPower = robot.addProperty("power");
		Type transformer = cache.addType("Transformer", vehicle, robot);
		Relation transformerPower = ((GenericImpl) transformer).addProperty("power");
		assert transformerPower.inheritsFrom(vehiclePower);
		assert transformerPower.inheritsFrom(robotPower);
	}

	public void testMultipleInheritanceDiamond() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.addProperty("power");
		Type car = vehicle.newSubType("Car");
		Attribute carPower = car.addProperty("power");
		assert carPower.inheritsFrom(vehiclePower);
		Type robot = vehicle.newSubType("Robot");
		Attribute robotPower = robot.addProperty("power");
		assert robotPower.inheritsFrom(vehiclePower);
		Type transformer = cache.addType("Transformer", car, robot);
		Relation transformerPower = ((GenericImpl) transformer).addProperty("power");
		assert transformerPower.isSingularConstraintEnabled();
		assert transformerPower.inheritsFrom(carPower);
		assert transformerPower.inheritsFrom(robotPower);
	}
}
