package org.genericsystem.test;

import java.util.List;

import javax.inject.Inject;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.Generic.AttributeWrapper;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.genericsystem.myadmin.beans.TypesBean;
import org.testng.annotations.Test;

@Test
public class TypesTest extends AbstractTest {

	@Inject
	private TypesBean typesBean;
	@Inject
	private Cache cache;

	// public void testTree() {
	// Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
	// directSubTypes(cache, cache.getEngine());
	// }
	//
	// private void directSubTypes(Cache cache, Type parent) {
	// log.info("DirectSubTypes " + parent.toString());
	// for (Type childen : parent.<Type> getDirectSubTypes(cache))
	// directSubTypes(cache, childen);
	// }

	// public void testGetAttributes() {
	// Type human = cache.newType("Human");
	// Generic michael = human.newInstance(cache, "Michael");
	// Generic michaelBrother = human.newInstance(cache, "MichaelBrother");
	// Relation isBrotherOf = human.setRelation(cache, "isBrotherOf", human);
	// isBrotherOf.enableMultiDirectional(cache);
	// michaelBrother.bind(cache, isBrotherOf, michael);
	//
	// List<AttributeWrapper> attributeWrappers = michaelBrother.getAttributeWrappers(cache).toList();
	// assert attributeWrappers.size() >= 2 : attributeWrappers.size();
	// assert attributeWrappers.contains(new AttributeWrapper(isBrotherOf, 0));
	// assert attributeWrappers.contains(new AttributeWrapper(isBrotherOf, 1));
	//
	// List<AttributeWrapper> attributeWrappers2 = michael.getAttributeWrappers(cache).toList();
	// assert attributeWrappers2.size() >= 2 : attributeWrappers2.size();
	// assert attributeWrappers2.contains(new AttributeWrapper(isBrotherOf, 0));
	// assert attributeWrappers2.contains(new AttributeWrapper(isBrotherOf, 1));
	// }

	// public void testGetOtherTargets() {
	// Type human = cache.newType("Human");
	// Generic michael = human.newInstance(cache, "Michael");
	// Generic michaelBrother = human.newInstance(cache, "MichaelBrother");
	// Relation isBrotherOf = human.setRelation(cache, "isBrotherOf", human);
	// isBrotherOf.enableMultiDirectional(cache);
	// Link link = michaelBrother.bind(cache, isBrotherOf, michael);
	//
	// List<Generic> targets = michaelBrother.getOtherTargets(cache, 0, link).toList();
	// assert targets.size() == 1 : targets.size();
	// assert targets.contains(michael);
	// assert !targets.contains(michaelBrother);
	// }

	public void testGUI() {
		Type human = cache.newType("Human");
		Generic michael = human.newInstance(cache, "Michael");
		Generic michaelBrother = human.newInstance(cache, "MichaelBrother");
		Relation isBrotherOf = human.setRelation(cache, "isBrotherOf", human);
		isBrotherOf.enableMultiDirectional(cache);
		michaelBrother.bind(cache, isBrotherOf, michael);

		typesBean.setSelectedTreeNode(michaelBrother);
		List<AttributeWrapper> attributeWrappers = typesBean.getAttributeWrappers();
		for (AttributeWrapper currentAttributeWrapper : attributeWrappers) {
			List<Holder> values = typesBean.getValues(currentAttributeWrapper);
			for (Holder currentValue : values) {
				log.info("ZZZZZZZZZZZZZZZZZZZZZZZZZZ attribute " + currentAttributeWrapper.getAttribute());
				log.info("ZZZZZZZZZZZZZZZZZZZZZZZZZZ value " + typesBean.getGenericWrapper(currentValue).getValue());
				List<Generic> targets = typesBean.getTargets(currentAttributeWrapper.getPosition(), currentValue);
				for (Generic currentTarget : targets) {
					log.info("ZZZZZZZZZZZZZZZZZZZZZZZZZZ target " + typesBean.getGenericWrapper(currentTarget).getValue());
				}
			}
		}
	}
}
