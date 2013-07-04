package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Statics;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class SelfRelationTest extends AbstractTest {

	public void test() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Generic red = color.newInstance( "red");
		Relation carOutsideColor = car.setRelation( "outside", color);
		car.setLink( carOutsideColor, "defaultOutside", red);
		assert car.getTargets( carOutsideColor).contains(red);

		Generic myCar = car.newInstance( "myCar");
		assert myCar.getTargets( carOutsideColor).contains(red);

		assert red.getTargets( carOutsideColor, Statics.BASE_POSITION).contains(myCar) : red.getTargets( carOutsideColor, Statics.BASE_POSITION);

		Generic myBmw = car.newInstance( "myBmw");
		assert red.getTargets( carOutsideColor, Statics.BASE_POSITION).contains(myCar) : red.getTargets( carOutsideColor, Statics.BASE_POSITION);
		assert red.getTargets( carOutsideColor, Statics.BASE_POSITION).contains(myBmw) : red.getTargets( carOutsideColor, Statics.BASE_POSITION);
	}

	public void paternityRelationTest() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type human = cache.newType("Human");
		Relation paternity = human.setRelation( "paternity", human);
		Generic theFather = human.newInstance( "theFather");
		Generic theSon = human.newInstance( "theSon");
		Link mySon = theFather.setLink( paternity, "mySon", theSon);
		assert human.getRelations().size() == 1;
		assert theFather.getLinks( paternity).contains(mySon) : theFather.getLinks( paternity);
		assert !theSon.getLinks( paternity).contains(theFather);
		assert theFather.getTargets( paternity).contains(theSon) : theFather.getLinks( paternity);
		assert !theSon.getTargets( paternity).contains(theFather) : theSon.getTargets( paternity);
	}

	public void fraternityRelationTest() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type human = cache.newType("Human");
		Relation fraternity = human.setRelation( "brother", human);
		fraternity.enableMultiDirectional();
		Generic theBrother1 = human.newInstance( "theBrother1");
		Generic theBrother2 = human.newInstance( "theBrother2");
		Link myBrother = theBrother1.setLink( fraternity, "myBrother", theBrother2);
		assert human.getRelations().size() == 1;
		assert theBrother1.getLinks( fraternity).contains(myBrother) : theBrother1.getLinks( fraternity);
		assert theBrother2.getLinks( fraternity).contains(myBrother) : theBrother2.getLinks( fraternity);
		assert theBrother1.getTargets( fraternity).contains(theBrother2) : theBrother1.getLinks( fraternity);
		assert theBrother2.getTargets( fraternity, Statics.BASE_POSITION).contains(theBrother1) : theBrother2.getTargets( fraternity, Statics.BASE_POSITION);
	}

}
