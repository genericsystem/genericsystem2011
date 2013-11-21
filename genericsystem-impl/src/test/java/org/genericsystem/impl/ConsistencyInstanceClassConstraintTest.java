package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.exception.InstanceClassConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Node;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Tree;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class ConsistencyInstanceClassConstraintTest extends AbstractTest {

	public void consistencyTypeOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		vehicle.newInstance("myVehicle");
		vehicle.setConstraintClass(String.class);
	}

	public void consistencyTypeKO() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type vehicle = cache.addType("Vehicle");
		vehicle.newInstance(123);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				vehicle.setConstraintClass(String.class);
			}
		}.assertIsCausedBy(InstanceClassConstraintViolationException.class);
	}

	public void consistencyAttributeOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Generic myVehicle = vehicle.newInstance("myVehicle");
		Attribute power = vehicle.setAttribute("power");
		myVehicle.setValue(power, 123);
		power.setConstraintClass(Integer.class);
	}

	public void consistencyAttributeKO() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Generic myVehicle = vehicle.newInstance("myVehicle");
		final Attribute power = vehicle.setAttribute("power");
		myVehicle.setValue(power, "123");
		new RollbackCatcher() {
			@Override
			public void intercept() {
				power.setConstraintClass(Integer.class);
			}
		}.assertIsCausedBy(InstanceClassConstraintViolationException.class);
	}

	public void consistencyRelationOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Generic myVehicle = vehicle.newInstance("myVehicle");
		Type color = cache.addType("Color");
		Generic red = color.newInstance("red");
		Relation vehicleColor = vehicle.addRelation("vehicleColor", color);
		myVehicle.setLink(vehicleColor, 80, red);
		vehicleColor.setConstraintClass(Integer.class);
	}

	public void consistencyRelationKO() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.addType("Vehicle");
		Generic myVehicle = vehicle.newInstance("myVehicle");
		Type color = cache.addType("Color");
		Generic red = color.newInstance("red");
		final Relation vehicleColor = vehicle.addRelation("vehicleColor", color);
		assert vehicleColor.isStructural();
		myVehicle.setLink(vehicleColor, "link", red);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				vehicleColor.setConstraintClass(Integer.class);
			}
		}.assertIsCausedBy(InstanceClassConstraintViolationException.class);

	}

	public void consistencyTreeOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Tree tree = cache.setTree("Tree");
		Node root = tree.newRoot(123);
		root.setNode(456);
		tree.setConstraintClass(Integer.class);
	}

	public void consistencyTreeKO() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Tree tree = cache.setTree("Tree");
		Node root = tree.newRoot(123);
		root.setNode("Child");
		new RollbackCatcher() {
			@Override
			public void intercept() {
				tree.setConstraintClass(Integer.class);
			}
		}.assertIsCausedBy(InstanceClassConstraintViolationException.class);
	}

	public void consistencyTreeKO2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Tree tree = cache.setTree("Tree");
		Node root = tree.newRoot("Root");
		root.setNode("Child");
		new RollbackCatcher() {
			@Override
			public void intercept() {
				tree.setConstraintClass(Integer.class);
			}
		}.assertIsCausedBy(InstanceClassConstraintViolationException.class);
	}

	public void consistencyInheritanceTreeOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Tree tree = cache.setTree("Tree");
		Node root = tree.newRoot(123);
		root.setSubNode(456);
		tree.setConstraintClass(Integer.class);
	}

	public void consistencyInheritanceTreeKO() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Tree tree = cache.setTree("Tree");
		Node root = tree.newRoot(123);
		root.setNode("Father");
		root.setSubNode("Child");
		new RollbackCatcher() {
			@Override
			public void intercept() {
				tree.setConstraintClass(Integer.class);
			}
		}.assertIsCausedBy(InstanceClassConstraintViolationException.class);
	}

	public void consistencyInheritanceTreeKO2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Tree tree = cache.setTree("Tree");
		Node root = tree.newRoot("Root");
		root.setSubNode("Child");
		new RollbackCatcher() {
			@Override
			public void intercept() {
				tree.setConstraintClass(Integer.class);
			}
		}.assertIsCausedBy(InstanceClassConstraintViolationException.class);
	}

}
