package org.genericsystem.test;

import java.util.List;

import javax.inject.Inject;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.Structural;
import org.genericsystem.core.StructuralImpl;
import org.genericsystem.generic.Link;
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
	// for (Type childen : parent.<Type> getDirectSubTypes())
	// directSubTypes(cache, childen);
	// }

	public void testGetAttributes() {
		Type human = cache.newType("Human");
		Generic michael = human.newInstance("Michael");
		Generic quentin = human.newInstance("Quentin");
		Relation isBrotherOf = human.setRelation("isBrotherOf", human);
		isBrotherOf.enableMultiDirectional();
		quentin.bind(isBrotherOf, michael);

		List<Structural> structurals = quentin.getStructurals();
		assert structurals.size() >= 2 : structurals.size();
		assert structurals.contains(new StructuralImpl(isBrotherOf, 0));

		List<Structural> structurals2 = michael.getStructurals();
		assert structurals2.size() >= 2 : structurals2.size();
		assert structurals2.contains(new StructuralImpl(isBrotherOf, 0));
	}

	public void testGetOtherTargets() {
		Type human = cache.newType("Human");
		Generic michael = human.newInstance("Michael");
		Generic quentin = human.newInstance("Quentin");
		Relation isBrotherOf = human.setRelation("isBrotherOf", human);
		isBrotherOf.enableMultiDirectional();
		Link link = quentin.bind(isBrotherOf, michael);

		List<Generic> targets = quentin.getOtherTargets(0, link);
		assert targets.size() == 1 : targets.size();
		assert targets.contains(michael);
		assert !targets.contains(quentin);
	}
}
