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
		return GenericSystem.newCacheOnANewPersistentEngine(path).start();
	}

	private void closingWorkingSpace(Cache cache) {
		cache.flush();
		Engine engine = cache.getEngine();
		engine.close();
		// GenericSystem.activateNewCache(engine);
		compareGraph(engine, engine);// GenericSystem.newCacheOnANewInMemoryEngine(directoryPath + directoryNumber).getEngine());
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
		Attribute equipment = vehicle.setAttribute("Equipment");
		Generic myVehicle = vehicle.newInstance("myVehicle");
		myVehicle.setValue(equipment, "ABS");
		closingWorkingSpace(cache);
	}

	public void testAddAndRemove() {
		Cache cache = initWorkingSpace();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType("Car");
		Type truck = vehicle.newSubType("Truck");
		truck.newSubType("Van");
		car.remove();
		closingWorkingSpace(cache);
	}

	public void testLink() {
		Cache cache = initWorkingSpace();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Relation vehicleColor = vehicle.setRelation("VehicleColor", color);
		Generic myVehicle = vehicle.newInstance("myVehicle");
		Generic red = color.newInstance("red");
		myVehicle.setLink(vehicleColor, "myVehicleRed", red);
		closingWorkingSpace(cache);
	}

	public void testHeritage() {
		Cache cache = initWorkingSpace();
		Type vehicle = cache.newType("Vehicle");
		vehicle.newSubType("Car");
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
		Type vehicle = movable.newSubType("Vehicle");
		Type human = movable.newSubType("Human");
		cache.newSubType("Transformer", vehicle, human);
		closingWorkingSpace(cache);
	}

	public void testTree() {
		Cache cache = initWorkingSpace();
		Tree tree = cache.newTree("Tree");
		Node root = tree.newRoot("Root");
		Node child = root.setNode("Child");
		root.setNode("Child2");
		child.setNode("Child3");
		closingWorkingSpace(cache);
	}

	public void testInheritanceTree() {
		Cache cache = initWorkingSpace();
		Tree tree = cache.newTree("Tree");
		Node root = tree.newRoot("Root");
		Node child = root.setSubNode("Child");
		root.setSubNode("Child2");
		child.setSubNode("Child3");
		closingWorkingSpace(cache);
	}

	private static void cleanDirectory(String directoryPath) {
		File file = new File(directoryPath);
		if (file.exists())
			for (File f : file.listFiles())
				f.delete();
	}

	private void compareGraph(Generic persistedNode, Generic readNode) {
		readByInheritings(persistedNode, readNode);
		readByComposites(persistedNode, readNode);
	}

	private void readByInheritings(Generic persistedNode, Generic readNode) {
		int indexInherintings = 0;
		assert (persistedNode.getInheritings().size() == readNode.getInheritings().size()) : persistedNode.getInheritings() + " / " + readNode.getInheritings();
		for (Generic persistedGeneric : persistedNode.getInheritings()) {
			compareGeneric(persistedGeneric, readNode.getInheritings().get(indexInherintings));
			readByInheritings(persistedGeneric, readNode.getInheritings().get(indexInherintings));
			indexInherintings++;
		}
	}

	private void readByComposites(Generic persistedNode, Generic readNode) {
		int indexComposites = 0;
		assert (persistedNode.getComposites().size() == readNode.getComposites().size());
		for (Generic persistedGeneric : persistedNode.getComposites()) {
			compareGeneric(persistedGeneric, readNode.getComposites().get(indexComposites));
			readByComposites(persistedGeneric, readNode.getComposites().get(indexComposites));
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