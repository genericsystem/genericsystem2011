package org.genericsystem.cdi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Objects;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Property;
import org.genericsystem.api.generic.Type;
import org.genericsystem.api.generic.Value;
import org.testng.annotations.Test;

@Test
public class SerializationTest extends AbstractTest {

	public void serializeDeserializeCacheContext() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		addGenerics(cache);

		ByteArrayOutputStream o = new ByteArrayOutputStream();
		ObjectOutputStream out;
		try {
			log.info("B");
			out = new ObjectOutputStream(o);
			log.info("C");
			out.writeObject(cache);
			log.info("D");
			out.close();

			// cache.deactivate();
			cache = null;

			InputStream i = new ByteArrayInputStream(o.toByteArray());
			ObjectInputStream in = new ObjectInputStream(i);
			cache = (Cache) in.readObject();

			// cache.activate();

		} catch (IOException | ClassNotFoundException e) {
			assert false : e;
		}

		Cache cache2 = GenericSystem.newCacheOnANewInMemoryEngine();
		addGenerics(cache2);

		equalsNode(cache2, cache, cache2.getEngine(), cache.getEngine());
		equalsNode(cache, cache2, cache.getEngine(), cache2.getEngine());

		cache.flush();
		cache2.flush();

		equalsNode(cache2, cache, cache2.getEngine(), cache.getEngine());
		equalsNode(cache, cache2, cache.getEngine(), cache2.getEngine());
	}

	private static void addGenerics(Cache cache) {
		Type man = cache.newType("Man");

		Type graphicComponent = cache.newType("graphicComponent");
		Type window = graphicComponent.newSubType(cache, "Window");

		Property height = window.addProperty(cache, "Height");
		Generic myWindow = window.newInstance(cache, "MyWindow");
		Value myHeight1 = ((Property) myWindow).setValue(cache, height, 165);

		cache.flush();

		man.addAttribute(cache, "Wheels");

		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Attribute vehiclePower = vehicle.addAttribute(cache, "power");

		Generic myBmw = vehicle.newInstance(cache, "myBmw");
		Generic bmw = vehicle.newInstance(cache, "Bmw");
		Generic myMercedes = car.newInstance(cache, "myMercedes");
		bmw.addValue(cache, vehiclePower, "213");
		myBmw.addValue(cache, vehiclePower, "512");
		myMercedes.addValue(cache, vehiclePower, "1024");
		myMercedes.addValue(cache, vehiclePower, 33);

		Type selectable = graphicComponent.newSubType(cache, "Selectable");
		cache.newSubType("selectableWindow", selectable, window);
		graphicComponent.addAttribute(cache, "size");
		selectable.addAttribute(cache, "Selected");

		myHeight1.remove(cache);
		myWindow.remove(cache);
	}

	private static void equalsNode(Cache cache1, Cache cache2, Generic g1, Generic g2) {
		equals(cache1, cache2, g1.getInheritings(cache1), g2.getInheritings(cache2));
		equals(cache1, cache2, g1.getComposites(cache1), g2.getComposites(cache2));
	}

	private static void equals(Cache cache1, Cache cache2, Snapshot<Generic> first, Snapshot<Generic> second) {
		Iterator<Generic> itFirst = first.iterator();
		Iterator<Generic> itSecond = second.iterator();
		while (itFirst.hasNext())
			equals(cache1, cache2, itFirst.next(), itSecond.next());
	}

	private static void equals(Cache cache1, Cache cache2, Generic first, Generic second) {
		assert Objects.equals(first.getValue(), second.getValue());
		equalsNode(cache1, cache2, first, second);
	}
}
