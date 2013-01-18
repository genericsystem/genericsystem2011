package org.genericsystem.impl;

import java.io.File;
import java.util.Random;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Node;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Tree;
import org.genericsystem.api.generic.Type;
import org.genericsystem.impl.core.GenericImpl;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class PersistenceTest {

	private String directoryPath = System.getenv("HOME") + "/test/snapshot_save";

	private Cache cache;
	private int directoryNumber;

	@BeforeMethod
	public void initWorkingSpace() {
		directoryNumber = new Random().nextInt();
		String path = directoryPath + directoryNumber;
		cleanDirectory(path);
		cache = GenericSystem.newCacheOnANewPersistentEngine(path);
	}

	@AfterMethod
	public void closingWorkingSpace() {
		cache.flush();
		Engine engine = cache.getEngine();
		engine.close();
//		GenericSystem.activateNewCache(engine);
		compareGraph(engine, engine);//GenericSystem.newCacheOnANewInMemoryEngine(directoryPath + directoryNumber).getEngine());
	}

	public void testDefaultConfiguration() {
	}

	public void testOnlyAType() {
		cache.newType("Vehicle");
	}

	public void testCustomTypeAndItsInstance() {
		Type vehicle = cache.newType("Vehicle");
		Attribute equipment = vehicle.addAttribute(cache, "Equipment");
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		myVehicle.setValue(cache, equipment, "ABS");
	}

	public void testAddAndRemove() {
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Type truck = vehicle.newSubType(cache, "Truck");
		truck.newSubType(cache, "Van");
		car.remove(cache);
	}

	public void testLink() {
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Relation vehicleColor = vehicle.addRelation(cache, "VehicleColor", color);
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		Generic red = color.newInstance(cache, "red");
		myVehicle.setLink(cache, vehicleColor, "myVehicleRed", red);
	}

	public void testHeritage() {
		Type vehicle = cache.newType("Vehicle");
		vehicle.newSubType(cache, "Car");
	}

	public void testHeritageMultiple() {
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		cache.newSubType("Transformer", vehicle, human);
	}

	public void testHeritageMultipleDiamond() {
		Type movable = cache.newType("Movable");
		Type vehicle = movable.newSubType(cache, "Vehicle");
		Type human = movable.newSubType(cache, "Human");
		cache.newSubType("Transformer", vehicle, human);
	}

	public void testTree() {
		Tree tree = cache.newTree("Tree");
		Node root = tree.newRoot(cache, "Root");
		Node child = root.addNode(cache, "Child");
		root.addNode(cache, "Child2");
		child.addNode(cache, "Child3");
	}

	public void testInheritanceTree() {
		Tree tree = cache.newTree("Tree");
		Node root = tree.newRoot(cache, "Root");
		Node child = root.addSubNode(cache, "Child");
		root.addSubNode(cache, "Child2");
		child.addSubNode(cache, "Child3");
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
		assert (persistedNode.getInheritings(cache).size() == readNode.getInheritings(cache).size()) : persistedNode.getInheritings(cache) + " / " + readNode.getInheritings(cache);
		for (Generic persistedGeneric : persistedNode.getInheritings(cache)) {
			compareGeneric(persistedGeneric, readNode.getInheritings(cache).get(indexInherintings));
			readByInheritings(persistedGeneric, readNode.getInheritings(cache).get(indexInherintings));
			indexInherintings++;
		}
	}

	private void readByComposites(Generic persistedNode, Generic readNode) {
		int indexComposites = 0;
		assert (persistedNode.getComposites(cache).size() == readNode.getComposites(cache).size());
		for (Generic persistedGeneric : persistedNode.getComposites(cache)) {
			compareGeneric(persistedGeneric, readNode.getComposites(cache).get(indexComposites));
			readByComposites(persistedGeneric, readNode.getComposites(cache).get(indexComposites));
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