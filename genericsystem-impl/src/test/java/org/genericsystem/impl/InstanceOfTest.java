package org.genericsystem.impl;

import java.util.Objects;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Holder;
import org.genericsystem.api.generic.Type;
import org.testng.annotations.Test;

@Test
public class InstanceOfTest extends AbstractTest {

	// public void testGetMetasAttributes() {
	// Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
	// Type vehicle = cache.newType("Vehicle");
	// Type car = vehicle.newSubType(cache, "Car");
	// Attribute vehiclePower = vehicle.addAttribute(cache, "Power");
	// Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
	// Generic myCar = car.newInstance(cache, "myCar");
	// Holder myVehiclePower236 = myVehicle.setValue(cache, vehiclePower, 236);
	// Attribute carSubPower = car.addSubAttribute(cache, vehiclePower, "subPower");
	//
	// Holder myCarPower235 = myCar.setValue(cache, carSubPower, 235);
	//
	// assert myVehiclePower236.getMeta().equals(vehiclePower) : myVehiclePower236.getMeta() + "  " + myVehiclePower236.getSupers();
	// assert myCarPower235.getMeta().equals(carSubPower) : myCarPower235.getMeta();
	//
	// assert ((GenericImpl) myVehicle).getMeta().equals(vehicle);
	// assert ((GenericImpl) myCar).getMeta().equals(car);
	// assert ((GenericImpl) car).getMeta().equals(cache.getEngine());
	// assert ((GenericImpl) vehicle).getMeta().equals(cache.getEngine());
	// }

	// public void testGetMetasRelations() {
	// Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
	// Type vehicle = cache.newType("Vehicle");
	// Type car = vehicle.newSubType(cache, "Car");
	// Type human = cache.newType("Human");
	// Type pilot = human.newSubType(cache, "Pilot");
	// Relation driving = human.addRelation(cache, "Driving", vehicle);
	// Relation piloting = pilot.addSubRelation(cache, driving, "Piloting", car);
	//
	// Generic maurice = human.newInstance(cache, "Maurice");
	// Generic federer = pilot.newInstance(cache, "Federer");
	//
	// Generic titine = vehicle.newInstance(cache, "myTitine");
	// Generic f1 = car.newInstance(cache, "myF1");
	//
	// Link drives = maurice.setLink(cache, driving, "Maurice_drives_Titine", titine);
	// Link pilots = federer.setLink(cache, piloting, "Federer_pilots_F1", f1);
	//
	// assert drives.getMeta().equals(driving) : drives.getMeta();
	//
	// assert pilots.getMeta().equals(piloting) : pilots.getMeta();
	// }

	public void testInstanceOf() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type vehicle = cache.newType("Vehicle");
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		assert myVehicle.isInstanceOf(vehicle);

		Type car = vehicle.newSubType(cache, "Car");
		Generic myCar = car.newInstance(cache, "myCar");
		assert myCar.isInstanceOf(car);

		assert myCar.isInstanceOf(vehicle);
		assert !myVehicle.isInstanceOf(car);

		Attribute vehiclePower = vehicle.setProperty(cache, "Power");

		Holder v90 = myVehicle.setValue(cache, vehiclePower, 90);
		Holder v235 = myCar.setValue(cache, vehiclePower, 235);

		assert v90.isInstanceOf(vehiclePower);
		assert v235.isInstanceOf(vehiclePower);

		cache.flush();

		assert Objects.equals(myVehicle.getValue(cache, vehiclePower), 90);
		assert Objects.equals(myCar.getValue(cache, vehiclePower), 235);

		assert v90.equals(myVehicle.getHolder(cache, vehiclePower));
		assert v235.equals(myCar.getHolder(cache, vehiclePower));

		v90.remove(cache);

		assert myVehicle.getValue(cache, vehiclePower) == null;
		assert myVehicle.getHolder(cache, vehiclePower) == null;

		cache.flush();
	}

	public void testMutipleInstanceOf() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type animal = cache.newType("Animal");
		Type mammal = cache.newType("Mammal");

		Type human = cache.newSubType("Human", animal, mammal);
		Generic michael = human.newInstance(cache, "Michael");

		Type canid = cache.newSubType("Canid", animal, mammal);
		Generic milou = canid.newInstance(cache, "Milou");
		assert michael.isInstanceOf(human);
		assert !michael.isInstanceOf(canid);
		assert michael.isInstanceOf(animal);
		assert michael.isInstanceOf(mammal);

		assert milou.isInstanceOf(canid);
		assert !milou.isInstanceOf(human);
		assert milou.isInstanceOf(animal);
		assert milou.isInstanceOf(mammal);
	}

	public void testNewSubTypeWithJustOneSuper() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car1 = cache.newSubType("Car1", vehicle);
		Type car2 = vehicle.newSubType(cache, "Car2");

		assert car1.getSupers().first().equals(car2.getSupers().first());
	}

	public void testNewSubTypeWithoutSuper() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car1 = cache.newSubType("Car1");
		assert car1.getSupers().first().equals(cache.getEngine());
	}
}
