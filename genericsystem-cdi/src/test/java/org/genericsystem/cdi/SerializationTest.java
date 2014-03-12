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
		cache.start();
		cache.clear();
		Type vehicle = cache.addType("Vehicle");
		Cache clone = deSerialize(serialize(cache)).start();
		assert clone.getGeneric("Vehicle", cache.getEngine()) != vehicle;
		assert clone.getGeneric("Vehicle", cache.getEngine()).getValue().equals(vehicle.getValue());
	}

	public void testAddsWithFlush() {
		cache.start();
		cache.clear();
		Type color = cache.addType("Color");
		cache.flush();
		Type vehicle = cache.addType("Vehicle");
		Cache clone = deSerialize(serialize(cache)).start();
		assert clone.getGeneric("Vehicle", cache.getEngine()) != vehicle;
		assert clone.getGeneric("Vehicle", cache.getEngine()).getValue().equals(vehicle.getValue());
		assert clone.getGeneric("Color", cache.getEngine()) == color : clone.getGeneric("Color", cache.getEngine());
	}

	public void testSuperCache() {
		cache.start();
		cache.clear();
		Type vehicle = cache.addType("Vehicle");
		Cache superCache = cache.mountNewCache();
		Attribute vehiclePower = vehicle.addAttribute("power");
		Cache superCacheClone = deSerialize(serialize(superCache)).start();
		Type vehicle2 = superCacheClone.getGeneric("Vehicle", cache.getEngine());
		assert vehicle2 != vehicle;
		assert vehicle2.getValue().equals(vehicle.getValue());
		assert vehicle2.getAttribute("power") != null : vehicle2.getAttribute("power");
		assert vehicle2.getAttribute("power") != vehiclePower;
		assert ((CacheImpl) superCacheClone).getSubContext() instanceof Cache;
	}

	public void testAutomatics() {
		cache.start();
		cache.clear();
		Type vehicle = cache.addType("Vehicle");
		Attribute vehiclePower = vehicle.addAttribute("power");
		deSerialize(serialize(cache)).start();
		Type vehicle2 = cache.getGeneric("Vehicle", cache.getEngine());
		assert vehicle2 != vehicle;
		assert vehicle2.getValue().equals(vehicle.getValue());
		assert vehicle2.getAttribute("power") != null;
		assert vehicle2.getAttribute("power") != vehiclePower;
	}

	public void testRemoves() {
		cache.start();
		cache.clear();
		Type vehicle = cache.addType("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Cache superCache = cache.mountNewCache();
		myVehicle.remove();
		Cache superCacheClone = deSerialize(serialize(superCache)).start();
		Type myVehicle2 = superCacheClone.getGeneric("Vehicle", cache.getEngine());
		assert myVehicle2 != vehicle;
		assert myVehicle2.getValue().equals(vehicle.getValue());
		assert ((CacheImpl) superCacheClone).getSubContext() instanceof Cache;
		assert !myVehicle2.getInstance("myVehicle").isAlive();
	}

	private static byte[] serialize(Cache cache) {
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			new ObjectOutputStream(byteArrayOutputStream).writeObject(cache);// writeExternal
			return byteArrayOutputStream.toByteArray();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private static Cache deSerialize(byte[] bytes) {
		try {
			return (Cache) new ObjectInputStream(new ByteArrayInputStream(bytes)).readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

}
