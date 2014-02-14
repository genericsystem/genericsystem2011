package org.genericsystem.core;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import org.genericsystem.core.Statics.OrderedSupers;
import org.genericsystem.core.UnsafeGList.Components;
import org.genericsystem.core.UnsafeGList.Supers;
import org.genericsystem.core.UnsafeGList.UnsafeComponents;
import org.genericsystem.iterator.AbstractSelectableLeafIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nicolas Feybesse
 * 
 */
class UnsafeVertex {

	protected static Logger log = LoggerFactory.getLogger(UnsafeVertex.class);

	private final HomeTreeNode homeTreeNode;

	private Supers supers;

	private final UnsafeComponents components;

	public UnsafeVertex(HomeTreeNode homeTreeNode, Supers supers, UnsafeComponents components) {
		assert homeTreeNode != null;
		assert supers != null;
		assert components != null;
		this.homeTreeNode = homeTreeNode;
		this.supers = supers;
		this.components = components;
	}

	UnsafeVertex(HomeTreeNode homeTreeNode, Supers supers, UnsafeComponents components, boolean respectSupers) {
		assert homeTreeNode != null;
		assert supers != null;
		assert components != null;
		this.homeTreeNode = homeTreeNode;
		this.supers = supers;
		this.components = components;
		this.supers = toExtendedSupers(respectSupers);
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

	public boolean isConcrete() {
		return Statics.CONCRETE == metaLevel();
	}

	public boolean isStructural() {
		return Statics.STRUCTURAL == metaLevel();
	}

	public boolean isMeta() {
		return Statics.META == metaLevel();
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

	private Supers toExtendedSupers(final boolean respectSupers) {
		final Engine engine = ((GenericImpl) getMeta()).getEngine();
		Iterator<Generic> iterator = new AbstractSelectableLeafIterator(engine) {
			@Override
			public boolean isSelected(Generic candidate) {
				return ((GenericImpl) candidate).isSuperOf(UnsafeVertex.this);
			}
		};
		Set<Generic> set = new TreeSet<>();
		while (iterator.hasNext())
			set.add(iterator.next());
		Supers result = new Supers(set);
		return respectSupers ? new OrderedSupers(result, supers().toArray()).toSupers() : result;
	}

	public static class Vertex extends UnsafeVertex {

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
