package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class AddComponentSuperTest extends AbstractTest {

	public void addComponentOnType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type power = cache.newType("power");
		assert vehicle.getAttribute("power") == null;
		power.addComponent(0, vehicle);
		assert vehicle.getAttribute("power") != null;
		assert vehicle.getAttribute("power").inheritsFrom(cache.getMetaAttribute());
		assert vehicle.getAttribute("power").getBaseComponent().equals(vehicle);
	}

	public void addComponentOnArribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Type vehicleColor = vehicle.addAttribute("VehicleColor");
		assert vehicle.getRelation("VehicleColor") == null;
		vehicleColor = vehicleColor.addComponent(1, color);
		assert vehicle.getRelation("VehicleColor") != null;
		assert vehicle.getRelation("VehicleColor").getComponent(0).equals(vehicle);
		assert vehicle.getRelation("VehicleColor").getComponent(1).equals(color);
		assert vehicle.getRelation("VehicleColor").inheritsFrom(cache.getMetaRelation());
		assert vehicleColor.getComponents().contains(vehicle) : vehicleColor.getComponents();
		assert vehicleColor.getComponents().contains(color) : vehicleColor.getComponents();
	}

	public void removeComponentOnAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Attribute power = vehicle.addAttribute("power");
		power.removeComponent(0, vehicle);
		assert vehicle.getAttribute("power") == null;
	}

	public void removeComponentOnRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Relation vehicleColor = vehicle.setRelation("VehicleColor", color);
		assert color.getAttribute("VehicleColor") != null;
		vehicleColor.removeComponent(1, color);
		assert color.getAttribute("VehicleColor") == null : color.getAttribute("VehicleColor");
		assert vehicle.getAttribute("VehicleColor") != null;
	}

	public void addSuperOnType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType("Car");
		Type robot = cache.newType("Robot");
		car = car.addSuper(robot);
		assert car.inheritsFrom(robot);
		assert car.inheritsFrom(vehicle);
	}

	public void removeSuperOnType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType("Car");
		Type robot = cache.newType("Robot");
		Type transformer = cache.newSubType("Transformer", robot, car);
		transformer = transformer.removeSuper(1);
		assert !transformer.inheritsFrom(robot);
		assert transformer.inheritsFrom(car);
		assert transformer.inheritsFrom(vehicle);
	}

	public void addRemoveComplexSuperOnType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType("Car");
		Type robot = cache.newType("Robot");
		Type transformer = cache.newSubType("Transformer", robot, car);
		Type clonable = cache.newType("Clonable");
		Type transformerClonable = cache.newSubType("TransformerClonable", transformer, clonable);
		transformerClonable = transformerClonable.removeSuper(2);
		assert transformerClonable.inheritsFrom(clonable);
		assert !transformerClonable.inheritsFrom(robot);
		assert !transformerClonable.inheritsFrom(car);
		assert !transformerClonable.inheritsFrom(vehicle);
	}

}
