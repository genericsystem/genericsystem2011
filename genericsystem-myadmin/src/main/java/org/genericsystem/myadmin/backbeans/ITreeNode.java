package org.genericsystem.myadmin.backbeans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.generic.Type;

public interface ITreeNode {

	ITreeNode getParent();

	List<ITreeNode> getChildrens(Cache cache);

	String getValue();

	boolean isReadOnly();

	static abstract class AbstractReadOnlyTreeNode implements ITreeNode {

		private GenericTreeNode parent;

		public AbstractReadOnlyTreeNode(GenericTreeNode parent) {
			this.parent = parent;
		}

		@Override
		public GenericTreeNode getParent() {
			return parent;
		}

		Generic getParentGeneric() {
			return parent.getGeneric();
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}
	}

	public static class GenericTreeNode implements ITreeNode {

		private final List<ITreeNode> childrens = Arrays.<ITreeNode> asList(new AbstractReadOnlyTreeNode[] { new InstancesTreeNode(this), new InheritingsTreeNode(this), new CompositesTreeNode(this) });

		private AbstractReadOnlyTreeNode parent;

		private Generic generic;

		public GenericTreeNode(AbstractReadOnlyTreeNode parent, Generic generic) {
			this.parent = parent;
			this.generic = generic;
		}

		@Override
		public AbstractReadOnlyTreeNode getParent() {
			return parent;
		}

		public Generic getGeneric() {
			return generic;
		}

		@Override
		public List<ITreeNode> getChildrens(Cache cache) {
			return childrens;
		}

		@Override
		public String getValue() {
			return generic.toString();
		}

		@Override
		public boolean isReadOnly() {
			return generic.isMeta();
		}

	}

	static class InstancesTreeNode extends AbstractReadOnlyTreeNode {

		public InstancesTreeNode(GenericTreeNode parent) {
			super(parent);
		}

		@Override
		public List<ITreeNode> getChildrens(Cache cache) {
			List<ITreeNode> list = new ArrayList<>();
			for (Generic g : ((Type) getParentGeneric()).getInstances(cache))
				list.add(new GenericTreeNode(this, g));
			return list;
		}

		@Override
		public String getValue() {
			return "instances";
		}

	}

	static class InheritingsTreeNode extends AbstractReadOnlyTreeNode {

		public InheritingsTreeNode(GenericTreeNode parent) {
			super(parent);
		}

		@Override
		public List<ITreeNode> getChildrens(Cache cache) {
			List<ITreeNode> list = new ArrayList<>();
			for (Generic g : getParentGeneric().getInheritings(cache))
				list.add(new GenericTreeNode(this, g));
			return list;
		}

		@Override
		public String getValue() {
			return "inheritings";
		}

	}

	static class CompositesTreeNode extends AbstractReadOnlyTreeNode {

		public CompositesTreeNode(GenericTreeNode parent) {
			super(parent);
		}

		@Override
		public List<ITreeNode> getChildrens(Cache cache) {
			List<ITreeNode> list = new ArrayList<>();
			for (Generic g : getParentGeneric().getComposites(cache))
				list.add(new GenericTreeNode(this, g));
			return list;
		}

		@Override
		public String getValue() {
			return "composites";
		}

	}
}
