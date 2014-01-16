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

	private Generic meta;

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

	public UnsafeVertex(HomeTreeNode homeTreeNode, Generic meta, Supers supers, UnsafeComponents components) {
		assert homeTreeNode != null;
		// assert meta != null;
		assert supers != null;
		assert components != null;
		this.homeTreeNode = homeTreeNode;
		this.meta = meta;
		this.supers = supers;
		this.components = components;
	}

	public HomeTreeNode homeTreeNode() {
		return homeTreeNode;
	}

	public Generic getMeta() {
		return meta;
	}

	public Supers supers() {
		return supers;
	}

	public UnsafeComponents components() {
		return components;
	}

	public int metaLevel() {
		return homeTreeNode.getMetaLevel();
	}

	UnsafeVertex truncateComponent(int pos) {
		return new UnsafeVertex(homeTreeNode(), getMeta(), supers(), Statics.truncate(pos, components()));
	}

	static class Vertex extends UnsafeVertex {

		public Vertex(Generic generic, UnsafeVertex uVertex) {
			super(uVertex.homeTreeNode(), uVertex.getMeta(), uVertex.supers(), new Components(generic, uVertex.components()));
		}

		@Override
		public Components components() {
			return (Components) super.components();
		}

		public boolean equiv(Vertex vertex) {
			return homeTreeNode().equals(vertex.homeTreeNode()) && supers().equals(vertex.supers()) && components().equals(vertex.components());
		}

	}

}
