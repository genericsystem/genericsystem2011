package org.genericsystem.impl;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Type;
import org.testng.annotations.Test;

@Test
public class PhamtomLinkTest extends AbstractTest {

	public void testPhantomAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");

		Attribute vehiclePower = vehicle.addAttribute(cache, "power");
		assert car.getAttributes(cache).contains(vehiclePower);

		car.cancel(cache, vehiclePower);
		assert !car.getAttributes(cache).contains(vehiclePower);

		car.restore(cache,vehiclePower);
		assert car.getAttributes(cache).contains(vehiclePower);
	}

	public void testPhantomSimpleRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Type color = cache.newType("Color");
		Relation vehicleHuman = vehicle.addRelation(cache, "VehicleHuman", human);
		Relation vehicleColor = vehicle.addRelation(cache, "vehicleColor", color);
		Type car = vehicle.newSubType(cache, "Car");

		assert vehicle.getRelations(cache).size() == 2;
		assert car.getRelations(cache).size() == 2;

		car.cancel(cache, vehicleHuman);
		assert vehicle.getRelations(cache).size() == 2;
		assert car.getRelations(cache).size() == 1;
		assert car.getRelations(cache).contains(vehicleColor);

		car.restore(cache,vehicleHuman);
		assert vehicle.getRelations(cache).size() == 2;
		assert car.getRelations(cache).size() == 2;
		assert car.getRelations(cache).get(0).equals(vehicleHuman);
		assert car.getRelations(cache).get(1).equals(vehicleColor);
	}

	public void testPhantomHierarchyRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation vehicleHuman = vehicle.addRelation(cache, "VehicleHuman", human);
		Type car = vehicle.newSubType(cache, "Car");
		Type convertible = car.newSubType(cache, "Convertible");

		assert vehicle.getRelations(cache).contains(vehicleHuman);
		assert car.getRelations(cache).contains(vehicleHuman);
		assert convertible.getRelations(cache).contains(vehicleHuman);

		car.cancel(cache, vehicleHuman);
		assert vehicle.getRelations(cache).contains(vehicleHuman);
		assert !car.getRelations(cache).contains(vehicleHuman);
		assert !convertible.getRelations(cache).contains(vehicleHuman);

		car.restore(cache,vehicleHuman);
		assert vehicle.getRelations(cache).contains(vehicleHuman);
		assert car.getRelations(cache).contains(vehicleHuman);
		assert convertible.getRelations(cache).contains(vehicleHuman);

		convertible.cancel(cache, vehicleHuman);
		assert vehicle.getRelations(cache).contains(vehicleHuman);
		assert car.getRelations(cache).contains(vehicleHuman);
		assert !convertible.getRelations(cache).contains(vehicleHuman);
	}

	public void testPhantomMultiHierachyRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Type car = vehicle.newSubType(cache, "Car");
		Type breakVehicle = car.newSubType(cache, "BreakVehicle");
		Type convertible = car.newSubType(cache, "Convertible");
		Relation vehicleColor = vehicle.addRelation(cache, "VehicleColor", color);

		car.cancel(cache, vehicleColor);
		assert vehicle.getRelations(cache).size() == 1;
		assert car.getRelations(cache).size() == 0;
		assert breakVehicle.getRelations(cache).size() == 0;
		assert convertible.getRelations(cache).size() == 0;

		car.restore(cache,vehicleColor);
		assert vehicle.getRelations(cache).size() == 1;
		assert car.getRelations(cache).get(0).equals(vehicleColor);
		assert breakVehicle.getRelations(cache).get(0).equals(vehicleColor);
		assert convertible.getRelations(cache).get(0).equals(vehicleColor);
	}

	public void testRelationsWIthSameName() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Type color = cache.newType("Color");
		Type car = vehicle.newSubType(cache, "Car");

		Relation vehicleHuman = vehicle.addRelation(cache, "VehicleHuman", human);
		Relation vehicleColor = vehicle.addRelation(cache, "VehicleHuman", color);
		assert vehicleHuman.getImplicit().equals(vehicleColor.getImplicit());
		car.cancel(cache, vehicleColor);
		assert car.getRelations(cache).size() == 1;
		assert car.getRelations(cache).get(0).getTargetComponent().equals(human);

		car.cancel(cache, vehicleHuman);
		car.restore(cache,vehicleHuman);
		assert car.getRelations(cache).size() == 1;
		assert car.getRelations(cache).get(0).getTargetComponent().equals(human);
	}

}
