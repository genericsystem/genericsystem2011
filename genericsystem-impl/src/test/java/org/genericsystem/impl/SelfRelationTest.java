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
		Generic red = color.newInstance(cache, "red");
		Relation carOutsideColor = car.setRelation(cache, "outside", color);
		car.setLink(cache, carOutsideColor, "defaultOutside", red);
		assert car.getTargets(cache, carOutsideColor).contains(red);

		Generic myCar = car.newInstance(cache, "myCar");
		assert myCar.getTargets(cache, carOutsideColor).contains(red);

		assert red.getTargets(cache, carOutsideColor, Statics.BASE_POSITION).contains(myCar) : red.getTargets(cache, carOutsideColor, Statics.BASE_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");
		assert red.getTargets(cache, carOutsideColor, Statics.BASE_POSITION).contains(myCar) : red.getTargets(cache, carOutsideColor, Statics.BASE_POSITION);
		assert red.getTargets(cache, carOutsideColor, Statics.BASE_POSITION).contains(myBmw) : red.getTargets(cache, carOutsideColor, Statics.BASE_POSITION);
	}

	public void paternityRelationTest() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type human = cache.newType("Human");
		Relation paternity = human.setRelation(cache, "paternity", human);
		Generic theFather = human.newInstance(cache, "theFather");
		Generic theSon = human.newInstance(cache, "theSon");
		Link mySon = theFather.setLink(cache, paternity, "mySon", theSon);
		assert human.getRelations(cache).size() == 1;
		assert theFather.getLinks(cache, paternity).contains(mySon) : theFather.getLinks(cache, paternity);
		assert !theSon.getLinks(cache, paternity).contains(theFather);
		assert theFather.getTargets(cache, paternity).contains(theSon) : theFather.getLinks(cache, paternity);
		assert !theSon.getTargets(cache, paternity).contains(theFather) : theSon.getTargets(cache, paternity);
	}

	public void fraternityRelationTest() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type human = cache.newType("Human");
		Relation fraternity = human.setRelation(cache, "brother", human);
		fraternity.enableMultiDirectional(cache);
		Generic theBrother1 = human.newInstance(cache, "theBrother1");
		Generic theBrother2 = human.newInstance(cache, "theBrother2");
		Link myBrother = theBrother1.setLink(cache, fraternity, "myBrother", theBrother2);
		assert human.getRelations(cache).size() == 1;
		assert theBrother1.getLinks(cache, fraternity).contains(myBrother) : theBrother1.getLinks(cache, fraternity);
		assert theBrother2.getLinks(cache, fraternity).contains(myBrother) : theBrother2.getLinks(cache, fraternity);
		assert theBrother1.getTargets(cache, fraternity).contains(theBrother2) : theBrother1.getLinks(cache, fraternity);
		assert theBrother2.getTargets(cache, fraternity, Statics.BASE_POSITION).contains(theBrother1) : theBrother2.getTargets(cache, fraternity, Statics.BASE_POSITION);
	}

}
