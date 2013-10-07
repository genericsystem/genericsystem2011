package org.genericsystem.myadmin.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.Structural;
import org.genericsystem.core.StructuralImpl;
import org.genericsystem.exception.NotRemovableException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Type;
import org.genericsystem.myadmin.util.GsMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@SessionScoped
public class GenericBean implements Serializable {

	private static final long serialVersionUID = 2108715680116264876L;
	protected static Logger log = LoggerFactory.getLogger(GenericBean.class);
	@Inject
	private transient Cache cache;

	@Inject
	private GsMessages messages;

	@Inject
	private GenericTreeBean genericTreeBean;

	private List<StructuralWrapper> structuralWrappers = new ArrayList<>();

	public void newType(String newValue) {
		cache.newType(newValue);
		messages.info("createRootType", newValue);
	}

	public void newSubType(String newValue) {
		((Type) genericTreeBean.getSelectedTreeNodeGeneric()).newSubType(newValue);
		messages.info("createSubType", newValue, genericTreeBean.getSelectedTreeNodeGeneric().getValue());
	}

	public void setAttribute(String newValue) {
		((Type) genericTreeBean.getSelectedTreeNodeGeneric()).setAttribute(newValue);
		messages.info("createRootAttribute", newValue, genericTreeBean.getSelectedTreeNodeGeneric().getValue());
	}

	public void addProperty(String key, String value) {
		((Type) genericTreeBean.getSelectedTreeNodeGeneric()).getPropertiesMap().put(key, value);
		messages.info("createRootProperty", key, value);
	}

	public void addContraint(String key, String value) {
		((Type) genericTreeBean.getSelectedTreeNodeGeneric()).getContraintsMap().put(key, value);
		messages.info("createRootProperty", key, value);
	}

	public void addSystemProperty(String key, String value) {
		((Type) genericTreeBean.getSelectedTreeNodeGeneric()).getSystemPropertiesMap().put(key, value);
		messages.info("createRootProperty", key, value);
	}

	public void newInstance(String newValue) {
		((Type) genericTreeBean.getSelectedTreeNodeGeneric()).newInstance(newValue);
		messages.info("createRootInstance", newValue, genericTreeBean.getSelectedTreeNodeGeneric().getValue());
	}

	public void addValue(Attribute attribute, String newValue) {
		Generic currentInstance = genericTreeBean.getSelectedTreeNodeGeneric();
		currentInstance.setValue(attribute, newValue);
		messages.info("addValue", newValue, attribute, currentInstance);
	}

	public void remove(Holder holder) {
		genericTreeBean.getSelectedTreeNodeGeneric().removeHolder(holder);
		messages.info("remove", holder);
	}

	public String delete() {
		Generic generic = genericTreeBean.getSelectedTreeNodeGeneric();
		generic.remove();
		messages.info("remove", generic);
		genericTreeBean.setSelectedTreeNode(genericTreeBean.getSelectedTreeNode().getParent());
		return "";
	}

	public void removeProperty(Entry<Serializable, Serializable> entry) {
		removeEntry(genericTreeBean.getSelectedTreeNodeGeneric().getPropertiesMap(), entry);
	}

	public void removeContraint(Entry<Serializable, Serializable> entry) {
		removeEntry(genericTreeBean.getSelectedTreeNodeGeneric().getContraintsMap(), entry);

	}

	public void removeSystemProperty(Entry<Serializable, Serializable> entry) {
		removeEntry(genericTreeBean.getSelectedTreeNodeGeneric().getSystemPropertiesMap(), entry);
	}

	private void removeEntry(Map<Serializable, Serializable> map, Entry<Serializable, Serializable> entry) {
		try {
			map.remove(entry.getKey());
			messages.info("remove", entry.getKey());
		} catch (NotRemovableException e) {
			messages.info("cannotremove", e.getMessage());
		}
	}

	/**
	 * Create and return structurals created on attributes of selected tree node.
	 * 
	 * @return list of structurals.
	 */
	public List<StructuralImpl> getStructurals() {
		List<StructuralImpl> structurals = new ArrayList<>();
		for (Attribute attribute : genericTreeBean.<Type> getSelectedTreeNodeGeneric().getAttributes())
			structurals.add(new StructuralImpl(attribute, genericTreeBean.getSelectedTreeNodeGeneric().getBasePos(attribute)));
		return structurals;
	}

	/*
	@SuppressWarnings("unchecked")
	public List<Entry<Serializable, Serializable>> getMapEntryList(MapProvider mapProvider) {
		return (List<Entry<Serializable, Serializable>>) mapProvider.getMap(genericTreeBean.getSelectedTreeNodeGeneric()).entrySet();
	}
	 */

	public class StructuralWrapper {
		private Structural structural;
		private boolean readPhantoms;

		public StructuralWrapper(Structural structural) {
			this.structural = structural;
		}

		public Structural getStructural() {
			return structural;
		}

		public void setStructural(Structural structural) {
			this.structural = structural;
		}

		public boolean isReadPhantoms() {
			return readPhantoms;
		}

		public void setReadPhantoms(boolean readPhantoms) {
			this.readPhantoms = readPhantoms;
		}
	}

	public List<Holder> getHolders(StructuralWrapper structuralWrapper) {
		return ((Type) genericTreeBean.getSelectedTreeNodeGeneric()).getHolders(structuralWrapper.getStructural().getAttribute(), structuralWrapper.getStructural().getPosition(), structuralWrapper.isReadPhantoms());
	}

	public List<Generic> getOtherTargets(Holder holder) {
		return genericTreeBean.getSelectedTreeNodeGeneric().getOtherTargets(holder);
	}

	public boolean isSingular(Structural structural) {
		return structural.getAttribute().isSingularConstraintEnabled();
	}

	public String getHolderStyle(Holder holder) {
		return !holder.getBaseComponent().equals(genericTreeBean.getSelectedTreeNodeGeneric()) ? "italic" : (isPhantom(holder) ? "phantom" : "");
	}

	public boolean hasValues(Attribute attribute) {
		return !genericTreeBean.getSelectedTreeNodeGeneric().getValues(attribute).isEmpty();
	}

	public boolean isPhantom(Holder holder) {
		return holder.getValue() == null;
	}

	public boolean isMeta() {
		return genericTreeBean.getSelectedTreeNodeGeneric().isMeta();
	}

}
