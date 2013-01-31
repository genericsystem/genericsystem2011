package org.genericsystem.impl;

import java.util.Arrays;
import java.util.Random;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.exception.ReferentialIntegrityConstraintViolationException;
import org.genericsystem.api.generic.Node;
import org.genericsystem.api.generic.Node.Visitor;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Tree;
import org.genericsystem.api.generic.Type;
import org.genericsystem.impl.core.GenericImpl;
import org.genericsystem.impl.core.Statics;
import org.testng.annotations.Test;

@Test
public class TreeTest extends AbstractTest {

	public void testTree() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Tree tree = cache.newTree("Tree");
		final Node root = tree.newRoot(cache, "Root");
		Node child = root.addNode(cache, "Child");
		Node child2 = root.addNode(cache, "Child2");
		Node child3 = child.addNode(cache, "Child3");

		assert root.getMeta().equals(tree);
		assert root.inheritsFrom(tree);
		assert root.isInstanceOf(tree);
		assert tree.getAllInstances(cache).size() == 4 : tree.getAllInstances(cache);
		assert child.isAttributeOf(root);
		assert child.isInstanceOf(tree);
		assert child.getBaseComponent().equals(root);
		assert !child3.inheritsFrom(root);
		assert child3.isInstanceOf(tree);
		assert child3.getBaseComponent().equals(child);
		assert root.getInheritings(cache).isEmpty();
		assert tree.getAllInstances(cache).containsAll(Arrays.asList(new Generic[] { root, child, child2, child3 }));
		assert child.inheritsFrom(tree);
		assert !child.inheritsFrom(root);
		assert child.isAttributeOf(root);
		assert child.isAttributeOf(root, 0);
		assert root.getChildren(cache).contains(child);
		assert !root.getChildren(cache).contains(root);
		assert root.getChildren(cache).contains(child2);
		assert !root.getChildren(cache).contains(child3);
		assert child.getChildren(cache).contains(child3);
		assert !child.getChildren(cache).contains(child);
		assert !child3.getChildren(cache).contains(child);
		assert !child3.getChildren(cache).contains(child2);
		assert child3.getChildren(cache).isEmpty();
		cache.flush();

		tree.enableReferentialIntegrity(cache, Statics.BASE_POSITION).log();
		assert tree.isReferentialIntegrity(cache, Statics.BASE_POSITION);
		assert root.isReferentialIntegrity(cache, Statics.BASE_POSITION);
		assert child.inheritsFrom(tree);
		assert ((GenericImpl) tree).getAllInheritings(cache).contains(child);
		assert child.isReferentialIntegrity(cache, Statics.BASE_POSITION);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				// mount a cache for try
				root.remove(cache.newSuperCache());
			}
		}.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
		tree.disableReferentialIntegrity(cache, Statics.BASE_POSITION);

		root.remove(cache);
		assert !root.isAlive(cache);
		assert !child.isAlive(cache);
		assert !child2.isAlive(cache);
		assert !child3.isAlive(cache);
		cache.flush();
	}

	public void testInheritingTree() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Tree tree = cache.newTree("Tree");
		final Node root = tree.newRoot(cache, "Root");
		Node child = root.addSubNode(cache, "Child");
		Node child2 = root.addSubNode(cache, "Child2");
		Node child3 = child.addSubNode(cache, "Child3");

		child.log();

		assert root.getMeta().equals(tree);
		assert root.inheritsFrom(tree);
		assert root.isInstanceOf(tree);
		assert child.isAttributeOf(root);
		assert child.isInstanceOf(tree);
		assert child.getBaseComponent().equals(root);
		assert child3.inheritsFrom(root);
		assert child3.isInstanceOf(tree);
		assert child3.getBaseComponent().equals(child);
		assert ((GenericImpl) root).getAllInheritings(cache).containsAll(Arrays.asList(new Generic[] { root, child, child2, child3 })) : ((GenericImpl) root).getAllInheritings(cache);
		assert tree.getAllInstances(cache).contains(root);
		assert tree.getAllInstances(cache).contains(child);
		assert tree.getAllInstances(cache).contains(child2);
		assert tree.getAllInstances(cache).contains(child3);
		assert tree.getAllInstances(cache).size() == 4 : tree.getAllInstances(cache);
		assert root.getChildren(cache).contains(child) : root.getChildren(cache);
		assert !root.getChildren(cache).contains(root);
		assert root.getChildren(cache).contains(child2);
		assert !root.getChildren(cache).contains(child3);
		assert child.getChildren(cache).contains(child3);
		assert tree.isTree();
		assert !child.getChildren(cache).contains(child2);
		assert !child.getChildren(cache).contains(child);
		assert !child3.getChildren(cache).contains(child);
		assert !child3.getChildren(cache).contains(child2);
		assert child3.getChildren(cache).isEmpty();
		cache.flush();

	}

	public void testGenealogicTree() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Tree bitree = cache.newTree("Tree");
		Node grandFather = bitree.newRoot(cache, "grandFather");
		Node grandMother = bitree.newRoot(cache, "grandMother");
		Node mother = bitree.newRoot(cache, "mother");
		Node father = grandFather.addNode(cache, "father", grandMother);
		Node fatherSister = grandFather.addNode(cache, "fatherSister", grandMother);
		father.addNode(cache, "son", mother);
		assert grandFather.getChildren(cache).contains(father);
		assert grandFather.getChildren(cache).contains(fatherSister);
		bitree.enableMultiDirectional(cache);
		assert grandMother.getChildren(cache).contains(father);
		assert grandMother.getChildren(cache).contains(fatherSister);
		bitree.disableMultiDirectional(cache);
		assert !bitree.isMultiDirectional(cache) : bitree.getComposites(cache);
		assert grandMother.getChildren(cache).isEmpty() : grandMother.getChildren(cache);
	}

	public void testAnalogicTree() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Tree graphicComponent = cache.newTree("GraphicComponent");
		Node webPage = graphicComponent.newRoot(cache, "webPage");
		Node header = webPage.addSubNode(cache, "header");
		Node body = webPage.addSubNode(cache, "body");
		Node footer = webPage.addSubNode(cache, "footer");
		body.addSubNode(cache, "message1");
		body.addSubNode(cache, "message2");

		Type color = cache.newType("Color");
		Generic red = color.newInstance(cache, "Red");
		Generic blue = color.newInstance(cache, "Blue");
		Generic yellow = color.newInstance(cache, "Yellow");

		Relation graphicComponentColor = graphicComponent.addRelation(cache, "GraphicComponentColor", color);
		graphicComponentColor.enablePropertyConstraint(cache);
		graphicComponentColor.enableSingularConstraint(cache);
		assert graphicComponentColor.isPropertyConstraintEnabled(cache);

		webPage.bind(cache, graphicComponentColor, red);
		header.bind(cache, graphicComponentColor, blue);
		header.bind(cache, graphicComponentColor, blue);
		footer.bind(cache, graphicComponentColor, yellow);

		assert red.equals(body.getLink(cache, graphicComponentColor).getTargetComponent()) : body.getLinks(cache, graphicComponentColor);
		assert "Red".equals(body.getLinks(cache, graphicComponentColor).first().getTargetComponent().getValue());
		header.bind(cache, graphicComponentColor, blue).log();
		cache.flush();
	}

	public void visitTree() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Tree tree = cache.newTree("Tree");
		final Node root = tree.newRoot(cache, "Root");

		Node child = root.addNode(cache, "Child");
		root.addNode(cache, "Child2");
		child.addNode(cache, "Child3");

		root.traverse(new Visitor(cache) {
			@Override
			public void before(Node node) {
				log.info("before : " + node);
			}
		});

		root.traverse(new Visitor(cache) {
			@Override
			public void after(Node node) {
				log.info("after : " + node);
			}
		});
	}

	public void testTree2() {
		String directoryPath = System.getenv("HOME") + "/test/snapshot_save" + new Random().nextInt();
		Cache cache = GenericSystem.newCacheOnANewPersistentEngine(directoryPath);
		assert cache.getMetaAttribute().isMeta();
		Tree tree = cache.newTree("Tree");
		Node root = tree.newRoot(cache, "Root");
		root.log();
		Node child = root.addNode(cache, "Child");
		root.addNode(cache, "Child2");
		child.addNode(cache, "Child3");
		cache.flush();
		cache.getEngine().close();
		cache = GenericSystem.newCacheOnANewPersistentEngine(directoryPath);
		assert ((GenericImpl) tree).getDesignTs() == ((GenericImpl) cache.newTree("Tree")).getDesignTs();
	}

	public void testInheritanceTree2() {
		String directoryPath = System.getenv("HOME") + "/test/snapshot_save" + new Random().nextInt();
		Cache cache = GenericSystem.newCacheOnANewPersistentEngine(directoryPath);
		Tree tree = cache.newTree("Tree");
		Node root = tree.newRoot(cache, "Root");
		Node child = root.addSubNode(cache, "Child");
		root.addSubNode(cache, "Child2");
		child.addSubNode(cache, "Child3");
		cache.flush();
		cache.getEngine().close();

		cache = GenericSystem.newCacheOnANewPersistentEngine(directoryPath);
		assert ((GenericImpl) tree).getDesignTs() == ((GenericImpl) cache.newTree("Tree")).getDesignTs();
	}
}
