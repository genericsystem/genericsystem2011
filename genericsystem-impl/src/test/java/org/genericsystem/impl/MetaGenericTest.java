package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class MetaGenericTest extends AbstractTest {

	public void testMetaForMeta() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Engine engine = cache.getEngine();
		Attribute metaAttribute = engine.getMetaAttribute();
		Relation metaRelation = engine.getMetaRelation();
		assert engine.getMeta().equals(engine);
		assert engine.getMetaAttribute().getMeta().equals(engine);
		assert engine.getMetaRelation().getMeta().equals(engine.getMetaAttribute());
	}

	public void testMetaForTypeAndSubType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Engine engine = cache.getEngine();
		Type newType = cache.addType("newType");
		assert newType.getMeta().equals(engine);
		Type newSubType = newType.newSubType("newSubType");
		assert newSubType.getMeta().equals(engine);
	}

	public void testMetaForAttributeAndSubAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Attribute metaAttribute = cache.getEngine().getMetaAttribute();
		Type newType = cache.addType("newType");
		Type newSubType = newType.newSubType("newSubType");
		Attribute newAttribute = newType.setAttribute("newAttribute");
		assert newAttribute.getMeta().equals(metaAttribute);
		Attribute newSubAttribute = newSubType.setAttribute("newAttribute");
		assert newSubAttribute.getMeta().equals(metaAttribute);
	}

	public void testMetaForRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Relation metaRelation = cache.getEngine().getMetaRelation();
		Type newType1 = cache.addType("newType1");
		Type newType2 = cache.addType("newType2");
		Relation newRelation = newType1.setRelation("newType1NewType2", newType2);
		assert newRelation.getMeta().equals(metaRelation) : newRelation.info();
	}

	public void testMetaForGeneric() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type newType = cache.addType("newType");
		Generic aNewType = newType.newInstance("aNewType");
		assert aNewType.getMeta().equals(newType);
	}

	public void testMetaForValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type newType = cache.addType("newType");
		Attribute newAttribute = newType.setAttribute("newAttribute");
		Generic aNewType = newType.newInstance("aNewType");
		Holder value = aNewType.setValue(newAttribute, "aNewAttribute");
		assert value.getMeta().equals(newAttribute);
	}

	public void testMetaForLink() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type newType1 = cache.addType("newType1");
		Type newType2 = cache.addType("newType2");
		Relation newType1NewType2 = newType1.setRelation("newType1NewType2", newType2);
		Generic aNewType1 = newType1.newInstance("aNewType1");
		Generic aNewType2 = newType2.newInstance("aNewType2");
		Link aNewType1Type2 = aNewType1.setLink(newType1NewType2, "aNewType1NewType2", aNewType2);
		assert aNewType1Type2.getMeta().equals(newType1NewType2);
	}
}
