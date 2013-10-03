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
import org.genericsystem.generic.MapProvider;
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

	public List<StructuralImpl> getStructurals() {
		List<StructuralImpl> wrappers = new ArrayList<>();
		for (Attribute attribute : genericTreeBean.<Type> getSelectedTreeNodeGeneric().getAttributes())
			wrappers.add(new StructuralImpl(attribute, genericTreeBean.getSelectedTreeNodeGeneric().getBasePos(attribute)));
		return wrappers;
	}

	// public List<StructuralWrapper> getStructuralWrappers() {
	// List<StructuralWrapper> wrappers = new ArrayList<>();
	// Snapshot<Attribute> attributes = ((GenericImpl) genericTreeBean.getSelectedTreeNodeGeneric()).getAttributes();
	// for (int i = 0; i < attributes.size(); i++)
	// wrappers.add(new StructuralWrapper(new StructuralImpl(attributes.get(i), i)));
	// return wrappers;
	// }

	@SuppressWarnings("unchecked")
	public List<Entry<Serializable, Serializable>> getMap(MapProvider mapProvider) {
		return (List<Entry<Serializable, Serializable>>) genericTreeBean.getSelectedTreeNodeGeneric().getMap(mapProvider.getClass()).entrySet();
	}

	/**/

	/*
	 * public Set<Entry<Serializable, Serializable>> getListEntries(MapProvider mapProvider) { return getMap(mapProvider).entrySet(); }
	 */

	/*
	 * public Snapshot<Structural> getStructurals() { return new AbstractSnapshot<Structural>() {
	 * 
	 * @Override public Iterator<Structural> iterator() { return structuralsIterator(); } }; }
	 */

	/*
	 * public Iterator<Structural> structuralsIterator() { return new AbstractConcateIterator<Attribute, Structural>(GenericImpl.this.getAttributes().iterator()) {
	 * 
	 * @Override protected Iterator<Structural> getIterator(final Attribute attribute) { return new SingletonIterator<Structural>(new StructuralImpl(attribute, getBasePos(attribute))); } }; }
	 */

	// @SuppressWarnings("unused")
	// private StructuralWrapper getStructuralWrapper(Structural structural) {
	// for (StructuralWrapper old : structuralWrappers)
	// if (old.getStructural().equals(structural))
	// return old;
	// return new StructuralWrapper(structural);
	// }

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

	// public class StructuralMap extends StructuralImpl{
	//
	// public StructuralMap(AbstractMapProvider<Serializable, Serializable> mapProvider, int position) {
	// super(mapProvider, position);
	// }
	//
	// @Override
	// public MapProvider getAttribute() {
	// return (MapProvider) super.getAttribute();
	// }
	//
	// @SuppressWarnings("unchecked")
	// public Map<Serializable, Serializable> getMap(){
	// return getAttribute().getMap((Class<AbstractMapProvider<Serializable, Serializable>>)getAttribute().getValue());
	// }
	//
	// }

	public List<Holder> getHolders(StructuralWrapper structuralWrapper) {
		return ((Type) genericTreeBean.getSelectedTreeNodeGeneric()).getHolders(structuralWrapper.getStructural().getAttribute(), structuralWrapper.getStructural().getPosition(), structuralWrapper.isReadPhantoms());
	}

	public List<Generic> getOtherTargets(Holder holder) {
		return genericTreeBean.getSelectedTreeNodeGeneric().getOtherTargets(holder);
	}

	/*
	 * @SuppressWarnings("unchecked") public List<Entry<Serializable, Serializable>> getPropertiesMap() { return (List<Entry<Serializable, Serializable>>) genericTreeBean.getSelectedTreeNodeGeneric().getPropertiesMap().entrySet(); }
	 * 
	 * @SuppressWarnings("unchecked") public List<Entry<Serializable, Serializable>> getContraintsMap() { return (List<Entry<Serializable, Serializable>>) genericTreeBean.getSelectedTreeNodeGeneric().getContraintsMap().entrySet(); }
	 * 
	 * @SuppressWarnings("unchecked") public List<Entry<Serializable, Serializable>> getSystemPropertiesMap() { return (List<Entry<Serializable, Serializable>>) genericTreeBean.getSelectedTreeNodeGeneric().getSystemPropertiesMap().entrySet(); }
	 */

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
