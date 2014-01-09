package org.genericsystem.impl;

import java.util.Arrays;
import java.util.Random;
import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Statics;
import org.genericsystem.exception.ReferentialIntegrityConstraintViolationException;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Node;
import org.genericsystem.generic.Node.Visitor;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Tree;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class TreeTest extends AbstractTest {

	public void test() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Tree tree = cache.setTree("Tree");
		tree.enableReferentialIntegrity(Statics.BASE_POSITION);
		assert tree.isReferentialIntegrity(Statics.BASE_POSITION);
		tree.disableReferentialIntegrity(Statics.BASE_POSITION);
		assert !tree.isReferentialIntegrity(Statics.BASE_POSITION);
	}

	public void testTree() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Tree tree = cache.setTree("Tree");
		final Node root = tree.addRoot("Root");
		Node child = root.setNode("Child");
		Node child2 = root.setNode("Child2");
		Node child3 = child.setNode("Child3");

		assert root.getMeta().equals(tree);
		assert root.inheritsFrom(tree);
		assert root.isInstanceOf(tree);
		assert tree.getAllInstances().size() == 4 : tree.getAllInstances();
		assert child.isAttributeOf(root);
		assert child.isInstanceOf(tree);
		assert child.getBaseComponent().equals(root);
		assert !child3.inheritsFrom(root);
		assert child3.isInstanceOf(tree);
		assert child3.getBaseComponent().equals(child);
		assert root.getInheritings().isEmpty();
		assert tree.getAllInstances().containsAll(Arrays.asList(new Generic[] { root, child, child2, child3 }));
		assert child.inheritsFrom(tree);
		assert !child.inheritsFrom(root);
		assert child.isAttributeOf(root);
		assert child.isAttributeOf(root);
		assert root.getChildren().contains(child);
		assert !root.getChildren().contains(root);
		assert root.getChildren().contains(child2);
		assert !root.getChildren().contains(child3);
		assert child.getChildren().contains(child3);
		assert !child.getChildren().contains(child);
		assert !child3.getChildren().contains(child);
		assert !child3.getChildren().contains(child2);
		assert child3.getChildren().isEmpty();
		cache.flush();

		tree.enableReferentialIntegrity(Statics.BASE_POSITION);
		assert tree.isReferentialIntegrity(Statics.BASE_POSITION);
		assert root.isReferentialIntegrity(Statics.BASE_POSITION);
		assert child.inheritsFrom(tree);
		assert ((GenericImpl) tree).getAllInheritings().contains(child);
		assert child.isReferentialIntegrity(Statics.BASE_POSITION);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				// mount a cache for try
				cache.mountNewCache().start();
				root.remove();
			}
		}.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
		cache.start();
		tree.disableReferentialIntegrity(Statics.BASE_POSITION);

		assert !tree.isReferentialIntegrity(Statics.BASE_POSITION);
		root.remove();
		assert !root.isAlive();
		assert !child.isAlive();
		assert !child2.isAlive();
		assert !child3.isAlive();
		cache.flush();
	}

	public void testInheritingTree() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Tree tree = cache.setTree("Tree");
		final Node root = tree.addRoot("Root");
		Node child = root.setSubNode("Child");
		Node child2 = root.setSubNode("Child2");
		Node child3 = child.setSubNode("Child3");

		assert root.getMeta().equals(tree);
		assert root.inheritsFrom(tree);
		assert root.isInstanceOf(tree);
		assert child.isAttributeOf(root);
		assert child.isInstanceOf(tree);
		assert child.getBaseComponent().equals(root);
		assert child3.inheritsFrom(root);
		assert child3.isInstanceOf(tree);
		assert child3.getBaseComponent().equals(child);
		assert ((GenericImpl) root).getAllInheritings().containsAll(Arrays.asList(new Generic[] { root, child, child2, child3 })) : ((GenericImpl) root).getAllInheritings();
		assert tree.getAllInstances().contains(root);
		assert tree.getAllInstances().contains(child);
		assert tree.getAllInstances().contains(child2);
		assert tree.getAllInstances().contains(child3);
		assert tree.getAllInstances().size() == 4 : tree.getAllInstances();
		assert root.getChildren().contains(child) : root.getChildren();
		assert !root.getChildren().contains(root);
		assert root.getChildren().contains(child2);
		assert !root.getChildren().contains(child3);
		assert child.getChildren().contains(child3);
		assert tree.isTree();
		assert !child.getChildren().contains(child2);
		assert !child.getChildren().contains(child);
		assert !child3.getChildren().contains(child);
		assert !child3.getChildren().contains(child2);
		assert child3.getChildren().isEmpty();
		cache.flush();

	}

	public void testGenealogicTree() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Tree bitree = cache.setTree("Tree", 2);
		Node grandFather = bitree.addRoot("grandFather", 2);
		Node grandMother = bitree.addRoot("grandMother", 2);
		assert grandMother.inheritsFrom(bitree) : grandMother.info();
		Node mother = bitree.addRoot("mother", 2);
		Node father = grandFather.setNode("father", grandMother);
		Node fatherSister = grandFather.setNode("fatherSister", grandMother);
		father.setNode("son", mother);
		assert grandFather.getChildren().contains(father);
		assert grandFather.getChildren().contains(fatherSister);
		// bitree.enableMultiDirectional();
		assert grandMother.getChildren(1).contains(father);
		assert grandMother.getChildren(1).contains(fatherSister);
		// bitree.disableMultiDirectional();
		// assert !bitree.isMultiDirectional() : bitree.getComposites();
		assert grandMother.getChildren().isEmpty() : grandMother.getChildren();
	}

	public void testAnalogicTree() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Tree graphicComponent = cache.setTree("GraphicComponent");
		Node webPage = graphicComponent.addRoot("webPage");
		Node header = webPage.setSubNode("header");
		Node body = webPage.setSubNode("body");
		Node footer = webPage.setSubNode("footer");
		body.setSubNode("message1");
		body.setSubNode("message2");

		Type color = cache.addType("Color");
		Generic red = color.addInstance("Red");
		Generic blue = color.addInstance("Blue");
		Generic yellow = color.addInstance("Yellow");

		Relation graphicComponentColor = graphicComponent.setRelation("GraphicComponentColor", color);
		graphicComponentColor.enablePropertyConstraint();
		graphicComponentColor.enableSingularConstraint();
		assert graphicComponentColor.isPropertyConstraintEnabled();
		assert graphicComponentColor.isSingularConstraintEnabled();

		Generic webPageRed = webPage.bind(graphicComponentColor, red);

		Link bind = header.bind(graphicComponentColor, blue);
		assert bind.inheritsFrom(webPageRed);
		Link bind2 = header.bind(graphicComponentColor, blue);
		assert bind2.inheritsFrom(webPageRed);

		assert bind.isAlive() : bind.info() + bind2.info();

		assert bind == bind2 : bind.info() + " " + bind2.info();
		footer.bind(graphicComponentColor, yellow);

		assert red.equals(body.getLink(graphicComponentColor).getTargetComponent()) : body.getLinks(graphicComponentColor);
		assert "Red".equals(body.getLinks(graphicComponentColor).get(0).getTargetComponent().getValue());
		header.bind(graphicComponentColor, blue);
		cache.flush();
	}

	public void visitTree() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Tree tree = cache.setTree("Tree");
		Node root = tree.addRoot("Root");
		root.setNode("Child");

		root.traverse(new Visitor() {
			@Override
			public void before(Node node) {}
		});

		root.traverse(new Visitor() {
			@Override
			public void after(Node node) {}
		});
	}

	public void testTree2() {
		String directoryPath = System.getenv("HOME") + "/test/snapshot_save" + new Random().nextInt();
		Cache cache = GenericSystem.newCacheOnANewPersistentEngine(directoryPath).start();
		assert cache.getMetaAttribute().isMeta();
		Tree tree = cache.setTree("Tree");
		Node root = tree.addRoot("Root");
		Node child = root.setNode("Child");
		root.setNode("Child2");
		child.setNode("Child3");
		cache.flush();
		cache.getEngine().close();
		cache = GenericSystem.newCacheOnANewPersistentEngine(directoryPath).start();
		assert ((GenericImpl) tree).getDesignTs() == ((GenericImpl) cache.setTree("Tree")).getDesignTs();
	}

	public void testInheritanceTree2() {
		String directoryPath = System.getenv("HOME") + "/test/snapshot_save" + new Random().nextInt();
		Cache cache = GenericSystem.newCacheOnANewPersistentEngine(directoryPath).start();
		Tree tree = cache.setTree("Tree");
		Node root = tree.addRoot("Root");
		Node child = root.setSubNode("Child");
		root.setSubNode("Child2");
		child.setSubNode("Child3");
		cache.flush();
		cache.getEngine().close();

		cache = GenericSystem.newCacheOnANewPersistentEngine(directoryPath).start();
		assert ((GenericImpl) tree).getDesignTs() == ((GenericImpl) cache.setTree("Tree")).getDesignTs();
	}
}
