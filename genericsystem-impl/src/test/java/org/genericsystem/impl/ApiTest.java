package org.genericsystem.impl;

import java.util.Objects;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class ApiTest extends AbstractTest {

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

		Generic yellow = color.newInstance(cache, "yellow");
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
}
