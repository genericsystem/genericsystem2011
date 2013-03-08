package org.genericsystem.impl;

import org.fest.assertions.Assertions;
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
		Assertions.assertThat(myFiat.getLink(cache, carColor)).isNotNull();
		Assertions.assertThat(myFiat.getLink(cache, carColor)).isEqualTo(carRed);

		Assertions.assertThat(myFiat.getTargets(cache, carColor)).containsOnly(red);
		// assert myFiat.getTargets(cache, carColor).contains(red);
	}

	// getType() tests

	public void test_get_existing_type() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type expected = cache.newType("Car");

		Type actual = cache.getType("Car");
		Assertions.assertThat(actual).isEqualTo(expected);
	}

	public void test_get_non_existing_type() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type expected = null;

		Type actual = cache.getType("Moto");
		Assertions.assertThat(actual).isEqualTo(expected);
	}

	public void test_get_multiple_existing_types() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type expectedCar = cache.newType("Car");
		Type expectedMoto = cache.newType("Moto");
		Type expectedBus = cache.newType("Bus");

		Type actualCar = cache.getType("Car");
		Type actualMoto = cache.getType("Moto");
		Type actualBus = cache.getType("Bus");
		Assertions.assertThat(actualCar).isEqualTo(expectedCar);
		Assertions.assertThat(actualMoto).isEqualTo(expectedMoto);
		Assertions.assertThat(actualBus).isEqualTo(expectedBus);
	}

	public void test_get_null_type() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type expected = null;
		Type actual = cache.getType(null);
		Assertions.assertThat(actual).isEqualTo(expected);
	}

	public void test_get_type_with_null_value() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type expected = cache.newType(null);
		Type actual = cache.getType(null);
		Assertions.assertThat(actual).isEqualTo(expected);
	}

	// getSubType() tests

	public void test_get_existing_subtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type expected = car.newSubType(cache, "Audi");

		Generic actual = car.getSubType(cache, "Audi");
		Assertions.assertThat(actual).isEqualTo(expected);
	}

	public void test_get_non_existing_subtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type expected = null;

		Generic actual = car.getSubType(cache, "Audi");
		Assertions.assertThat(actual).isEqualTo(expected);
	}

	public void test_get_multiple_existing_subtypes() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type expectedAudi = car.newSubType(cache, "Audi");
		Type expectedMercedes = car.newSubType(cache, "Mercedes");

		Generic actualAudi = car.getSubType(cache, "Audi");
		Generic actualMercedes = car.getSubType(cache, "Mercedes");

		Assertions.assertThat(actualAudi).isEqualTo(expectedAudi);
		Assertions.assertThat(actualMercedes).isEqualTo(expectedMercedes);
	}

	public void test_get_null_subtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");

		Type expected = null;
		Generic actual = car.getSubType(cache, "Audi");
		Assertions.assertThat(actual).isEqualTo(expected);
	}

	public void test_get_subtype_with_null_value() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		// car.enableNotNullConstraint(cache);
		Type expected = car.newSubType(cache, null);
		Generic actual = car.getSubType(cache, null);

		Assertions.assertThat(actual).isEqualTo(expected);
	}
}
