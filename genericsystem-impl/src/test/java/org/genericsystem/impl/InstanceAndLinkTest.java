package org.genericsystem.impl;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.generic.Link;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Type;
import org.genericsystem.impl.core.Statics;
import org.testng.annotations.Test;

@Test
public class InstanceAndLinkTest extends AbstractTest {

	public void testInstanceIsConcrete() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Generic audi = car.newInstance(cache, "audi");
		assert audi.isConcrete();
	}

	public void testCountAncestor() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Generic audi = car.newInstance(cache, "audi");
		Snapshot<Generic> snapshot = audi.getSupers();
		assert snapshot.size() == 1;
		assert snapshot.contains(car);
	}

	public void testInstanceIsConcreteWithValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.addRelation(cache, "outsideColor", color);
		Generic audi = car.newInstance(cache, "audi");
		Generic red = color.newInstance(cache, "red");
		Link audiIsRed = audi.addLink(cache, carColor, "audiRed", red);
		assert audiIsRed.isConcrete();
	}

	public void testCountAncestorLink() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.addRelation(cache, "outsideColor", color);
		Generic audi = car.newInstance(cache, "audi");
		Generic red = color.newInstance(cache, "red");
		Link audiIsRed = audi.addLink(cache, carColor, "audiRed", red);
		assert audiIsRed.getComponents().size() == 2;
	}

	public void testTargetsAncestor() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		Type human = cache.newType("Human");
		Generic myck = human.newInstance(cache, "myck");
		Relation possessVehicle = human.addRelation(cache, "HumanPossessVehicle", vehicle);
		Link myckPossessMyVehicle = myck.addLink(cache, possessVehicle, "myckPossessMyVehicle", myVehicle);
		assert myckPossessMyVehicle.getTargetComponent().equals(myVehicle);
	}

	public void testTargetsAncestorWithMultipleTarget() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Generic myVehicle1 = vehicle.newInstance(cache, "myVehicle1");
		Generic myVehicle2 = vehicle.newInstance(cache, "myVehicle2");
		Type human = cache.newType("Human");
		Generic myck = human.newInstance(cache, "myck");
		Relation possessVehicle = human.addRelation(cache, "HumanPossessVehicle", vehicle);
		Link myckPossessMyVehicle1 = myck.addLink(cache, possessVehicle, "myckPossessMyVehicle1", myVehicle1);
		Link myckPossessMyVehicle2 = myck.addLink(cache, possessVehicle, "myckPossessMyVehicle2", myVehicle2);
		assert myckPossessMyVehicle1.getTargetComponent().equals(myVehicle1);
		assert myckPossessMyVehicle1.getComponent(Statics.TARGET_POSITION).equals(myVehicle1) : myckPossessMyVehicle1.getComponent(Statics.TARGET_POSITION);
		assert myckPossessMyVehicle2.getTargetComponent().equals(myVehicle2);
		assert myckPossessMyVehicle2.getComponent(Statics.TARGET_POSITION).equals(myVehicle2) : myckPossessMyVehicle2.getComponent(Statics.TARGET_POSITION);
	}

	public void testUnidirectionalRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		Type human = cache.newType("Human");
		Generic myck = human.newInstance(cache, "myck");
		Relation possessVehicle = human.addRelation(cache, "HumanPossessVehicle", vehicle);
		Link myckPossessMyVehicle = myck.addLink(cache, possessVehicle, "myckPossessMyVehicle", myVehicle);
		assert myck.getLinks(cache, possessVehicle).size() == 1;
		assert myck.getLinks(cache, possessVehicle).contains(myckPossessMyVehicle);
		assert myVehicle.getLinks(cache, possessVehicle).size() == 0;
	}

	public void testBidirectionalRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		Type human = cache.newType("Human");
		Generic myck = human.newInstance(cache, "myck");
		Relation possessVehicle = human.addRelation(cache, "HumanPossessVehicle", vehicle);
		assert !possessVehicle.isMultiDirectional(cache);
		possessVehicle.enableMultiDirectional(cache);

		assert possessVehicle.isMultiDirectional(cache);
		assert human.getRelations(cache).contains(possessVehicle);
		assert vehicle.getRelations(cache).contains(possessVehicle) : vehicle.getRelations(cache);

		Link myckPossessMyVehicle = myck.addLink(cache, possessVehicle, "myckPossessMyVehicle", myVehicle);

		myckPossessMyVehicle.isAttributeOf(myVehicle);
		assert ((Relation) myckPossessMyVehicle).isMultiDirectional(cache);
		assert myck.getLinks(cache, possessVehicle).contains(myckPossessMyVehicle);
		assert myVehicle.getLinks(cache, possessVehicle).contains(myckPossessMyVehicle);
	}

	public void testRelationToHimself() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type human = cache.newType("Human");
		Generic myck = human.newInstance(cache, "myck");
		Generic quentin = human.newInstance(cache, "quentin");
		Relation brother = human.addRelation(cache, "brother", human);
		brother.enableMultiDirectional(cache);
		Link myBrother = myck.addLink(cache, brother, "myBrother", quentin);
		assert myck.getLinks(cache, brother).size() == 1;
		assert myck.getLinks(cache, brother).contains(myBrother);
		assert quentin.getLinks(cache, brother).size() == 1 : quentin.getLinks(cache, brother);
		assert quentin.getLinks(cache, brother).contains(myBrother);
	}

	public void testRelationToHimselfUnidirectional() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type human = cache.newType("Human");
		Generic myck = human.newInstance(cache, "myck");
		Generic quentin = human.newInstance(cache, "quentin");
		Relation littleBrother = human.addRelation(cache, "littleBrother", human);
		Link myBrother = myck.addLink(cache, littleBrother, "myLittleBrother", quentin);
		assert myck.getLinks(cache, littleBrother).size() == 1;
		assert myck.getLinks(cache, littleBrother).contains(myBrother);
		assert quentin.getLinks(cache, littleBrother).size() == 0;
	}

	public void testNewInstanceBinaryRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Generic myBMW = car.newInstance(cache, "myBMW");
		Type pilot = cache.newType("Pilot");
		Generic myck = pilot.newInstance(cache, "Pilot");

		Relation carPilot = car.addRelation(cache, "CarPilot", pilot);
		Link linkCarPilot = (Link) carPilot.newInstance(cache, "35%", myBMW, myck);
		assert myBMW.getLinks(cache, carPilot).size() == 1;
		assert myBMW.getLinks(cache, carPilot).contains(linkCarPilot);
	}

	public void testNewInstanceTernaryRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Generic myBMW = car.newInstance(cache, "myBMW");
		Type color = cache.newType("Color");
		Generic blue = color.newInstance(cache, "Blue");
		Type element = cache.newType("Element");
		Generic door = element.newInstance(cache, "Door");

		Relation carElementColor = car.addRelation(cache, "CarElementColor", element, color);
		Link myMBWBlueDoor = carElementColor.newInstance(cache, "droite", myBMW, door, blue);
		assert myBMW.getLinks(cache, carElementColor).size() == 1;
		assert myBMW.getLinks(cache, carElementColor).contains(myMBWBlueDoor);
	}

}
