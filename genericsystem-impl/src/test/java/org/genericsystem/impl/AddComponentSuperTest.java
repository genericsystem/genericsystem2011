package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class AddComponentSuperTest extends AbstractTest {

	public void addComponentOnType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type power = cache.newType("power");
		assert vehicle.getAttribute(cache, "power") == null;
		power.addComponent(cache, 0, vehicle);
		assert vehicle.getAttribute(cache, "power") != null;
		assert vehicle.getAttribute(cache, "power").inheritsFrom(cache.getMetaAttribute());
		assert vehicle.getAttribute(cache, "power").getBaseComponent().equals(vehicle);
	}

	public void addComponentOnArribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Type vehicleColor = vehicle.addAttribute(cache, "VehicleColor");
		assert vehicle.getRelation(cache, "VehicleColor") == null;
		vehicleColor = vehicleColor.addComponent(cache, 1, color);
		assert vehicle.getRelation(cache, "VehicleColor") != null;
		assert vehicle.getRelation(cache, "VehicleColor").getComponent(0).equals(vehicle);
		assert vehicle.getRelation(cache, "VehicleColor").getComponent(1).equals(color);
		assert vehicle.getRelation(cache, "VehicleColor").inheritsFrom(cache.getMetaRelation());
		assert vehicleColor.getComponents().contains(vehicle) : vehicleColor.getComponents();
		assert vehicleColor.getComponents().contains(color) : vehicleColor.getComponents();
	}

	public void removeComponentOnType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Attribute power = vehicle.addAttribute(cache, "power");
		power.removeComponent(cache, 0, vehicle);
		assert vehicle.getAttribute(cache, "power") == null;
	}

	public void addAndRemoveComponentOnType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type power = cache.newType("power");
		assert vehicle.getAttribute(cache, "power") == null;
		power = power.addComponent(cache, 0, vehicle);
		assert vehicle.getAttribute(cache, "power") != null;
		assert vehicle.getAttribute(cache, "power").inheritsFrom(cache.getMetaAttribute());
		assert vehicle.getAttribute(cache, "power").getBaseComponent().equals(vehicle);
		power.removeComponent(cache, 0, vehicle);
		assert vehicle.getAttribute(cache, "power") == null;
	}

	public void addAndRemoveComponentOnArribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Type vehicleColor = vehicle.addAttribute(cache, "VehicleColor");
		assert vehicle.getRelation(cache, "VehicleColor") == null;
		vehicleColor = vehicleColor.addComponent(cache, 1, color);
		assert vehicle.getRelation(cache, "VehicleColor") != null;
		assert vehicle.getRelation(cache, "VehicleColor").getComponent(0).equals(vehicle);
		assert vehicle.getRelation(cache, "VehicleColor").getComponent(1).equals(color);
		assert vehicle.getRelation(cache, "VehicleColor").inheritsFrom(cache.getMetaRelation());
		assert vehicleColor.getComponents().contains(vehicle) : vehicleColor.getComponents();
		assert vehicleColor.getComponents().contains(color) : vehicleColor.getComponents();
	}
}
