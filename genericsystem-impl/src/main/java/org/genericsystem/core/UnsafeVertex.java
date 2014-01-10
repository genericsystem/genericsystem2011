package org.genericsystem.core;

import org.genericsystem.core.UnsafeGList.Components;
import org.genericsystem.core.UnsafeGList.Supers;
import org.genericsystem.core.UnsafeGList.UnsafeComponents;

/**
 * @author Nicolas Feybesse
 * 
 */
class UnsafeVertex {

	private HomeTreeNode homeTreeNode;

	private Supers supers;

	private UnsafeComponents components;

	public UnsafeVertex(HomeTreeNode homeTreeNode, Supers supers, UnsafeComponents components) {
		assert homeTreeNode != null;
		assert supers != null;
		assert components != null;
		this.homeTreeNode = homeTreeNode;
		this.supers = supers;
		this.components = components;
	}

	public HomeTreeNode getHomeTreeNode() {
		return homeTreeNode;
	}

	public Supers getSupers() {
		return supers;
	}

	public UnsafeComponents getComponents() {
		return components;
	}

	public int getMetaLevel() {
		return homeTreeNode.getMetaLevel();
	}

	static class Vertex extends UnsafeVertex {

		public Vertex(Generic generic, UnsafeVertex uVertex) {
			super(uVertex.getHomeTreeNode(), uVertex.getSupers(), new Components(generic, uVertex.getComponents()));
		}

		@Override
		public Components getComponents() {
			return (Components) super.getComponents();
		}

	}

}
