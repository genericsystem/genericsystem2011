package org.genericsystem.core;

import org.genericsystem.core.UnsafeGList.Components;
import org.genericsystem.core.UnsafeGList.Supers;
import org.genericsystem.core.UnsafeGList.UnsafeComponents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nicolas Feybesse
 * 
 */
class UnsafeVertex {

	protected static Logger log = LoggerFactory.getLogger(UnsafeVertex.class);

	private final HomeTreeNode homeTreeNode;

	private final Supers supers;

	private final UnsafeComponents components;

	public UnsafeVertex(HomeTreeNode homeTreeNode, Supers supers, UnsafeComponents components) {
		assert homeTreeNode != null;
		assert supers != null;
		assert components != null;
		this.homeTreeNode = homeTreeNode;
		this.supers = supers;
		this.components = components;
	}

	public HomeTreeNode homeTreeNode() {
		return homeTreeNode;
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

	@SuppressWarnings("unchecked")
	public <T extends Generic> T getMeta() {
		for (Generic superGeneric : supers())
			if (((GenericImpl) superGeneric).homeTreeNode().equals(homeTreeNode.metaNode))
				return (T) superGeneric;
		for (Generic superGeneric : supers())
			if (((GenericImpl) superGeneric).homeTreeNode().inheritsFrom(homeTreeNode.metaNode))
				return superGeneric.getMeta();
		throw new IllegalStateException();
	}

	UnsafeVertex truncateComponent(int pos) {
		return new UnsafeVertex(homeTreeNode(), supers(), Statics.truncate(pos, components()));
	}

	public void log() {
		log.info(info());
	}

	public String info() {
		String s = "\n******************************" + System.identityHashCode(this) + "******************************\n";
		s += " Name        : " + homeTreeNode.getValue() + "\n";
		s += " MetaLevel   : " + homeTreeNode.getMetaLevel() + "\n";
		// s += " Category    : " + getCategoryString() + "\n";
		s += " Class       : " + getClass().getSimpleName() + "\n";
		s += "**********************************************************************\n";
		for (Generic superGeneric : supers())
			s += " Super       : " + superGeneric + " (" + System.identityHashCode(superGeneric) + ")\n";
		for (Generic component : components())
			s += " Component   : " + component + " (" + System.identityHashCode(component) + ")\n";
		s += "**********************************************************************\n";
		return s;
	}

	static class Vertex extends UnsafeVertex {

		public Vertex(Generic generic, UnsafeVertex uVertex) {
			super(uVertex.homeTreeNode(), uVertex.supers(), new Components(generic, uVertex.components()));
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
