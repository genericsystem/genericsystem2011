package org.genericsystem.impl;

import java.util.List;
import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Statics;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class InstanceAndLinkTest extends AbstractTest {

	public void testInstanceIsConcrete() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Generic audi = car.addInstance("audi");
		assert audi.isConcrete();
	}

	public void testCountAncestor() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Generic audi = car.addInstance("audi");
		List<Generic> supers = audi.supers();
		assert supers.size() == 1;
		assert supers.contains(car);
	}

	public void testInstanceIsConcreteWithValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type color = cache.addType("Color");
		Relation carColor = car.setRelation("outsideColor", color);
		Generic audi = car.addInstance("audi");
		Generic red = color.addInstance("red");
		Link audiIsRed = audi.setLink(carColor, "audiRed", red);
		assert audiIsRed.isConcrete();
	}

	public void testCountAncestorLink() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Type color = cache.addType("Color");

		Relation carColor = car.setRelation("carColor", color);
		Generic audi = car.addInstance("audi");
		Generic red = color.addInstance("red");
		Link audiIsRed = audi.setLink(carColor, "audiRed", red);
		assert audiIsRed.components().size() == 2;
	}

	public void testTargetsAncestor() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Type human = cache.addType("Human");
		Generic myck = human.addInstance("myck");
		Relation possessVehicle = human.setRelation("HumanPossessVehicle", vehicle);
		Link myckPossessMyVehicle = myck.setLink(possessVehicle, "myckPossessMyVehicle", myVehicle);
		assert myckPossessMyVehicle.getTargetComponent().equals(myVehicle);
	}

	public void testTargetsAncestorWithMultipleTarget() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Generic myVehicle1 = vehicle.addInstance("myVehicle1");
		Generic myVehicle2 = vehicle.addInstance("myVehicle2");
		Type human = cache.addType("Human");
		Generic myck = human.addInstance("myck");
		Relation possessVehicle = human.setRelation("HumanPossessVehicle", vehicle);
		Link myckPossessMyVehicle1 = myck.setLink(possessVehicle, "myckPossessMyVehicle1", myVehicle1);
		Link myckPossessMyVehicle2 = myck.setLink(possessVehicle, "myckPossessMyVehicle2", myVehicle2);
		assert myckPossessMyVehicle1.getTargetComponent().equals(myVehicle1);
		assert myckPossessMyVehicle1.getComponent(Statics.TARGET_POSITION).equals(myVehicle1) : myckPossessMyVehicle1.getComponent(Statics.TARGET_POSITION);
		assert myckPossessMyVehicle2.getTargetComponent().equals(myVehicle2);
		assert myckPossessMyVehicle2.getComponent(Statics.TARGET_POSITION).equals(myVehicle2) : myckPossessMyVehicle2.getComponent(Statics.TARGET_POSITION);
	}

	public void testUnidirectionalRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Type human = cache.addType("Human");
		Generic myck = human.addInstance("myck");
		Relation possessVehicle = human.setRelation("HumanPossessVehicle", vehicle);
		Link myckPossessMyVehicle = myck.setLink(possessVehicle, "myckPossessMyVehicle", myVehicle);
		assert myck.getLinks(possessVehicle).size() == 1;
		assert myck.getLinks(possessVehicle).contains(myckPossessMyVehicle);
		assert myVehicle.getLinks(possessVehicle).size() == 1 : myVehicle.getLinks(possessVehicle);
		assert myVehicle.getLinks(possessVehicle).contains(myckPossessMyVehicle);
	}

	public void testBidirectionalRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Type human = cache.addType("Human");
		Generic myck = human.addInstance("myck");
		Relation possessVehicle = human.setRelation("HumanPossessVehicle", vehicle);
		// assert !possessVehicle.isMultiDirectional();
		// possessVehicle.enableMultiDirectional();
		// assert possessVehicle.isMultiDirectional();
		assert human.getRelations().contains(possessVehicle);
		assert vehicle.getRelations().contains(possessVehicle) : vehicle.getRelations();

		Link myckPossessMyVehicle = myck.setLink(possessVehicle, "myckPossessMyVehicle", myVehicle);

		myckPossessMyVehicle.isAttributeOf(myVehicle);
		// assert ((Relation) myckPossessMyVehicle).isMultiDirectional();
		assert myck.getLinks(possessVehicle).contains(myckPossessMyVehicle);
		assert myVehicle.getLinks(possessVehicle).contains(myckPossessMyVehicle);
	}

	public void testRelationToHimself() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type human = cache.addType("Human");
		Generic myck = human.addInstance("myck");
		Generic quentin = human.addInstance("quentin");
		Relation brother = human.setRelation("brother", human);
		// brother.enableMultiDirectional();
		Link myBrother = myck.setLink(brother, "myBrother", quentin);
		assert myck.getLinks(brother).size() == 1;
		assert myck.getLinks(brother).contains(myBrother);
		assert quentin.getLinks(brother, 1).size() == 1 : quentin.getLinks(brother, 1);
		assert quentin.getLinks(brother, 1).contains(myBrother);
	}

	public void testRelationToHimselfUnidirectional() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type human = cache.addType("Human");
		Generic myck = human.addInstance("myck");
		Generic quentin = human.addInstance("quentin");
		Relation littleBrother = human.setRelation("littleBrother", human);
		Link myBrother = myck.setLink(littleBrother, "myLittleBrother", quentin);
		assert myck.getLinks(littleBrother).size() == 1;
		assert myck.getLinks(littleBrother).contains(myBrother);
		assert quentin.getLinks(littleBrother).size() == 0 : quentin.getLinks(littleBrother);
	}

	public void testNewInstanceBinaryRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Generic myBMW = car.addInstance("myBMW");
		Type pilot = cache.addType("Pilot");
		Generic myck = pilot.addInstance("Pilot");

		Relation carPilot = car.setRelation("CarPilot", pilot);
		Link linkCarPilot = (Link) carPilot.addInstance("35%", myBMW, myck);
		assert myBMW.getLinks(carPilot).size() == 1 : myBMW.getLinks(carPilot);
		assert myBMW.getLinks(carPilot).contains(linkCarPilot);
	}

	public void testNewInstanceTernaryRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.addType("Car");
		Generic myBMW = car.addInstance("myBMW");
		Type color = cache.addType("Color");
		Generic blue = color.addInstance("Blue");
		Type element = cache.addType("Element");
		Generic door = element.addInstance("Door");

		Relation carElementColor = car.setRelation("CarElementColor", element, color);
		Link myMBWBlueDoor = carElementColor.addInstance("droite", myBMW, door, blue);
		assert myBMW.getLinks(carElementColor).size() == 1;
		assert myBMW.getLinks(carElementColor).contains(myMBWBlueDoor);
	}

}
