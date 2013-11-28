package org.genericsystem.cdi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.genericsystem.core.Cache;
import org.genericsystem.core.CacheImpl;
import org.genericsystem.core.Generic;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class SerializationTest extends AbstractTest {

	public void testAdds() {
		cache.clear();
		Type vehicle = cache.addType("Vehicle");
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();) {
			new ObjectOutputStream(outputStream).writeObject(cache);// writeExternal

			cache = cache.getEngine().newCache().start();
			cache = (Cache) new ObjectInputStream(new ByteArrayInputStream(outputStream.toByteArray())).readObject();// readExternal
			assert cache.getType("Vehicle") != vehicle;
			assert cache.getType("Vehicle").getValue().equals(vehicle.getValue());
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void testSuperCache() {
		cache.clear();
		Type vehicle = cache.addType("Vehicle");
		Cache superCache = cache.mountNewCache();
		Attribute vehiclePower = vehicle.addAttribute("power");
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();) {
			new ObjectOutputStream(outputStream).writeObject(superCache);// writeExternal

			cache = cache.getEngine().newCache().start();
			cache = (Cache) new ObjectInputStream(new ByteArrayInputStream(outputStream.toByteArray())).readObject();// readExternal
			Type vehicle2 = cache.getType("Vehicle");
			assert vehicle2 != vehicle;
			assert vehicle2.getValue().equals(vehicle.getValue());
			assert vehicle2.getAttribute("power") != null : vehicle2.getAttribute("power");
			assert vehicle2.getAttribute("power") != vehiclePower;
			assert ((CacheImpl) cache).getSubContext() instanceof Cache;
		} catch (IOException | ClassNotFoundException e) {
			assert false : e.getMessage();
		}
	}

	public void testAutomatics() {
		cache.clear();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.addAttribute("power");
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();) {
			new ObjectOutputStream(outputStream).writeObject(cache);// writeExternal

			cache = cache.getEngine().newCache().start();
			cache = (Cache) new ObjectInputStream(new ByteArrayInputStream(outputStream.toByteArray())).readObject();// readExternal
			Type vehicle2 = cache.getType("Vehicle");
			assert vehicle2 != vehicle;
			assert vehicle2.getValue().equals(vehicle.getValue());
			assert vehicle2.getAttribute("power") != null;
			assert vehicle2.getAttribute("power") != vehiclePower;
		} catch (IOException | ClassNotFoundException e) {
			assert false : e.getMessage();
		}
	}

	public void testRemoves() {
		cache.clear();
		Type vehicle = cache.addType("Vehicle");
		Generic myVehicle = vehicle.newInstance("myVehicle");
		Cache superCache = cache.mountNewCache();
		myVehicle.remove();
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();) {
			new ObjectOutputStream(outputStream).writeObject(superCache);// writeExternal

			cache = cache.getEngine().newCache().start();
			cache = (Cache) new ObjectInputStream(new ByteArrayInputStream(outputStream.toByteArray())).readObject();// readExternal
			Type myVehicle2 = cache.getType("Vehicle");
			assert myVehicle2 != vehicle;
			assert myVehicle2.getValue().equals(vehicle.getValue());
			assert ((CacheImpl) cache).getSubContext() instanceof Cache;
			assert !myVehicle2.getInstance("myVehicle").isAlive();
		} catch (IOException | ClassNotFoundException e) {
			assert false : e.getMessage();
		}
	}
}
