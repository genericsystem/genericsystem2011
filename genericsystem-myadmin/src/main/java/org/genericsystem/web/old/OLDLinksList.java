package org.genericsystem.web.old;
//package org.genericsystem.web.beans;
//
//import java.io.Serializable;
//import java.util.Iterator;
//import java.util.List;
//
//import javax.enterprise.context.ConversationScoped;
//import javax.inject.Inject;
//import javax.inject.Named;
//
//import org.genericsystem.core.Cache;
//import org.genericsystem.core.Generic;
//import org.genericsystem.generic.Link;
//import org.genericsystem.generic.Relation;
//import org.genericsystem.generic.Type;
//import org.genericsystem.web.util.AbstractSequentialList;
//import org.slf4j.Logger;
//
//@Named
//@ConversationScoped
//public class LinksList implements Serializable {
//
//	private static final long serialVersionUID = -5824712114122634588L;
//
//	@Inject
//	private Cache cacheContext;
//
//	@Inject
//	private TypesManager typesManager;
//
//	// private Generic currentInstance;
//
//	@Inject
//	private Logger log;
//
//	@Inject
//	private SelectedInstanceManager sim;
//
//	@Inject
//	private LinkListForm formulaire;
//
//	public List<Relation> makeRelationList() {
//
//		final Type selectedType = typesManager.getSelectedType();
//		// if (selectedType == null) {
//		return null;
//		// }
//		//
//		// return new AbstractSequentialList<Relation>() {
//		// @Override
//		// public Iterator<Relation> iterator() {
//		// return selectedType.getStructurals().iterator();
//		// }
//		// };
//
//	}
//
//	public List<Link> makeLinkListFromRelation(final Relation relation) {
//
//		final Generic selectedInstance = sim.getSelectedInstance();
//		if (selectedInstance == null) {
//			return null;
//		}
//
//		return new AbstractSequentialList<Link>() {
//			@Override
//			public Iterator<Link> iterator() {
//				return null;// selectedInstance.getLinks(relation).iterator();
//			}
//		};
//	}
//
//	public List<Generic> makeInstanceListFromType(final Type type) {
//		List<Generic> list = new AbstractSequentialList<Generic>() {
//			@Override
//			public Iterator<Generic> iterator() {
//				return type.getInstances().iterator();
//			}
//		};
//		return list;
//	}
//
//	public Generic targetFromRelation(Relation relation) {
//		return relation.getTargetComponent();
//	}
//
//	// public boolean hasTarget(Relation relation) {
//	// return relation.getTargetComponent() != null;
//	// }
//
//	public Generic targetFromLink(Link l) {
//		return l.getTargetComponent();
//	}
//
//	public List<Generic> getTargetsInstanceSelectItems(Relation relation) {
//		return makeInstanceListFromType((Type) relation.getTargetComponent());
//	}
//
//	public void addLink(Relation relation) {
//
//		String value = formulaire.getWrapperAdd(relation).getValue();
//		if (value == null) {
//			return;
//		}
//
//		Generic target = formulaire.getWrapperAdd(relation).getTarget();
//
//		if (target == null) {
//			sim.getSelectedInstance().addValue(relation, value);
//		} else {
//			sim.getSelectedInstance().addLink(relation, value, target);
//		}
//	}
//
//	public void addRelation() {
//
//		Type selectedType = typesManager.getSelectedType();
//
//		log.info("ADD RELATION SUR " + selectedType);
//		String value = formulaire.getWrapperAdd(selectedType).getValue();
//		if (value == null) {
//			return;
//		}
//
//		selectedType.addAttribute(value);
//
//		// Generic target = formulaire.getWrapperAdd(relation).getTarget();
//		//
//		// if (target == null) {
//		// sim.getSelectedInstance().addValue(relation, value);
//		// } else {
//		// sim.getSelectedInstance().addLink(relation, value, target);
//		// }
//	}
//
//	public void modifyValue(Link link, Relation relation) {
//		String value = formulaire.getWrapperModify(link).getValue();
//		if (value == null) {
//			return;
//		}
//
//		Generic target = formulaire.getWrapperModify(link).getTarget();
//		link.remove();
//
//		if (target == null) {
//			sim.getSelectedInstance().addValue(relation, value);
//		} else {
//			sim.getSelectedInstance().addLink(relation, value, target);
//		}
//
//		// formulaire.clear();
//	}
//
//	public List<Generic> getAllType() {
//		return new AbstractSequentialList<Generic>() {
//			@Override
//			public Iterator<Generic> iterator() {
//				return cacheContext.getEngine().getAllSubTypes().iterator();
//			}
//		};
//	}
//
//	public void delete(Generic generic) {
//		generic.remove();
//	}
//
//	public Generic getTargetComponent(Generic generic) {
//		if (generic instanceof Link) {
//			Link l = (Link) generic;
//			return l.getTargetComponent();
//		}
//		return null;
//	}
//
//	public boolean hasTarget(Generic generic) {
//		if (generic instanceof Link) {
//			return ((Link) generic).getTargetComponent() != null;
//		}
//		return false;
//	}
//
//	public List<Generic> getTargetItems(Relation relation) {
//		final Type type = relation.getTargetComponent();
//		return new AbstractSequentialList<Generic>() {
//			@Override
//			public Iterator<Generic> iterator() {
//				return type.getInstances().iterator();
//			}
//		};
//	}
//
//	public List<Generic> getCompositionComponentsList(final Generic generic) {
//
//		// if (generic == null) {
//		log.info("GENERIC NULL DANS GET COMPOSITION COMPOENENT");
//		return null;
//		// }
//		//
//		// return new AbstractSequentialList<Generic>() {
//		// @Override
//		// public Iterator<Generic> iterator() {
//		// return generic.getCompositionComponents().iterator();
//		// }
//		// };
//	}
//
//	public List<Generic> getAllInstances(final Type type) {
//		return new AbstractSequentialList<Generic>() {
//			@Override
//			public Iterator<Generic> iterator() {
//				return type.getAllInstances().iterator();
//			}
//		};
//	}
//
//	public void newInstance(Generic type) {
//
//		String value = formulaire.getWrapperAdd(type).getValue();
//		if (value == null) {
//			return;
//		}
//
//		// if (!type.getCompositionComponents().iterator().hasNext()) {
//		// type.newInstance(value);
//		// } else {
//		Generic target = formulaire.getWrapperAdd(type).getTarget();
//		type.newInstance(value, sim.getSelectedInstance(), target);
//		// }
//	}
//
//	//
//	// public List<Generic> getTargetItems(Link link) {
//	// // return getTargetItems(link.getRelation());
//	// return new ArrayList<Generic>();
//	// }
//
// }
