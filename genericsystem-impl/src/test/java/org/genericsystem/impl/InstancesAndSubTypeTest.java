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

	public void testInstancesSnapshotOfType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Generic myVehicle1 = vehicle.newInstance( "myVehicle1");
		Generic myVehicle2 = vehicle.newInstance( "myVehicle2");
		Generic myVehicle3 = vehicle.newInstance( "myVehicle3");
		Snapshot<Generic> instances = vehicle.getInstances();
		assert instances.size() == 3;
		assert instances.contains(myVehicle1);
		assert instances.contains(myVehicle2);
		assert instances.contains(myVehicle3);
		cache.flush();
	}

	public void testAllInstancesSnapshotOfType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Generic myVehicle1 = vehicle.newInstance( "myVehicle1");
		Generic myVehicle2 = vehicle.newInstance( "myVehicle2");
		Generic myVehicle3 = vehicle.newInstance( "myVehicle3");
		Snapshot<Generic> snapshot = vehicle.getAllInstances();
		assert snapshot.size() == 3 : snapshot;
		assert snapshot.contains(myVehicle1);
		assert snapshot.contains(myVehicle2);
		assert snapshot.contains(myVehicle3);
	}

	public void testInstancesSnapshotOfTypeWithSubtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.newSubType( "Car");
		car.newInstance( "myCar1");
		Generic myVehicle1 = vehicle.newInstance( "myVehicle1");
		Generic myVehicle2 = vehicle.newInstance( "myVehicle2");
		Generic myVehicle3 = vehicle.newInstance( "myVehicle3");
		Snapshot<Generic> snapshot = vehicle.getInstances();
		assert snapshot.size() == 3 : snapshot;
		assert snapshot.contains(myVehicle1);
		assert snapshot.contains(myVehicle2);
		assert snapshot.contains(myVehicle3);
	}

	public void testAllInstancesSnapshotOfTypeWithSubtype() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.newSubType( "Car");
		Generic myCar1 = car.newInstance( "myCar1");
		Generic myVehicle1 = vehicle.newInstance( "myVehicle1");
		Generic myVehicle2 = vehicle.newInstance( "myVehicle2");
		Generic myVehicle3 = vehicle.newInstance( "myVehicle3");
		Snapshot<Generic> snapshot = vehicle.getAllInstances();
		assert snapshot.size() == 4 : snapshot;
		assert snapshot.contains(myVehicle1);
		assert snapshot.contains(myVehicle2);
		assert snapshot.contains(myVehicle3);
		assert snapshot.contains(myCar1);
		snapshot = car.getAllInstances();
		assert snapshot.size() == 1 : snapshot;
		assert snapshot.contains(myCar1);
	}

	public void testInstancesSnapshotOfAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute( "vehiclePower");
		Generic myVehicle = vehicle.newInstance( "myVehicle");
		Holder valueOfAttribute = myVehicle.setValue( vehiclePower, "233 HP");
		Snapshot<Generic> snapshot = vehiclePower.getInstances();
		assert snapshot.size() == 1 : valueOfAttribute.info();
		assert snapshot.contains(valueOfAttribute);
		valueOfAttribute.remove();
		assert snapshot.size() == 0;
	}

	public void testAllInstancesSnapshotOfAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute( "power");
		Generic myVehicle = vehicle.newInstance( "myVehicle");
		Holder valueOfAttribute = myVehicle.setValue( vehiclePower, "123");
		Snapshot<Generic> snapshot = vehiclePower.getAllInstances();
		assert snapshot.size() == 1 : snapshot;
		assert snapshot.contains(valueOfAttribute);
	}

	public void testInstancesSnapshotOfRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type color = cache.addType("Color");
		Relation relation = color.setRelation( "relation", car);
		Generic bmw = car.newInstance( "bmw");
		Generic red = color.newInstance( "red");
		Link link2 = red.setLink( relation, "link2", bmw);
		assert relation.getInstances().size() == 1 : relation.getInstances();
		assert relation.getInstances().contains(link2);
	}

	public void testInstancesSnapshotOfRelationTernary() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type human = cache.addType("Human");
		Type time = cache.addType("Time");
		Relation drive = human.setRelation( "drive", vehicle, time);
		Generic myck = human.newInstance( "myck");
		Generic myBmw = vehicle.newInstance( "myBmw");
		Generic myTime = time.newInstance( "myTime");
		Link link = myck.setLink( drive, "theDrive", myBmw, myTime);
		assert drive.getInstances().size() == 1 : drive.getInstances();
		assert drive.getInstances().contains(link);
	}

	public void testAllInstancesSnapshotOfRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type human = cache.addType("Human");
		Relation driver = human.setRelation( "drive", vehicle);
		Generic myBmw = vehicle.newInstance( "myBmw");
		Generic myck = human.newInstance( "myck");
		Link link = myck.setLink( driver, "theDrive", myBmw);
		assert driver.getAllInstances().size() == 1 : driver.getAllInstances();
		assert driver.getAllInstances().contains(link);
	}

	public void testOneSubType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.newSubType( "Car");
		Snapshot<Generic> snapshot = vehicle.getDirectSubTypes();
		assert snapshot.size() == 1;
		assert snapshot.contains(car);
	}

	public void testManySubType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.newSubType( "Car");
		car.newSubType( "ElectricCar");
		Type truck = vehicle.newSubType( "Truck");
		Snapshot<Generic> snapshot = vehicle.getDirectSubTypes();
		assert snapshot.size() == 2;
		assert snapshot.contains(car) : snapshot;
		assert snapshot.contains(truck);
	}

	public void testManyAllSubType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.newSubType( "Car");
		Type electricCar = car.newSubType( "ElectricCar");
		Type truck = vehicle.newSubType( "Truck");
		Snapshot<Generic> snapshot = vehicle.getSubTypes();
		assert snapshot.size() == 3 : snapshot;
		assert snapshot.contains(car);
		assert snapshot.contains(electricCar);
		assert snapshot.contains(truck);
	}

	public void testSubTypeWithInstance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		vehicle.newInstance( "myVehicle");
		Type car = vehicle.newSubType( "Car");
		car.newInstance( "myCar");
		Type truck = vehicle.newSubType( "Truck");
		Snapshot<Generic> snapshot = vehicle.getDirectSubTypes();
		assert snapshot.size() == 2 : snapshot;
		assert snapshot.contains(car);
		assert snapshot.contains(truck);
	}

	public void testAllSubTypeWithInstance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		vehicle.newInstance( "myVehicle");
		Type car = vehicle.newSubType( "Car");
		Type electricCar = car.newSubType( "ElectricCar");
		car.newInstance( "myCar");
		Type truck = vehicle.newSubType( "Truck");
		Snapshot<Generic> snapshot = vehicle.getSubTypes();
		assert snapshot.size() == 3 : snapshot;
		assert snapshot.contains(car) : snapshot;
		assert snapshot.contains(electricCar);
		assert snapshot.contains(truck);
	}

	public void testSubTypeWithAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		vehicle.setAttribute( "power");
		Type car = vehicle.newSubType( "Car");
		car.newInstance( "myCar");
		Type truck = vehicle.newSubType( "Truck");
		Snapshot<Generic> snapshot = vehicle.getDirectSubTypes();
		assert snapshot.size() == 2 : snapshot;
		assert snapshot.contains(car);
		assert snapshot.contains(truck);
	}

	public void testAllSubTypeWithAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		vehicle.setAttribute( "power");
		Type car = vehicle.newSubType( "Car");
		Type electricCar = car.newSubType( "ElectricCar");
		car.setAttribute( "numberWheel");
		car.newInstance( "myCar");
		Type truck = vehicle.newSubType( "Truck");
		Snapshot<Generic> snapshot = vehicle.getSubTypes();
		assert snapshot.size() == 3 : snapshot;
		assert snapshot.contains(car);
		assert snapshot.contains(electricCar);
		assert snapshot.contains(truck);
	}

	public void testSubTypeWithRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type human = cache.addType("Human");
		human.setRelation( "drive", vehicle);
		Type car = vehicle.newSubType( "Car");
		car.newInstance( "myCar");
		Type truck = vehicle.newSubType( "Truck");
		Snapshot<Generic> snapshot = vehicle.getDirectSubTypes();
		assert snapshot.size() == 2 : snapshot;
		assert snapshot.contains(car);
		assert snapshot.contains(truck);
	}

	public void testAllSubTypeWithRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type human = cache.addType("Human");
		human.setRelation( "drive", vehicle);
		Type car = vehicle.newSubType( "Car");
		Type electricCar = car.newSubType( "ElectricCar");
		car.newInstance( "myCar");
		Type truck = vehicle.newSubType( "Truck");
		Snapshot<Generic> snapshot = vehicle.getSubTypes();

		assert snapshot.size() == 3 : snapshot;
		assert snapshot.contains(car);
		assert snapshot.contains(electricCar);
		assert snapshot.contains(truck);
	}

}
