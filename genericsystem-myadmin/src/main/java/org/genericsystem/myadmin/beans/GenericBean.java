package org.genericsystem.myadmin.beans;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.generic.Type;
import org.genericsystem.myadmin.util.GsMessages;

@Named
@SessionScoped
public class GenericBean implements Serializable {

	private static final long serialVersionUID = 2108715680116264876L;

	public enum View {
		SUPERS, INHERITINGS, COMPONENTS, COMPOSITES, ATTRIBUTES;
	}

	public static final View DEFAULT_VIEW = View.INHERITINGS;

	@Inject
	private transient Cache cache;

	@Inject
	private GsMessages messages;

	private Generic genericEdited;

	@PostConstruct
	public void init() {
		genericEdited = getGenericEditedByDefault();
	}

	private Generic getGenericEditedByDefault() {
		return cache.getEngine();
	}

	@GenericEdited
	@Produces
	public Generic getGenericEdited() {
		return genericEdited;
	}

	public String getGenericEditedValue() {
		return genericEdited.toString();
	}

	public void newType(String newValue) {
		cache.newType(newValue);
		messages.info("createRootType", newValue);
	}

	public void newSubType(String newValue) {
		((Type) genericEdited).newSubType(newValue);
		messages.info("createSubType", newValue, genericEdited.getValue());
	}

	public void setAttribute(String newValue) {
		((Type) genericEdited).setAttribute(newValue);
		messages.info("createRootAttribute", newValue, genericEdited.getValue());
	}

	public void setProperty(String newValue) {
		((Type) genericEdited).setProperty(newValue);
		messages.info("createRootProperty", newValue, genericEdited.getValue());
	}

	public void newInstance(String newValue) {
		((Type) genericEdited).newInstance(newValue);
		messages.info("createRootInstance", newValue, genericEdited.getValue());
	}

	public String delete() {
		genericEdited.remove();
		messages.info("remove", genericEdited);
		genericEdited = getGenericEditedByDefault();
		return "";
	}

	public boolean genericEditedIsConcrete() {
		return genericEdited.isConcrete();
	}

	@Retention(RetentionPolicy.CLASS)
	@Target({ ElementType.FIELD, ElementType.METHOD })
	@Qualifier
	public @interface GenericEdited {
	}

	public static class ModifyViewEvent {

		private final View view;

		public ModifyViewEvent(View view) {
			this.view = view;
		}

		public View getView() {
			return view;
		}

	}

	// @Inject
	// private TreeBean genericTreeBean;

	// private List<StructuralWrapper> structuralWrappers = new ArrayList<>();
	//

	//
	// public void addProperty(String key, String value) {
	// ((Type) genericTreeBean.getSelectedTreeNodeGeneric()).getPropertiesMap().put(key, value);
	// messages.info("createRootProperty", key, value);
	// }
	//
	// public void addContraint(String key, String value) {
	// ((Type) genericTreeBean.getSelectedTreeNodeGeneric()).getContraintsMap().put(key, value);
	// messages.info("createRootProperty", key, value);
	// }
	//
	// public void addSystemProperty(String key, String value) {
	// ((Type) genericTreeBean.getSelectedTreeNodeGeneric()).getSystemPropertiesMap().put(key, value);
	// messages.info("createRootProperty", key, value);
	// }
	//
	// public void newInstance(String newValue) {
	// ((Type) genericTreeBean.getSelectedTreeNodeGeneric()).newInstance(newValue);
	// messages.info("createRootInstance", newValue, genericTreeBean.getSelectedTreeNodeGeneric().getValue());
	// }
	//
	// public void addValue(Attribute attribute, String newValue) {
	// Generic currentInstance = genericTreeBean.getSelectedTreeNodeGeneric();
	// currentInstance.setValue(attribute, newValue);
	// messages.info("addValue", newValue, attribute, currentInstance);
	// }
	//
	// public void remove(Holder holder) {
	// genericTreeBean.getSelectedTreeNodeGeneric().removeHolder(holder);
	// messages.info("remove", holder);
	// }
	//
	// public String delete() {
	// Generic generic = genericTreeBean.getSelectedTreeNodeGeneric();
	// generic.remove();
	// messages.info("remove", generic);
	// genericTreeBean.setSelectedTreeNode(genericTreeBean.getSelectedTreeNode().getParent());
	// return "";
	// }
	//
	// public void removeProperty(Entry<Serializable, Serializable> entry) {
	// removeEntry(genericTreeBean.getSelectedTreeNodeGeneric().getPropertiesMap(), entry);
	// }
	//
	// public void removeContraint(Entry<Serializable, Serializable> entry) {
	// removeEntry(genericTreeBean.getSelectedTreeNodeGeneric().getContraintsMap(), entry);
	//
	// }
	//
	// public void removeSystemProperty(Entry<Serializable, Serializable> entry) {
	// removeEntry(genericTreeBean.getSelectedTreeNodeGeneric().getSystemPropertiesMap(), entry);
	// }
	//
	// private void removeEntry(Map<Serializable, Serializable> map, Entry<Serializable, Serializable> entry) {
	// try {
	// map.remove(entry.getKey());
	// messages.info("remove", entry.getKey());
	// } catch (NotRemovableException e) {
	// messages.info("cannotremove", e.getMessage());
	// }
	// }
	//
	// public List<StructuralWrapper> getStructurals() {
	// log.info("" + Cache.class.isInstance(cache));
	// List<StructuralWrapper> list = new ArrayList<>();
	// for (Structural structural : genericTreeBean.getSelectedTreeNodeGeneric().getStructurals())
	// if (!structural.getAttribute().inheritsFrom(cache.find(AbstractMapProvider.class)))
	// list.add(getStructuralWrapper(structural));
	// structuralWrappers = list;
	// return list;
	// }
	//
	// private StructuralWrapper getStructuralWrapper(Structural structural) {
	// for (StructuralWrapper old : structuralWrappers)
	// if (old.getStructural().equals(structural))
	// return old;
	// return new StructuralWrapper(structural);
	// }
	//
	// public class StructuralWrapper {
	// private Structural structural;
	// private boolean readPhantoms;
	//
	// public StructuralWrapper(Structural structural) {
	// this.structural = structural;
	// }
	//
	// public Structural getStructural() {
	// return structural;
	// }
	//
	// public void setStructural(Structural structural) {
	// this.structural = structural;
	// }
	//
	// public boolean isReadPhantoms() {
	// return readPhantoms;
	// }
	//
	// public void setReadPhantoms(boolean readPhantoms) {
	// this.readPhantoms = readPhantoms;
	// }
	// }
	//
	// public List<Holder> getHolders(StructuralWrapper structuralWrapper) {
	// return ((Type) genericTreeBean.getSelectedTreeNodeGeneric()).getHolders(structuralWrapper.getStructural().getAttribute(), structuralWrapper.getStructural().getPosition(), structuralWrapper.isReadPhantoms());
	// }
	//
	// public List<Generic> getOtherTargets(Holder holder) {
	// return genericTreeBean.getSelectedTreeNodeGeneric().getOtherTargets(holder);
	// }
	//
	// @SuppressWarnings("unchecked")
	// public List<Entry<Serializable, Serializable>> getPropertiesMap() {
	// return (List<Entry<Serializable, Serializable>>) genericTreeBean.getSelectedTreeNodeGeneric().getPropertiesMap().entrySet();
	// }
	//
	// @SuppressWarnings("unchecked")
	// public List<Entry<Serializable, Serializable>> getContraintsMap() {
	// return (List<Entry<Serializable, Serializable>>) genericTreeBean.getSelectedTreeNodeGeneric().getContraintsMap().entrySet();
	// }
	//
	// @SuppressWarnings("unchecked")
	// public List<Entry<Serializable, Serializable>> getSystemPropertiesMap() {
	// return (List<Entry<Serializable, Serializable>>) genericTreeBean.getSelectedTreeNodeGeneric().getSystemPropertiesMap().entrySet();
	// }
	//
	// // TODO in GS CORE
	// // public boolean isValue(Generic generic) {
	// // return generic.isConcrete() && generic.isAttribute();
	// // }
	//
	// public boolean isSingular(Structural structural) {
	// return structural.getAttribute().isSingularConstraintEnabled();
	// }
	//
	// public String getHolderStyle(Holder holder) {
	// return !holder.getBaseComponent().equals(genericTreeBean.getSelectedTreeNodeGeneric()) ? "italic" : (isPhantom(holder) ? "phantom" : "");
	// }
	//
	// public boolean hasValues(Attribute attribute) {
	// return !genericTreeBean.getSelectedTreeNodeGeneric().getValues(attribute).isEmpty();
	// }
	//
	// public boolean isPhantom(Holder holder) {
	// return holder.getValue() == null;
	// }
	//
	// public boolean isMeta() {
	// return genericTreeBean.getSelectedTreeNodeGeneric().isMeta();
	// }

}
