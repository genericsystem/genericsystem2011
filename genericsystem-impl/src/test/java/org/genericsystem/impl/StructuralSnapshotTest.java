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
		Attribute power = vehicle.addAttribute(cache,"Power");
		assert vehicle.getStructurals(cache).contains(power);
	}

	public void testStructuralSnapshotWithRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation vehicleHuman = vehicle.addRelation(cache,"pilot", human);
		assert vehicle.getStructurals(cache).contains(vehicleHuman);
	}

	public void testStructuralSnapshotWithSubType2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache,"Car");
		Attribute carPower = car.addAttribute(cache,"Power");
		Type human = cache.newType("Human");
		human.newSubType(cache,"Pilot");
		Relation vehicleHuman = vehicle.addRelation(cache,"Drive", human);
		assert car.getStructurals(cache).contains(vehicleHuman);
		assert car.getStructurals(cache).contains(carPower);
	}

	public void testStructuralSnapshotWithAttributeAndRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.addAttribute(cache,"Power");
		Type human = cache.newType("Human");
		Relation vehicleHuman = vehicle.addRelation(cache,"pilot", human);
		assert vehicle.getStructurals(cache).contains(vehicleHuman);
		assert vehicle.getStructurals(cache).contains(vehiclePower);
	}

}
