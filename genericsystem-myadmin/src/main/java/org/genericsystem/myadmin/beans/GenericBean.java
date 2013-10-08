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

	/**
	 * Wrapper for structural.
	 * 
	 * @author middleware
	 */
	public class StructuralWrapper {

		private Structural structural;			// structural
		private boolean readPhantoms;			// flag read phantoms

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

	/**
	 * Creates new type in the cache.
	 * 
	 * @param newValue - type's name.
	 */
	public void newType(String newValue) {
		cache.newType(newValue);
		messages.info("createRootType", newValue);
	}

	/**
	 * Creates new subtype of selected tree node.
	 * 
	 * @param newValue - subtype's name.
	 */
	public void newSubType(String newValue) {
		((Type) genericTreeBean.getSelectedTreeNodeGeneric()).newSubType(newValue);
		messages.info("createSubType", newValue, genericTreeBean.getSelectedTreeNodeGeneric().getValue());
	}

	/**
	 * Creates new attribute on selected tree node.
	 * 
	 * @param newValue - attribute's name.
	 */
	public void setAttribute(String newValue) {
		((Type) genericTreeBean.getSelectedTreeNodeGeneric()).setAttribute(newValue);
		messages.info("createRootAttribute", newValue, genericTreeBean.getSelectedTreeNodeGeneric().getValue());
	}

	/**
	 * Adds one property at propertie's map of selected tree node.
	 * 
	 * @param key - key of property.
	 * @param value - value of property.
	 */
	public void addProperty(String key, String value) {
		((Type) genericTreeBean.getSelectedTreeNodeGeneric()).getPropertiesMap().put(key, value);
		messages.info("createRootProperty", key, value);
	}

	/**
	 * Adds one constraint at constraint's map of selected tree node.
	 * 
	 * @param key - key of constraint.
	 * @param value - value of constraint.
	 */
	public void addContraint(String key, String value) {
		((Type) genericTreeBean.getSelectedTreeNodeGeneric()).getContraintsMap().put(key, value);
		messages.info("createRootProperty", key, value);
	}

	/**
	 * Adds one system property at system properties map of selected node.
	 * 
	 * @param key - key of system property.
	 * @param value - value of system property.
	 */
	public void addSystemProperty(String key, String value) {
		((Type) genericTreeBean.getSelectedTreeNodeGeneric()).getSystemPropertiesMap().put(key, value);
		messages.info("createRootProperty", key, value);
	}

	/**
	 * Creates new instance of selected node if this node is class.
	 * 
	 * @param newValue - name of new instance.
	 */
	public void newInstance(String newValue) {
		((Type) genericTreeBean.getSelectedTreeNodeGeneric()).newInstance(newValue);
		messages.info("createRootInstance", newValue, genericTreeBean.getSelectedTreeNodeGeneric().getValue());
	}

	/**
	 * Change the value of attribute of selected tree node.
	 * 
	 * @param attribute - attribute.
	 * @param newValue - new value to set.
	 */
	public void addValue(Attribute attribute, String newValue) {
		Generic currentInstance = genericTreeBean.getSelectedTreeNodeGeneric();
		currentInstance.setValue(attribute, newValue);
		messages.info("addValue", newValue, attribute, currentInstance);
	}

	/**
	 * Removes holder of selected tree node.
	 * 
	 * @param holder - holder to remove.
	 */
	public void remove(Holder holder) {
		genericTreeBean.getSelectedTreeNodeGeneric().removeHolder(holder);
		messages.info("remove", holder);
	}

	/**
	 * Removes selected tree node. Selection switch on it's parent.
	 * 
	 * @return empty string.
	 */
	public String delete() {
		Generic generic = genericTreeBean.getSelectedTreeNodeGeneric();
		generic.remove();
		messages.info("remove", generic);
		genericTreeBean.setSelectedTreeNode(genericTreeBean.getSelectedTreeNode().getParent());
		return "";
	}

	/**
	 * Removes one entry from properties map of selected tree node.
	 * 
	 * @param entry - entry to remove.
	 */
	public void removeProperty(Entry<Serializable, Serializable> entry) {
		removeEntry(genericTreeBean.getSelectedTreeNodeGeneric().getPropertiesMap(), entry);
	}

	/**
	 * Removes one entry from constraints map of selected tree node.
	 * 
	 * @param entry - entry to remove.
	 */
	public void removeContraint(Entry<Serializable, Serializable> entry) {
		removeEntry(genericTreeBean.getSelectedTreeNodeGeneric().getContraintsMap(), entry);

	}

	/**
	 * Removes one entry from system properties map of selected tree node.
	 * 
	 * @param entry - entry to remove.
	 */
	public void removeSystemProperty(Entry<Serializable, Serializable> entry) {
		removeEntry(genericTreeBean.getSelectedTreeNodeGeneric().getSystemPropertiesMap(), entry);
	}

	/**
	 * Rempoves one entry from the map.
	 * 
	 * @param map - map.
	 * @param entry - entry to remove.
	 */
	private void removeEntry(Map<Serializable, Serializable> map, Entry<Serializable, Serializable> entry) {
		try {
			map.remove(entry.getKey());
			messages.info("remove", entry.getKey());
		} catch (NotRemovableException e) {
			messages.info("cannotremove", e.getMessage());
		}
	}

	/**
	 * Creates and returns structurals created on attributes of selected tree node.
	 * 
	 * @return list of structurals.
	 */
	public List<StructuralImpl> getStructurals() {
		List<StructuralImpl> structurals = new ArrayList<>();
		for (Attribute attribute : genericTreeBean.<Type> getSelectedTreeNodeGeneric().getAttributes())
			structurals.add(new StructuralImpl(attribute, genericTreeBean.getSelectedTreeNodeGeneric().getBasePos(attribute)));
		return structurals;
	}

	/**
	 * Creates and returns structural wrappers on attributes of selected tree node.
	 * 
	 * @return list of structural wrappers.
	 */
	public List<StructuralWrapper> getStructuralWrappers() {
		List<StructuralWrapper> wrappers = new ArrayList<>();
		for (Structural structural : getStructurals()) {
			wrappers.add(new StructuralWrapper(structural));
		}
		return wrappers;
	}

	/**
	 * Returns holders of generic packaged in structural and structural wrapper. Wrapper's attribute
	 * tells if return phantoms or not.
	 * 
	 * @param structuralWrapper - structural wrapper.
	 * 
	 * @return list of holders.
	 */
	public List<Holder> getHolders(StructuralWrapper structuralWrapper) {
		return ((Type) genericTreeBean.getSelectedTreeNodeGeneric()).getHolders(structuralWrapper.getStructural().getAttribute(), structuralWrapper.getStructural().getPosition(), structuralWrapper.isReadPhantoms());
	}

	/**
	 * Returns other targets of holder.
	 * 
	 * @param holder - holder.
	 * 
	 * @return list of targets.
	 */
	public List<Generic> getOtherTargets(Holder holder) {
		return genericTreeBean.getSelectedTreeNodeGeneric().getOtherTargets(holder);
	}

	/**
	 * Checks if SingularConstraint is enabled on generic packaged within the structural.
	 * 
	 * @param structural - structural.
	 * 
	 * @return true - if SingularConstraint is enabled on generic, false - if not.
	 */
	public boolean isSingular(Structural structural) {
		return structural.getAttribute().isSingularConstraintEnabled();
	}

	/**
	 * Returns the name of CSS style to apply on holder.
	 * If holder's base is selected tree node it's style is "italic", if it's phantom style will be
	 * "phantom". If not, there is not specific style for the holder.
	 * 
	 * @param holder - holder.
	 * 
	 * @return CSS style name.
	 */
	public String getHolderStyle(Holder holder) {
		return !holder.getBaseComponent().equals(genericTreeBean.getSelectedTreeNodeGeneric()) ? "italic" : (isPhantom(holder) ? "phantom" : "");
	}

	/**
	 * Returns true if selected tree node's attribute has values. False if not.
	 * 
	 * @param attribute - attribute.
	 * 
	 * @return true - if selected tree node's attribute has values, false - if not.
	 */
	public boolean hasValues(Attribute attribute) {
		return !genericTreeBean.getSelectedTreeNodeGeneric().getValues(attribute).isEmpty();
	}

	/**
	 * Returns true if holder if phantom. False if not.
	 * 
	 * @param holder - holder.
	 * @return true - if holder is phanton, false - if not.
	 */
	public boolean isPhantom(Holder holder) {
		return holder.getValue() == null;
	}

	/**
	 * Returns true if selected tree node is meta. False if not.
	 * 
	 * @return true - if selected tree node is meta, false - if not.
	 */
	public boolean isMeta() {
		return genericTreeBean.getSelectedTreeNodeGeneric().isMeta();
	}

}
