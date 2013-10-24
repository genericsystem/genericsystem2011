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
import org.genericsystem.core.Snapshot;
import org.genericsystem.core.Snapshot.Projector;
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
		((Type) genericTreeBean.getSelectedTreeNodeGeneric()).getConstraintsMap().put(key, value);
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
		removeEntry(genericTreeBean.getSelectedTreeNodeGeneric().getConstraintsMap(), entry);

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

	public List<StructuralWrapper> getStructuralWrappers() {
		List<StructuralWrapper> list = new ArrayList<>();
		for (Structural structural : listStructurals(genericTreeBean.<Attribute> getSelectedTreeNodeGeneric())) {
			//log.info("structural " + structural);
			//if (!structural.getAttribute().isMapProvider())
			list.add(getStructuralWrapper(structural));
		}
		structuralWrappers = list;
		//log.info(">>>>>>>>>>>>>>>>>> " + list);
		return list;
	}

	Snapshot<Structural> listStructurals(final Attribute generic) {
		return generic.getAttributes().project(new Projector<Structural, Attribute>() {
			@Override
			public Structural project(Attribute element) {
				return new StructuralImpl(element, generic.getBasePos(element));
			}
		});
	}

	private StructuralWrapper getStructuralWrapper(Structural structural) {
		for (StructuralWrapper old : structuralWrappers)
			if (old.getStructural().equals(structural))
				return old;
		return new StructuralWrapper(structural);
	}

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

	@SuppressWarnings("unchecked")
	public List<Entry<Serializable, Serializable>> getPropertiesMap() {
		return (List<Entry<Serializable, Serializable>>) genericTreeBean.getSelectedTreeNodeGeneric().getPropertiesMap().entrySet();
	}

	@SuppressWarnings("unchecked")
	public List<Entry<Serializable, Serializable>> getContraintsMap() {
		return (List<Entry<Serializable, Serializable>>) genericTreeBean.getSelectedTreeNodeGeneric().getConstraintsMap().entrySet();
	}

	@SuppressWarnings("unchecked")
	public List<Entry<Serializable, Serializable>> getSystemPropertiesMap() {
		return (List<Entry<Serializable, Serializable>>) genericTreeBean.getSelectedTreeNodeGeneric().getSystemPropertiesMap().entrySet();
	}

	// TODO in GS CORE
	// public boolean isValue(Generic generic) {
	// return generic.isConcrete() && generic.isAttribute();
	// }

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
