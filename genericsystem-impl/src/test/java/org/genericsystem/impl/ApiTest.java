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
import org.genericsystem.exception.GetGenericConstraintVioliationException;
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

	public void testSingularWithMultiInheritance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.setType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute("power").enableSingularConstraint();
		Type car = vehicle.setSubType("Car");
		car.setValue(vehiclePower, 233);
		Type robot = vehicle.setSubType("Robot");
		robot.setValue(vehiclePower, 233);
		cache.setType("Transformer", car, robot);
	}

	public void testSingularWithMultiInheritance2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.setType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute("power").enableSingularConstraint();
		Type car = vehicle.setSubType("Car");
		car.setValue(vehiclePower, 233);
		Type robot = vehicle.setSubType("Robot");
		cache.setType("Transformer", car, robot);
		robot.setValue(vehiclePower, 233);
	}

	public void testSingularWithMultiInheritanceWithValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.setType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute("power").enableSingularConstraint();
		Type car = vehicle.setSubType("Car");
		car.setValue(vehiclePower, 233);
		Type robot = vehicle.setSubType("Robot");
		robot.setValue(vehiclePower, 233);
		Type transformer = cache.setType("Transformer", car, robot);
		transformer.setInstance("myTransformer");
	}

	public void testPropertyWithMultiInheritance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.setType("Vehicle");
		Attribute vehiclePower = vehicle.setProperty("power");
		Type car = vehicle.setSubType("Car");
		car.setValue(vehiclePower, 233);
		Type robot = vehicle.setSubType("Robot");
		robot.setValue(vehiclePower, 233);
		cache.setType("Transformer", car, robot);
	}

	public void testPropertyWithMultiInheritance2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.setType("Vehicle");
		Attribute vehiclePower = vehicle.setProperty("power");
		Type car = vehicle.setSubType("Car");
		car.setValue(vehiclePower, 233);
		Type robot = vehicle.setSubType("Robot");
		cache.setType("Transformer", car, robot);
		robot.setValue(vehiclePower, 233);
	}

	public void testPropertyWithMultiInheritanceWithValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.setType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute("power").enableSingularConstraint();
		Type car = vehicle.setSubType("Car");
		car.setValue(vehiclePower, 233);
		Type robot = vehicle.setSubType("Robot");
		robot.setValue(vehiclePower, 233);
		Type transformer = cache.setType("Transformer", car, robot);
		transformer.setInstance("myTransformer");
	}

	public void specializeGeneric() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Vehicle.class).start();
		Type vehicle = cache.find(Vehicle.class);
		// assert ((CacheImpl) cache).isFlushable(vehicle);
		assert vehicle.isAlive();
		assert vehicle instanceof Vehicle;
	}

	public void specializeAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Vehicle.class, Power.class).start();
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
		Type car = vehicle.addSubType("Car");
		Type vehicle2 = vehicle.setValue("Vehicle2");
		assert vehicle2.getValue().equals("Vehicle2");
		assert !vehicle.isAlive();
		assert !car.isAlive();
		assert vehicle2.getAllSubType("Car").isAlive();
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
		Type car = vehicle.addSubType("Car");
		Type color = cache.addType("Color");
		Type matColor = color.addSubType("MatColor");
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
		Generic myAudi = car.addInstance("myAudi");
		Generic red = color.addInstance("red");

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
		car.addInstance("myAudi");
		car.addInstance("myMercedes");
		car.addInstance("myLada");
		Generic red = color.addInstance("red");
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
		car.addInstance("myAudi");
		car.addInstance("myBmw");
		Generic red = color.addInstance("red");
		time.addInstance("today");
		time.addInstance("tomorrow");
		Link carRed = car.setLink(carColorTime, "carRed", red, time);
		assert !red.getLinks(carColorTime).contains(carRed) : red.getLinks(carColorTime);
	}

	public void deductWithInherits() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		Type color = cache.addType("Color");
		Relation vehicleColor = vehicle.setRelation("VehicleColor", color);
		car.addInstance("myAudi");
		Generic red = color.addInstance("red");
		Link vehicleRed = vehicle.setLink(vehicleColor, "vehicleRed", red);
		assert !red.getLinks(vehicleColor).contains(vehicleRed) : red.getLinks(vehicleColor);
	}

	public void ternaryDeductWithInherits() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		Type color = cache.addType("Color");
		Type time = cache.addType("Time");
		Relation vehicleColor = vehicle.setRelation("VehicleColor", color, time);
		car.addInstance("myAudi");
		Generic red = color.addInstance("red");
		time.addInstance("today");
		time.addInstance("tomorrow");
		Link vehicleRed = vehicle.setLink(vehicleColor, "vehicleRed", red, time);
		assert !red.getLinks(vehicleColor).contains(vehicleRed) : red.getLinks(vehicleColor);
	}

	public void deductWithTwoInstances() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type color = cache.addType("Color");
		Relation carColor = car.setRelation("CarColor", color);
		car.addInstance("myAudi");
		Generic myBmw = car.addInstance("myBmw");
		Generic red = color.addInstance("red");
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
		car.addInstance("myAudi");
		Generic myBmw = car.addInstance("myBmw");
		Generic red = color.addInstance("red");
		Generic today = time.addInstance("today");
		time.addInstance("tomorrow");
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
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		cache.addType("A");
		final Type b = cache.addType("B");
		new RollbackCatcher() {

			@Override
			public void intercept() {
				b.addSubType("A");
			}
		}.assertIsCausedBy(GetGenericConstraintVioliationException.class);
	}

	public void testGetAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Attribute carPower = car.addAttribute("power");
		Type bike = cache.addType("Bike");
		Attribute bikePower = bike.addAttribute("power");
		assert cache.getGeneric("power", carPower.getMeta(), car) == carPower;
		assert cache.getGeneric("power", carPower.getMeta(), bike) == bikePower;
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
		Type car = vehicle.addSubType("Car");
		Type superCar = car.addSubType("SuperCar");
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
		Type car = vehicle.addSubType("Car");
		car.enableReferentialIntegrity(Statics.BASE_POSITION);
		// assert vehicle.getRefenrentialIntegrities().contains(car) : vehicle.getRefenrentialIntegrities();
		assert !vehicle.isRemovable();
	}

	public void testGetReferentialAndIsRemovableWithSubTypes2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		Type superCar = car.addSubType("SuperCar");
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

		Generic myBmw = car.addInstance("myBmw");
		Generic red = color.addInstance("red");
		myBmw.setValue(power, "123");
		myBmw.setLink(carColor, "myBmwRed", red);

		Link carRed = car.setLink(carColor, "CarRed", red);

		Generic myFiat = car.addInstance("myFiat");
		assert myFiat.getLink(carColor) != null;
		assert Objects.equals(myFiat.getLink(carColor), carRed);
		assert myFiat.getTargets(carColor).contains(red);
		// assert myFiat.getTargets( carColor).contains(red);
	}

	// getType() tests

	public void test_get_existing_type() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type expected = cache.addType("Car");

		Type actual = cache.getGeneric("Car", cache.getEngine());
		assert Objects.equals(actual, expected);
	}

	public void test_get_non_existing_type() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		assert cache.getGeneric("Car", cache.getEngine()) == null;
	}

	public void test_get_multiple_existing_types() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type expectedCar = cache.addType("Car");
		Type expectedMoto = cache.addType("Moto");
		Type expectedBus = cache.addType("Bus");

		Type actualCar = cache.getGeneric("Car", cache.getEngine());
		Type actualMoto = cache.getGeneric("Moto", cache.getEngine());
		Type actualBus = cache.getGeneric("Bus", cache.getEngine());

		assert Objects.equals(actualCar, expectedCar);
		assert Objects.equals(actualMoto, expectedMoto);
		assert Objects.equals(actualBus, expectedBus);
	}

	public void test_get_null_type() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type expected = null;
		Type actual = cache.getGeneric(null, cache.getEngine());

		assert Objects.equals(actual, expected);
	}

	public void testNewTypeWithNullValue() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type nullType = cache.addType(null);
		assert nullType.equals(cache.getGeneric(null, cache.getEngine()));
		// nullType.equals(cache.getType(null));
	}

	public void test_get_type_with_hierarchy() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type superCar = car.addSubType("SuperCar");

		assert cache.getGeneric("SuperCar", cache.getEngine()) == superCar;
	}

	// getSubType() tests

	public void test_get_existing_subtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type expected = car.addSubType("Audi");

		Generic actual = car.getAllSubType("Audi");
		assert Objects.equals(actual, expected);
	}

	public void test_get_non_existing_subtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type expected = null;

		Generic actual = car.getAllSubType("Audi");
		assert Objects.equals(actual, expected);
	}

	public void test_get_multiple_existing_subtypes() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type expectedAudi = car.addSubType("Audi");
		Type expectedMercedes = car.addSubType("Mercedes");

		Generic actualAudi = car.getAllSubType("Audi");
		Generic actualMercedes = car.getAllSubType("Mercedes");

		assert Objects.equals(actualAudi, expectedAudi);
		assert Objects.equals(actualMercedes, expectedMercedes);
	}

	public void test_get_null_subtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");

		Type expected = null;
		Generic actual = car.getAllSubType("Audi");

		assert Objects.equals(actual, expected);
	}

	public void test_get_subtype_with_null_value() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type car = cache.addType("Car");
		car.addSubType(null);
	}

	// getAllTypes() tests
	public void test_get_all_existing_types() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type bus = cache.addType("Bus");
		Type moto = cache.addType("Moto");

		Type superCar = car.addSubType("SuperCar");

		Snapshot<Type> types = cache.getAllTypes();
		assert types.size() >= 4;
		assert types.containsAll(Arrays.asList(car, bus, moto, superCar));
	}

	// getInstance tests
	public void test_get_existing_instances() {

		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Generic myBmw = car.addInstance("myBmw");
		Generic myAudi = car.addInstance("myAudi");
		Generic myMercedes = car.addInstance("myMercedes");
		Generic myPeugeot = car.addInstance("myPeugeot");

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

		Generic myBmw = car.addInstance("myBmw");
		Generic red = color.addInstance("red");
		Generic yellow = color.addInstance("yellow");

		Link myBmwYellow = myBmw.setLink(carColor, "myBmwYellow", yellow);
		Link carRed = car.setLink(carColor, "CarRed", red);

		Generic myFiat = car.addInstance("myFiat");

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

		Generic father = human.addInstance("father");
		Generic son = human.addInstance("son");
		Generic daughter = human.addInstance("daughter");

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
		Generic myVehicle = vehicle.addInstance("myVehicle");

		Holder myVehicle20 = myVehicle.setValue(vehicle30, 20);
		assert myVehicle20.inheritsFrom(vehicle30);
		assert myVehicle20.inheritsFrom(vehicle20);

		Holder myVehicle30 = myVehicle.setValue(vehicle20, 30);
		assert myVehicle30.inheritsFrom(vehicle20);
		assert myVehicle30.inheritsFrom(vehicle30);
	}

	public void testInheritance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.addAttribute("power");
		Type car = vehicle.addSubType("Car");
		Holder defaultVehiclePower235 = vehicle.setValue(vehiclePower, 235);
		Holder defaultVehiclePower237 = vehicle.setValue(vehiclePower, 237);
		Holder defaultCarPower235 = car.setValue(vehiclePower, 235);
		Holder defaultCarPower236 = car.setValue(vehiclePower, 236);
		assert defaultCarPower235.inheritsFrom(defaultVehiclePower235);
		assert !defaultCarPower236.inheritsFrom(defaultVehiclePower235);
		Holder defaultCarPower237 = car.setValue(defaultVehiclePower235, 237);
		assert defaultCarPower237.inheritsFrom(defaultVehiclePower235);
		assert defaultCarPower237.inheritsFrom(defaultVehiclePower237);
	}

	public void testTreeDefault() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute equipment = vehicle.addAttribute("Equipment");
		Holder airConditioning = vehicle.setValue(equipment, "air conditioning");
		Holder metalPaint = vehicle.setValue(equipment, "metal paint");
		Generic myVehicle = vehicle.setInstance("myVehicle");
		Generic myVehicleAirConditioning = myVehicle.setHolder(airConditioning, "metal paint");
		assert myVehicleAirConditioning.inheritsFrom(metalPaint);
		assert myVehicleAirConditioning.inheritsFrom(airConditioning);
		assert myVehicle.getHolders(equipment).size() == 1 : myVehicle.getHolders(equipment);
	}

	public void testOverrideAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.addAttribute("power");
		Type car = vehicle.addSubType("Car");
		Relation carPower = ((GenericImpl) car).addSubAttribute(vehiclePower, "power");
		Generic myCar = car.addInstance("myCar");
		Holder myCar233 = myCar.setValue(vehiclePower, 233);
		assert myCar233.inheritsFrom(carPower);
	}

	public void testOverrideAttributeOverrideName() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.addAttribute("power");
		Type car = vehicle.addSubType("Car");
		Relation carPower = ((GenericImpl) car).addSubAttribute(vehiclePower, "power2");
		Generic myCar = car.addInstance("myCar");
		Holder myCar233 = myCar.setValue(vehiclePower, 233);
		assert !myCar233.inheritsFrom(carPower) : myCar233.info();
	}

	public void testOverrideProperty() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.addProperty("power");
		Type car = vehicle.addSubType("Car");
		Relation carPower = ((GenericImpl) car).addSubProperty(vehiclePower, "power");
		Generic myCar = car.addInstance("myCar");
		Holder myCar233 = myCar.setValue(vehiclePower, 233);
		assert myCar233.inheritsFrom(carPower) : myCar233.info();
	}

	public void testOverridePropertyOverrideName() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.addProperty("power");
		Type car = vehicle.addSubType("Car");
		Relation carPower = ((GenericImpl) car).addSubProperty(vehiclePower, "power2");
		Generic myCar = car.addInstance("myCar");
		Holder myCar233 = myCar.setValue(vehiclePower, 233);
		assert !myCar233.inheritsFrom(carPower) : myCar233.info();
	}

	public void testMultipleInheritance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.addProperty("Power");
		vehiclePower.enableVirtualConstraint();
		Type robot = cache.addType("Robot");
		Attribute robotPower = robot.addProperty("Power");
		Type transformer = cache.addType("Transformer", vehicle, robot);
		assert transformer.inheritsFrom(robot);
		assert transformer.inheritsFrom(vehicle);
		assert cache.getMetaAttribute().inheritsFrom(cache.getEngine());
		Attribute transformerPower = ((GenericImpl) transformer).addProperty("Power");
		assert transformerPower.inheritsFrom(vehiclePower);
		assert transformerPower.inheritsFrom(robotPower);
		assert transformerPower.isPropertyConstraintEnabled();
		assert !transformerPower.isVirtualConstraintEnabled();
	}

	public void testMultipleInheritanceDiamond() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		vehicle.addProperty("power");
		Type car = vehicle.addSubType("Car");
		Attribute carPower = car.addProperty("power");
		Type robot = vehicle.addSubType("Robot");
		Attribute robotPower = robot.addProperty("power");
		Type transformer = cache.addType("Transformer", car, robot);
		Relation transformerPower = ((GenericImpl) transformer).addProperty("power");
		assert transformerPower.inheritsFrom(carPower);
		assert transformerPower.inheritsFrom(robotPower);
		assert transformerPower.isPropertyConstraintEnabled();
	}
}
