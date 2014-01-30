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
		Type vehicle = cache.addType("Vehicle");
		Type power = cache.addType("power");
		assert vehicle.getAttribute("power") == null;
		power.addComponent(vehicle, 0);
		assert vehicle.getAttribute("power") != null;
		assert vehicle.getAttribute("power").inheritsFrom(cache.getMetaAttribute());
		assert vehicle.getAttribute("power").getBaseComponent().equals(vehicle);
	}

	public void addComponentOnArribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type color = cache.addType("Color");
		Type vehicleColor = vehicle.addAttribute("VehicleColor");
		vehicleColor = vehicleColor.addComponent(color, 1);
		assert vehicle.getRelation("VehicleColor").getComponent(0).equals(vehicle);
		assert vehicle.getRelation("VehicleColor").getComponent(1).equals(color);
		assert vehicle.getRelation("VehicleColor").inheritsFrom(cache.getMetaRelation());
		assert vehicleColor.getComponents().contains(vehicle) : vehicleColor.getComponents();
		assert vehicleColor.getComponents().contains(color) : vehicleColor.getComponents();
	}

	public void removeComponentOnAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Attribute power = vehicle.addAttribute("power");
		Type newPower = power.removeComponent(vehicle, 0);
		assert vehicle.getAttribute("power") == null;
		assert newPower.getMeta().equals(cache.getEngine());
	}

	public void removeComponentOnRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type color = cache.addType("Color");
		Relation vehicleColor = vehicle.setRelation("VehicleColor", color);
		assert color.getAttribute("VehicleColor") != null;
		vehicleColor.removeComponent(color, 1);
		assert color.getAttribute("VehicleColor") == null : color.getAttribute("VehicleColor");
		assert vehicle.getAttribute("VehicleColor") != null;
	}

	public void addSuperOnType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		Type robot = cache.addType("Robot");
		car = car.addSuper(robot);
		assert car.inheritsFrom(robot);
		assert car.inheritsFrom(vehicle);
	}

	public void removeSuperOnType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		Type robot = cache.addType("Robot");
		Type transformer = cache.addType("Transformer", robot, car);
		transformer = transformer.removeSuper(1);
		assert !transformer.inheritsFrom(robot);
		assert transformer.inheritsFrom(car);
		assert transformer.inheritsFrom(vehicle);
	}

	public void addRemoveComplexSuperOnType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Type car = vehicle.addSubType("Car");
		Type robot = cache.addType("Robot");
		Type transformer = cache.addType("Transformer", robot, car);
		Type clonable = cache.addType("Clonable");
		Type transformerClonable = cache.addType("TransformerClonable", transformer, clonable);
		transformerClonable = transformerClonable.removeSuper(0);
		assert transformerClonable.inheritsFrom(clonable);
		assert !transformerClonable.inheritsFrom(robot);
		assert !transformerClonable.inheritsFrom(car);
		assert !transformerClonable.inheritsFrom(vehicle);
	}

}
