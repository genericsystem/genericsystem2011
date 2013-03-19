package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Snapshot;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class InstancesAndSubTypeTest extends AbstractTest {

	public void testSubTypes() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car1 = cache.newSubType("Car1", vehicle);
		Type car2 = vehicle.newSubType(cache, "Car2");
		car1.log();
		car2.log();
	}

	public void testInstancesSnapshotOfType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Generic myVehicle1 = vehicle.newInstance(cache, "myVehicle1");
		Generic myVehicle2 = vehicle.newInstance(cache, "myVehicle2");
		Generic myVehicle3 = vehicle.newInstance(cache, "myVehicle3");
		Snapshot<Generic> instances = vehicle.getInstances(cache);
		assert instances.size() == 3;
		assert instances.contains(myVehicle1);
		assert instances.contains(myVehicle2);
		assert instances.contains(myVehicle3);
		cache.flush();
	}

	public void testAllInstancesSnapshotOfType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Generic myVehicle1 = vehicle.newInstance(cache, "myVehicle1");
		Generic myVehicle2 = vehicle.newInstance(cache, "myVehicle2");
		Generic myVehicle3 = vehicle.newInstance(cache, "myVehicle3");
		Snapshot<Generic> snapshot = vehicle.getAllInstances(cache);
		assert snapshot.size() == 3 : snapshot;
		assert snapshot.contains(myVehicle1);
		assert snapshot.contains(myVehicle2);
		assert snapshot.contains(myVehicle3);
	}

	public void testInstancesSnapshotOfTypeWithSubtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		car.newInstance(cache, "myCar1");
		Generic myVehicle1 = vehicle.newInstance(cache, "myVehicle1");
		Generic myVehicle2 = vehicle.newInstance(cache, "myVehicle2");
		Generic myVehicle3 = vehicle.newInstance(cache, "myVehicle3");
		Snapshot<Generic> snapshot = vehicle.getInstances(cache);
		assert snapshot.size() == 3 : snapshot;
		assert snapshot.contains(myVehicle1);
		assert snapshot.contains(myVehicle2);
		assert snapshot.contains(myVehicle3);
	}

	public void testAllInstancesSnapshotOfTypeWithSubtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Generic myCar1 = car.newInstance(cache, "myCar1");
		Generic myVehicle1 = vehicle.newInstance(cache, "myVehicle1");
		Generic myVehicle2 = vehicle.newInstance(cache, "myVehicle2");
		Generic myVehicle3 = vehicle.newInstance(cache, "myVehicle3");
		Snapshot<Generic> snapshot = vehicle.getAllInstances(cache);
		assert snapshot.size() == 4 : snapshot;
		assert snapshot.contains(myVehicle1);
		assert snapshot.contains(myVehicle2);
		assert snapshot.contains(myVehicle3);
		assert snapshot.contains(myCar1);
		snapshot = car.getAllInstances(cache);
		assert snapshot.size() == 1 : snapshot;
		assert snapshot.contains(myCar1);
	}

	public void testInstancesSnapshotOfAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute(cache, "vehiclePower");
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		Holder valueOfAttribute = myVehicle.setValue(cache, vehiclePower, "233 HP");
		Snapshot<Generic> snapshot = vehiclePower.getInstances(cache);
		assert snapshot.size() == 1 : snapshot;
		assert snapshot.contains(valueOfAttribute);
	}

	public void testAllInstancesSnapshotOfAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute(cache, "power");
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		Holder valueOfAttribute = myVehicle.setValue(cache, vehiclePower, "123");
		Snapshot<Generic> snapshot = vehiclePower.getAllInstances(cache);
		assert snapshot.size() == 1 : snapshot;
		assert snapshot.contains(valueOfAttribute);
	}

	public void testInstancesSnapshotOfRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation relation = color.setRelation(cache, "relation", car);
		Generic bmw = car.newInstance(cache, "bmw");
		Generic red = color.newInstance(cache, "red");
		Link link2 = red.setLink(cache, relation, "link2", bmw);
		assert relation.getInstances(cache).size() == 1 : relation.getInstances(cache);
		assert relation.getInstances(cache).contains(link2);
	}

	public void testInstancesSnapshotOfRelationTernary() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Type time = cache.newType("Time");
		Relation drive = human.setRelation(cache, "drive", vehicle, time);
		Generic myck = human.newInstance(cache, "myck");
		Generic myBmw = vehicle.newInstance(cache, "myBmw");
		Generic myTime = time.newInstance(cache, "myTime");
		Link link = myck.setLink(cache, drive, "theDrive", myBmw, myTime);
		assert drive.getInstances(cache).size() == 1 : drive.getInstances(cache);
		assert drive.getInstances(cache).contains(link);
	}

	public void testAllInstancesSnapshotOfRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation driver = human.setRelation(cache, "drive", vehicle);
		Generic myBmw = vehicle.newInstance(cache, "myBmw");
		Generic myck = human.newInstance(cache, "myck");
		Link link = myck.setLink(cache, driver, "theDrive", myBmw);
		assert driver.getAllInstances(cache).size() == 1 : driver.getAllInstances(cache);
		assert driver.getAllInstances(cache).contains(link);
	}

	public void testOneSubType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Snapshot<Generic> snapshot = vehicle.getDirectSubTypes(cache);
		assert snapshot.size() == 1;
		assert snapshot.contains(car);
	}

	public void testManySubType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		car.newSubType(cache, "ElectricCar");
		Type truck = vehicle.newSubType(cache, "Truck");
		Snapshot<Generic> snapshot = vehicle.getDirectSubTypes(cache);
		assert snapshot.size() == 2;
		assert snapshot.contains(car) : snapshot;
		assert snapshot.contains(truck);
	}

	public void testManyAllSubType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Type electricCar = car.newSubType(cache, "ElectricCar");
		Type truck = vehicle.newSubType(cache, "Truck");
		Snapshot<Generic> snapshot = vehicle.getSubTypes(cache);
		assert snapshot.size() == 3 : snapshot;
		assert snapshot.contains(car);
		assert snapshot.contains(electricCar);
		assert snapshot.contains(truck);
	}

	public void testSubTypeWithInstance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		vehicle.newInstance(cache, "myVehicle");
		Type car = vehicle.newSubType(cache, "Car");
		car.newInstance(cache, "myCar");
		Type truck = vehicle.newSubType(cache, "Truck");
		Snapshot<Generic> snapshot = vehicle.getDirectSubTypes(cache);
		assert snapshot.size() == 2 : snapshot;
		assert snapshot.contains(car);
		assert snapshot.contains(truck);
	}

	public void testAllSubTypeWithInstance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		vehicle.newInstance(cache, "myVehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Type electricCar = car.newSubType(cache, "ElectricCar");
		car.newInstance(cache, "myCar");
		Type truck = vehicle.newSubType(cache, "Truck");
		Snapshot<Generic> snapshot = vehicle.getSubTypes(cache);
		assert snapshot.size() == 3 : snapshot;
		assert snapshot.contains(car) : snapshot;
		assert snapshot.contains(electricCar);
		assert snapshot.contains(truck);
	}

	public void testSubTypeWithAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		vehicle.setAttribute(cache, "power");
		Type car = vehicle.newSubType(cache, "Car");
		car.newInstance(cache, "myCar");
		Type truck = vehicle.newSubType(cache, "Truck");
		Snapshot<Generic> snapshot = vehicle.getDirectSubTypes(cache);
		assert snapshot.size() == 2 : snapshot;
		assert snapshot.contains(car);
		assert snapshot.contains(truck);
	}

	public void testAllSubTypeWithAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		vehicle.setAttribute(cache, "power");
		Type car = vehicle.newSubType(cache, "Car");
		Type electricCar = car.newSubType(cache, "ElectricCar");
		car.setAttribute(cache, "numberWheel");
		car.newInstance(cache, "myCar");
		Type truck = vehicle.newSubType(cache, "Truck");
		Snapshot<Generic> snapshot = vehicle.getSubTypes(cache);
		assert snapshot.size() == 3 : snapshot;
		assert snapshot.contains(car);
		assert snapshot.contains(electricCar);
		assert snapshot.contains(truck);
	}

	public void testSubTypeWithRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		human.setRelation(cache, "drive", vehicle);
		Type car = vehicle.newSubType(cache, "Car");
		car.newInstance(cache, "myCar");
		Type truck = vehicle.newSubType(cache, "Truck");
		Snapshot<Generic> snapshot = vehicle.getDirectSubTypes(cache);
		assert snapshot.size() == 2 : snapshot;
		assert snapshot.contains(car);
		assert snapshot.contains(truck);
	}

	public void testAllSubTypeWithRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		human.setRelation(cache, "drive", vehicle);
		Type car = vehicle.newSubType(cache, "Car");
		Type electricCar = car.newSubType(cache, "ElectricCar");
		car.newInstance(cache, "myCar");
		Type truck = vehicle.newSubType(cache, "Truck");
		Snapshot<Generic> snapshot = vehicle.getSubTypes(cache);

		assert snapshot.size() == 3 : snapshot;
		assert snapshot.contains(car);
		assert snapshot.contains(electricCar);
		assert snapshot.contains(truck);
	}

}
