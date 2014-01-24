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

	public void log() {
		log.info(info());
	}

	public String info() {
		String s = "\n******************************" + System.identityHashCode(this) + "******************************\n";
		s += " Name        : " + homeTreeNode.getValue() + "\n";
		s += " Meta        : " + meta + " (" + System.identityHashCode(getMeta()) + ")\n";
		s += " MetaLevel   : " + homeTreeNode.getMetaLevel() + "\n";
		// s += " Category    : " + getCategoryString() + "\n";
		s += " Class       : " + getClass().getSimpleName() + "\n";
		s += "**********************************************************************\n";
		for (Generic superGeneric : supers())
			s += " Super       : " + superGeneric + " (" + System.identityHashCode(superGeneric) + ")\n";
		for (Generic component : components())
			s += " Component   : " + component + " (" + System.identityHashCode(component) + ")\n";
		s += "**********************************************************************\n";

		// for (Attribute attribute : getAttributes())
		// if (!(attribute.getValue() instanceof Class) /* || !Constraint.class.isAssignableFrom((Class<?>) attribute.getValue()) */) {
		// s += ((GenericImpl) attribute).getCategoryString() + "   : " + attribute + " (" + System.identityHashCode(attribute) + ")\n";
		// for (Holder holder : getHolders(attribute))
		// s += "                          ----------> " + ((GenericImpl) holder).getCategoryString() + " : " + holder + "\n";
		// }
		// s += "**********************************************************************\n";
		// s += "design date : " + new SimpleDateFormat(Statics.LOG_PATTERN).format(new Date(getDesignTs() / Statics.MILLI_TO_NANOSECONDS)) + "\n";
		// s += "birth date  : " + new SimpleDateFormat(Statics.LOG_PATTERN).format(new Date(getBirthTs() / Statics.MILLI_TO_NANOSECONDS)) + "\n";
		// s += "death date  : " + new SimpleDateFormat(Statics.LOG_PATTERN).format(new Date(getDeathTs() / Statics.MILLI_TO_NANOSECONDS)) + "\n";
		// s += "**********************************************************************\n";

		return s;
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

		public boolean equivByMeta(Vertex vertex) {
			return homeTreeNode().equals(vertex.homeTreeNode()) && getMeta().equals(vertex.getMeta()) && components().equals(vertex.components());
		}

	}

}
