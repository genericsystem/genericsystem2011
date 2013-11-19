package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class StructuralSnapshotTest extends AbstractTest {

	public void testStructuralSnapshotWithAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute power = vehicle.setAttribute( "Power");
		assert vehicle.getAttributes().contains(power);
	}

	public void testStructuralSnapshotWithRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type human = cache.addType("Human");
		Relation vehicleHuman = vehicle.setRelation( "pilot", human);
		assert vehicle.getAttributes().contains(vehicleHuman);
	}

	public void testStructuralSnapshotWithSubType2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.newSubType( "Car");
		Attribute carPower = car.setAttribute( "Power");
		Type human = cache.addType("Human");
		human.newSubType( "Pilot");
		Relation vehicleHuman = vehicle.setRelation( "Drive", human);
		assert car.getAttributes().contains(vehicleHuman);
		assert car.getAttributes().contains(carPower);
	}

	public void testStructuralSnapshotWithAttributeAndRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute( "Power");
		Type human = cache.addType("Human");
		Relation vehicleHuman = vehicle.setRelation( "pilot", human);
		assert vehicle.getAttributes().contains(vehicleHuman);
		assert vehicle.getAttributes().contains(vehiclePower);
	}

}
