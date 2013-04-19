package org.genericsystem.impl;

import java.nio.channels.IllegalSelectorException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Snapshot;
import org.genericsystem.core.Statics;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class ApiTest extends AbstractTest {

	public void testRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation(cache, "CarColor", color);
		Generic myAudi = car.newInstance(cache, "myAudi");
		Generic red = color.newInstance(cache, "red");
		Link carRed = car.bind(cache, carColor, red);
		Link myAudiRed = myAudi.bind(cache, carColor, red);
		assert myAudiRed.inheritsFrom(carRed);
		assert !red.getLinks(cache, carColor).contains(carRed) : red.getLinks(cache, carColor);
	}

	public void deduct() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation(cache, "CarColor", color);
		car.newInstance(cache, "myAudi");
		Generic red = color.newInstance(cache, "red");
		Link carRed = car.setLink(cache, carColor, "carRed", red);
		assert !red.getLinks(cache, carColor).contains(carRed) : red.getLinks(cache, carColor);
	}

	public void ternaryDeduct() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Type time = cache.newType("Time");
		Relation carColorTime = car.setRelation(cache, "CarColorTime", color, time);
		car.newInstance(cache, "myAudi");
		car.newInstance(cache, "myBmw");
		Generic red = color.newInstance(cache, "red");
		time.newInstance(cache, "today");
		time.newInstance(cache, "tomorrow");
		Link carRed = car.setLink(cache, carColorTime, "carRed", red, time);
		assert !red.getLinks(cache, carColorTime).contains(carRed) : red.getLinks(cache, carColorTime);
	}

	public void deductWithInherits() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Type color = cache.newType("Color");
		Relation vehicleColor = vehicle.setRelation(cache, "VehicleColor", color);
		car.newInstance(cache, "myAudi");
		Generic red = color.newInstance(cache, "red");
		Link vehicleRed = vehicle.setLink(cache, vehicleColor, "vehicleRed", red);
		assert !red.getLinks(cache, vehicleColor).contains(vehicleRed) : red.getLinks(cache, vehicleColor);
	}

	public void ternaryDeductWithInherits() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Type color = cache.newType("Color");
		Type time = cache.newType("Time");
		Relation vehicleColor = vehicle.setRelation(cache, "VehicleColor", color, time);
		car.newInstance(cache, "myAudi");
		Generic red = color.newInstance(cache, "red");
		time.newInstance(cache, "today");
		time.newInstance(cache, "tomorrow");
		Link vehicleRed = vehicle.setLink(cache, vehicleColor, "vehicleRed", red, time);
		assert !red.getLinks(cache, vehicleColor).contains(vehicleRed) : red.getLinks(cache, vehicleColor);
	}

	public void deductWithTwoInstances() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation(cache, "CarColor", color);
		car.newInstance(cache, "myAudi");
		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic red = color.newInstance(cache, "red");
		Link carRed = car.bind(cache, carColor, red);
		Link myBmwRed = myBmw.bind(cache, carRed, red);
		assert !red.getLinks(cache, carColor).contains(carRed) : red.getLinks(cache, carColor);
		assert red.getLinks(cache, carColor).contains(myBmwRed) : red.getLinks(cache, carColor);
	}

	public void ternaryDeductWithTwoInstances() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Type time = cache.newType("Time");
		Relation carColorTime = car.setRelation(cache, "CarColorTime", color, time);
		car.newInstance(cache, "myAudi");
		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic red = color.newInstance(cache, "red");
		Generic today = time.newInstance(cache, "today");
		time.newInstance(cache, "tomorrow");
		Link carRedTime = car.bind(cache, carColorTime, red, time);
		Link myBmwRed = myBmw.bind(cache, carRedTime, red, today);
		assert !red.getLinks(cache, carColorTime).contains(carRedTime);
		assert red.getLinks(cache, carColorTime).contains(myBmwRed) : red.getLinks(cache, carColorTime);
	}

	public void testOrderedGenerics() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle1 = cache.newType("Vehicle1");
		Engine engine = cache.getEngine();
		Cache otherCache = engine.newCache();
		Type vehicle2 = otherCache.newType("Vehicle2");
		Set<Generic> orderedGenerics = new TreeSet<>();
		orderedGenerics.add(vehicle2);
		orderedGenerics.add(vehicle1);
		Iterator<Generic> iterator = orderedGenerics.iterator();
		assert iterator.next().equals(vehicle1);
		assert iterator.next().equals(vehicle2);
	}

	public void testOrderedGenericsWithCommit() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle1 = cache.newType("Vehicle1");
		Engine engine = cache.getEngine();
		Cache otherCache = engine.newCache();
		Type vehicle2 = otherCache.newType("Vehicle2");
		otherCache.flush();
		Set<Generic> orderedGenerics = new TreeSet<>();
		orderedGenerics.add(vehicle1);
		orderedGenerics.add(vehicle2);
		Iterator<Generic> iterator = orderedGenerics.iterator();
		assert iterator.next().equals(vehicle2);
		assert iterator.next().equals(vehicle1);
	}

	public void testCyclicInherits() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type a = cache.newType("A");
		a.newSubType(cache, "B");
		final Type b = cache.newType("B");
		new RollbackCatcher() {

			@Override
			public void intercept() {
				b.newSubType(cache, "A");
			}
		}.assertIsCausedBy(IllegalSelectorException.class);
	}

	public void testGetReferentialAndIsRemovable() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute power = vehicle.setProperty(cache, "Power");
		power.enableReferentialIntegrity(cache, Statics.BASE_POSITION);
		assert vehicle.getRefenrentialIntegrities(cache).contains(power) : vehicle.getRefenrentialIntegrities(cache);
		assert !vehicle.isRemovable(cache);
	}

	public void testGetReferentialAndIsRemovable2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Type superCar = car.newSubType(cache, "SuperCar");
		Attribute power = vehicle.setProperty(cache, "Power");
		power.enableReferentialIntegrity(cache, Statics.BASE_POSITION);
		assert vehicle.getRefenrentialIntegrities(cache).contains(power) : vehicle.getRefenrentialIntegrities(cache);
		assert car.getRefenrentialIntegrities(cache).contains(superCar) : car.getRefenrentialIntegrities(cache);
		assert superCar.getRefenrentialIntegrities(cache).isEmpty();
		assert !vehicle.isRemovable(cache);
		assert !car.isRemovable(cache);
		assert superCar.isRemovable(cache);
	}

	public void testGetReferentialAndIsRemovableWithSubTypes() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		car.enableReferentialIntegrity(cache, Statics.BASE_POSITION);
		assert vehicle.getRefenrentialIntegrities(cache).contains(car) : vehicle.getRefenrentialIntegrities(cache);
		assert !vehicle.isRemovable(cache);
	}

	public void testGetReferentialAndIsRemovableWithSubTypes2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Type superCar = car.newSubType(cache, "SuperCar");
		superCar.enableReferentialIntegrity(cache, Statics.BASE_POSITION);
		assert vehicle.getRefenrentialIntegrities(cache).contains(superCar) : vehicle.getRefenrentialIntegrities(cache);
		assert !vehicle.isRemovable(cache);
	}

	@Test(enabled = false)
	public void test_simple_api() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Attribute power = car.setProperty(cache, "Power");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation(cache, "CarColor", color);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic red = color.newInstance(cache, "red");
		myBmw.setValue(cache, power, "123");
		myBmw.setLink(cache, carColor, "myBmwRed", red);

		Link carRed = car.setLink(cache, carColor, "CarRed", red);

		Generic myFiat = car.newInstance(cache, "myFiat");
		assert myFiat.getLink(cache, carColor) != null;
		assert Objects.equals(myFiat.getLink(cache, carColor), carRed);
		assert myFiat.getTargets(cache, carColor).contains(red);
		// assert myFiat.getTargets(cache, carColor).contains(red);
	}

	// getType() tests

	public void test_get_existing_type() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type expected = cache.newType("Car");

		Type actual = cache.getType("Car");
		assert Objects.equals(actual, expected);
	}

	public void test_get_non_existing_type() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type expected = null;

		Type actual = cache.getType("Moto");

		assert Objects.equals(actual, expected);
	}

	public void test_get_multiple_existing_types() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type expectedCar = cache.newType("Car");
		Type expectedMoto = cache.newType("Moto");
		Type expectedBus = cache.newType("Bus");

		Type actualCar = cache.getType("Car");
		Type actualMoto = cache.getType("Moto");
		Type actualBus = cache.getType("Bus");

		assert Objects.equals(actualCar, expectedCar);
		assert Objects.equals(actualMoto, expectedMoto);
		assert Objects.equals(actualBus, expectedBus);
	}

	public void test_get_null_type() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type expected = null;
		Type actual = cache.getType(null);

		assert Objects.equals(actual, expected);
	}

	public void test_get_type_with_null_value() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type expected = cache.newType(null);
		Type actual = cache.getType(null);

		assert Objects.equals(actual, expected);
	}

	public void test_get_type_with_hierarchy() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type superCar = car.newSubType(cache, "SuperCar");

		assert cache.getType("SuperCar") == superCar;
	}

	// getSubType() tests

	public void test_get_existing_subtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type expected = car.newSubType(cache, "Audi");

		Generic actual = car.getSubType(cache, "Audi");
		assert Objects.equals(actual, expected);
	}

	public void test_get_non_existing_subtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type expected = null;

		Generic actual = car.getSubType(cache, "Audi");
		assert Objects.equals(actual, expected);
	}

	public void test_get_multiple_existing_subtypes() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type expectedAudi = car.newSubType(cache, "Audi");
		Type expectedMercedes = car.newSubType(cache, "Mercedes");

		Generic actualAudi = car.getSubType(cache, "Audi");
		Generic actualMercedes = car.getSubType(cache, "Mercedes");

		assert Objects.equals(actualAudi, expectedAudi);
		assert Objects.equals(actualMercedes, expectedMercedes);
	}

	public void test_get_null_subtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");

		Type expected = null;
		Generic actual = car.getSubType(cache, "Audi");

		assert Objects.equals(actual, expected);
	}

	public void test_get_subtype_with_null_value() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		// car.enableNotNullConstraint(cache);
		Type expected = car.newSubType(cache, null);
		Generic actual = car.getSubType(cache, null);

		assert Objects.equals(actual, expected);
	}

	// getAllTypes() tests
	public void test_get_all_existing_types() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type bus = cache.newType("Bus");
		Type moto = cache.newType("Moto");

		Type superCar = car.newSubType(cache, "SuperCar");

		Snapshot<Type> types = cache.getTypes();
		assert types.size() >= 4;
		assert types.containsAll(Arrays.asList(car, bus, moto, superCar));
	}

	// getInstance tests
	public void test_get_existing_instances() {

		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic myAudi = car.newInstance(cache, "myAudi");
		Generic myMercedes = car.newInstance(cache, "myMercedes");
		Generic myPeugeot = car.newInstance(cache, "myPeugeot");

		assert car.getInstance(cache, "myBmw") == myBmw;
		assert car.getInstance(cache, "myAudi") == myAudi;
		assert car.getInstance(cache, "myMercedes") == myMercedes;
		assert car.getInstance(cache, "myPeugeot") == myPeugeot;
	}

	public void test_get_non_existing_instances() {

		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");

		assert car.getInstance(cache, "myAudi") == null;
	}

	// getLink() tests
	@Test(enabled = false)
	public void test_dummy() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation(cache, "CarColor", color);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic red = color.newInstance(cache, "red");
		Generic yellow = color.newInstance(cache, "yellow");

		Link myBmwYellow = myBmw.setLink(cache, carColor, "myBmwYellow", yellow);
		Link carRed = car.setLink(cache, carColor, "CarRed", red);

		Generic myFiat = car.newInstance(cache, "myFiat");

		assert myBmw.getLink(cache, carColor) != null;
		assert myBmw.getLinks(cache, carColor).size() == 2;
		assert myBmw.getLinks(cache, carColor).contains(myBmwYellow);
		assert myFiat.getLink(cache, carColor) != null;
		assert Objects.equals(myFiat.getLink(cache, carColor), carRed);
		assert myFiat.getTargets(cache, carColor).contains(red);
	}

	public void test_retrieve_a_father_child_relation_by_the_father() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type human = cache.newType("Homme");

		Relation family = human.setRelation(cache, "family", human);

		Generic father = human.newInstance(cache, "father");
		Generic son = human.newInstance(cache, "son");
		Generic daughter = human.newInstance(cache, "daughter");

		Link fatherSon = father.setLink(cache, family, "fatherSon", son);
		Link fatherDaughter = father.setLink(cache, family, "fatherDaughter", daughter);

		// assert father.getLink(cache, family, 0) == fatherSon;
		assert son.getLink(cache, family, 1) == fatherSon;
		assert daughter.getLink(cache, family, 1) == fatherDaughter;
	}
}
