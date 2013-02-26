package org.genericsystem.impl;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Type;
import org.testng.annotations.Test;

@Test
public class StructuralSnapshotTest extends AbstractTest {

	public void testStructuralSnapshotWithAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute power = vehicle.setAttribute(cache, "Power");
		assert vehicle.getAttributes(cache).contains(power);
	}

	public void testStructuralSnapshotWithRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation vehicleHuman = vehicle.setRelation(cache, "pilot", human);
		assert vehicle.getAttributes(cache).contains(vehicleHuman);
	}

	public void testStructuralSnapshotWithSubType2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Attribute carPower = car.setAttribute(cache, "Power");
		Type human = cache.newType("Human");
		human.newSubType(cache, "Pilot");
		Relation vehicleHuman = vehicle.setRelation(cache, "Drive", human);
		assert car.getAttributes(cache).contains(vehicleHuman);
		assert car.getAttributes(cache).contains(carPower);
	}

	public void testStructuralSnapshotWithAttributeAndRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute(cache, "Power");
		Type human = cache.newType("Human");
		Relation vehicleHuman = vehicle.setRelation(cache, "pilot", human);
		assert vehicle.getAttributes(cache).contains(vehicleHuman);
		assert vehicle.getAttributes(cache).contains(vehiclePower);
	}

}
