package org.genericsystem.impl;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Type;
import org.genericsystem.impl.core.GenericImpl;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class SuperOfTest extends AbstractTest {

	private Cache cache;

	@BeforeMethod
	public void initMethod() {
		cache = GenericSystem.newCacheOnANewInMemoryEngine();
	}

	public void testMultiInheritings() {
		Type vehicle = cache.newType("Vehicle");
		Type robot = cache.newType("Robot");
		cache.newSubType("Transformer", vehicle, robot);
	}

	public void testMultiInheritingsWithInstance() {
		Type vehicle = cache.newType("Vehicle");
		Type robot = cache.newType("Robot");
		Type transformer = cache.newSubType("Transformer", vehicle, robot);
		Attribute power = transformer.setProperty(cache, "Power");
		Generic myTransformer = transformer.newInstance(cache, "myTransformer");
		myTransformer.setValue(cache, power, 123);

		Type human = cache.newType("Human");
		Relation humanPilotTransformer = human.setRelation(cache, "pilot", transformer);
		Generic myck = human.newInstance(cache, "myck");
		myck.setLink(cache, humanPilotTransformer, "myPilot", myTransformer);
	}

	@AfterMethod
	public void compare() {
		Engine engine = cache.getEngine();
		for (Generic inheriting : engine.getInheritings(cache))
			internalCompare(inheriting, engine);
		for (Generic composite : engine.getComposites(cache))
			internalCompare(composite, engine);
	}

	private void internalCompare(Generic superGeneric, Generic inheritingGeneric) {
		assert inheritingGeneric.inheritsFrom(superGeneric) == ((GenericImpl) superGeneric).isSuperOf(inheritingGeneric, false) : "superGeneric : " + superGeneric + " inheritingGeneric : " + inheritingGeneric + " => "
				+ inheritingGeneric.inheritsFrom(superGeneric) + " / " + ((GenericImpl) superGeneric).isSuperOf(inheritingGeneric, false);
		for (Generic inheriting : inheritingGeneric.getInheritings(cache))
			internalCompare(superGeneric, inheriting);
		for (Generic composite : inheritingGeneric.getComposites(cache))
			internalCompare(superGeneric, composite);
	}

}
