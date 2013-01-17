package org.genericsystem.impl;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Link;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Type;
import org.genericsystem.api.generic.Value;
import org.testng.annotations.Test;

@Test
public class MetaGenericTest extends AbstractTest {

	public void testMetaForMeta() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Engine engine = cache.getEngine();
		assert engine.getMeta().equals(engine);
		assert engine.getMetaAttribute().getMeta().equals(engine.getMetaAttribute()) : engine.getMetaAttribute().getMeta();
		assert engine.getMetaRelation().getMeta().equals(engine.getMetaRelation()) : engine.getMetaRelation().getMeta();
	}

	public void testMetaForTypeAndSubType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Engine engine = cache.getEngine();
		Type newType = cache.newType("newType");
		assert newType.getMeta().equals(engine);
		Type newSubType = newType.newSubType(cache, "newSubType");
		assert newSubType.getMeta().equals(engine);
	}

	public void testMetaForAttributeAndSubAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Engine engine = cache.getEngine();
		Attribute metaAttribute = cache.getEngine().getMetaAttribute();
		Type newType = cache.newType("newType");
		Type newSubType = newType.newSubType(cache, "newSubType");
		Attribute newAttribute = newType.addAttribute(cache, "newAttribute");
		assert newAttribute.getMeta().equals(metaAttribute);
		Attribute newSubAttribute = newSubType.addAttribute(cache, "newAttribute");
		assert newSubAttribute.getMeta().equals(engine.getMetaAttribute());
	}

	public void testMetaForRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Relation metaRelation = cache.getEngine().getMetaRelation();
		Type newType1 = cache.newType("newType1");
		Type newType2 = cache.newType("newType2");
		Relation newRelation = newType1.addRelation(cache, "newType1NewType2", newType2);
		assert newRelation.getMeta().equals(metaRelation);
	}

	public void testMetaForGeneric() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type newType = cache.newType("newType");
		Generic aNewType = newType.newInstance(cache, "aNewType");
		assert aNewType.getMeta().equals(newType);
	}

	public void testMetaForValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type newType = cache.newType("newType");
		Attribute newAttribute = newType.addAttribute(cache, "newAttribute");
		Generic aNewType = newType.newInstance(cache, "aNewType");
		Value value = aNewType.addValue(cache, newAttribute, "aNewAttribute");
		assert value.getMeta().equals(newAttribute);
	}

	public void testMetaForLink() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type newType1 = cache.newType("newType1");
		Type newType2 = cache.newType("newType2");
		Relation newType1NewType2 = newType1.addRelation(cache, "newType1NewType2", newType2);
		Generic aNewType1 = newType1.newInstance(cache, "aNewType1");
		Generic aNewType2 = newType2.newInstance(cache, "aNewType2");
		Link aNewType1Type2 = aNewType1.addLink(cache, newType1NewType2, "aNewType1NewType2", aNewType2);
		assert aNewType1Type2.getMeta().equals(newType1NewType2);
	}
}
