package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class SuperOfTest extends AbstractTest {

	private Cache cache;

	@BeforeMethod
	public void initMethod() {
		cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
	}

	public void testMultiInheritings() {
		Type vehicle = cache.addType("Vehicle");
		Type robot = cache.addType("Robot");
		cache.addType("Transformer", vehicle, robot);
	}

	public void testMultiInheritingsWithInstance() {
		Type vehicle = cache.addType("Vehicle");
		Type robot = cache.addType("Robot");
		Type transformer = cache.addType("Transformer", vehicle, robot);
		Attribute power = transformer.setProperty("Power");
		Generic myTransformer = transformer.addInstance("myTransformer");
		myTransformer.setValue(power, 123);

		Type human = cache.addType("Human");
		Relation humanPilotTransformer = human.setRelation("pilot", transformer);
		Generic myck = human.addInstance("myck");
		myck.setLink(humanPilotTransformer, "myPilot", myTransformer);
	}

	@AfterMethod
	public void compare() {
		Engine engine = cache.getEngine();
		for (Generic inheriting : engine.getInheritings())
			internalCompare(inheriting, engine);
		for (Generic composite : engine.getComposites())
			internalCompare(composite, engine);
	}

	private void internalCompare(Generic superGeneric, Generic inheritingGeneric) {
		assert inheritingGeneric.inheritsFrom(superGeneric) == ((GenericImpl) superGeneric).isSuperOf(inheritingGeneric) : "superGeneric : " + superGeneric + " inheritingGeneric : " + inheritingGeneric + " => "
				+ inheritingGeneric.inheritsFrom(superGeneric) + " / " + ((GenericImpl) superGeneric).isSuperOf(inheritingGeneric);
		for (Generic inheriting : inheritingGeneric.getInheritings())
			internalCompare(superGeneric, inheriting);
		for (Generic composite : inheritingGeneric.getComposites())
			internalCompare(superGeneric, composite);
	}

}
