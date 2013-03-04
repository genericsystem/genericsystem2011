package org.genericsystem.impl;

import java.io.File;
import java.util.Random;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Node;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Tree;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class PersistenceTest {

	private String directoryPath = System.getenv("HOME") + "/test/snapshot_save";

	private Cache initWorkingSpace() {
		String path = directoryPath + new Random().nextInt();
		cleanDirectory(path);
		return GenericSystem.newCacheOnANewPersistentEngine(path);
	}

	private void closingWorkingSpace(Cache cache) {
		cache.flush();
		Engine engine = cache.getEngine();
		engine.close();
		// GenericSystem.activateNewCache(engine);
		compareGraph(cache, engine, engine);// GenericSystem.newCacheOnANewInMemoryEngine(directoryPath + directoryNumber).getEngine());
	}

	public void testDefaultConfiguration() {
		closingWorkingSpace(initWorkingSpace());
	}

	public void testOnlyAType() {
		Cache cache = initWorkingSpace();
		cache.newType("Vehicle");
		closingWorkingSpace(cache);
	}

	public void testCustomTypeAndItsInstance() {
		Cache cache = initWorkingSpace();
		Type vehicle = cache.newType("Vehicle");
		Attribute equipment = vehicle.setAttribute(cache, "Equipment");
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		myVehicle.setValue(cache, equipment, "ABS");
		closingWorkingSpace(cache);
	}

	public void testAddAndRemove() {
		Cache cache = initWorkingSpace();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Type truck = vehicle.newSubType(cache, "Truck");
		truck.newSubType(cache, "Van");
		car.remove(cache);
		closingWorkingSpace(cache);
	}

	public void testLink() {
		Cache cache = initWorkingSpace();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Relation vehicleColor = vehicle.setRelation(cache, "VehicleColor", color);
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		Generic red = color.newInstance(cache, "red");
		myVehicle.setLink(cache, vehicleColor, "myVehicleRed", red);
		closingWorkingSpace(cache);
	}

	public void testHeritage() {
		Cache cache = initWorkingSpace();
		Type vehicle = cache.newType("Vehicle");
		vehicle.newSubType(cache, "Car");
		closingWorkingSpace(cache);
	}

	public void testHeritageMultiple() {
		Cache cache = initWorkingSpace();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		cache.newSubType("Transformer", vehicle, human);
		closingWorkingSpace(cache);
	}

	public void testHeritageMultipleDiamond() {
		Cache cache = initWorkingSpace();
		Type movable = cache.newType("Movable");
		Type vehicle = movable.newSubType(cache, "Vehicle");
		Type human = movable.newSubType(cache, "Human");
		cache.newSubType("Transformer", vehicle, human);
		closingWorkingSpace(cache);
	}

	public void testTree() {
		Cache cache = initWorkingSpace();
		Tree tree = cache.newTree("Tree");
		Node root = tree.newRoot(cache, "Root");
		Node child = root.addNode(cache, "Child");
		root.addNode(cache, "Child2");
		child.addNode(cache, "Child3");
		closingWorkingSpace(cache);
	}

	public void testInheritanceTree() {
		Cache cache = initWorkingSpace();
		Tree tree = cache.newTree("Tree");
		Node root = tree.newRoot(cache, "Root");
		Node child = root.addSubNode(cache, "Child");
		root.addSubNode(cache, "Child2");
		child.addSubNode(cache, "Child3");
		closingWorkingSpace(cache);
	}

	private static void cleanDirectory(String directoryPath) {
		File file = new File(directoryPath);
		if (file.exists())
			for (File f : file.listFiles())
				f.delete();
	}

	private void compareGraph(Cache cache, Generic persistedNode, Generic readNode) {
		readByInheritings(cache, persistedNode, readNode);
		readByComposites(cache, persistedNode, readNode);
	}

	private void readByInheritings(Cache cache, Generic persistedNode, Generic readNode) {
		int indexInherintings = 0;
		assert (persistedNode.getInheritings(cache).size() == readNode.getInheritings(cache).size()) : persistedNode.getInheritings(cache) + " / " + readNode.getInheritings(cache);
		for (Generic persistedGeneric : persistedNode.getInheritings(cache)) {
			compareGeneric(persistedGeneric, readNode.getInheritings(cache).get(indexInherintings));
			readByInheritings(cache, persistedGeneric, readNode.getInheritings(cache).get(indexInherintings));
			indexInherintings++;
		}
	}

	private void readByComposites(Cache cache, Generic persistedNode, Generic readNode) {
		int indexComposites = 0;
		assert (persistedNode.getComposites(cache).size() == readNode.getComposites(cache).size());
		for (Generic persistedGeneric : persistedNode.getComposites(cache)) {
			compareGeneric(persistedGeneric, readNode.getComposites(cache).get(indexComposites));
			readByComposites(cache, persistedGeneric, readNode.getComposites(cache).get(indexComposites));
			indexComposites++;
		}
	}

	private static void compareGeneric(Generic persistedGeneric, Generic readGeneric) {
		assert ((GenericImpl) persistedGeneric).getBirthTs() == ((GenericImpl) readGeneric).getBirthTs() : "BirthTs : " + ((GenericImpl) persistedGeneric).getBirthTs() + " / " + ((GenericImpl) readGeneric).getBirthTs();
		assert ((GenericImpl) persistedGeneric).getDeathTs() == ((GenericImpl) readGeneric).getDeathTs() : "DeathTs : " + ((GenericImpl) persistedGeneric).getDeathTs() + " / " + ((GenericImpl) readGeneric).getDeathTs();
		assert ((GenericImpl) persistedGeneric).getDesignTs() == ((GenericImpl) readGeneric).getDesignTs() : "DesignTs : " + ((GenericImpl) persistedGeneric).getDesignTs() + " / " + ((GenericImpl) readGeneric).getDesignTs();
		assert (persistedGeneric.getMetaLevel() == readGeneric.getMetaLevel());
		assert (persistedGeneric.getValue().equals(readGeneric.getValue()));
	}
}