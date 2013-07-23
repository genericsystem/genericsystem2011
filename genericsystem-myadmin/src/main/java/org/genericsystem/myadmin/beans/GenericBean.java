package org.genericsystem.myadmin.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.Structural;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Type;
import org.genericsystem.myadmin.util.GsMessages;
import org.genericsystem.myadmin.util.GsRedirect;

@Named
@SessionScoped
public class GenericBean implements Serializable {

	private static final long serialVersionUID = 2108715680116264876L;

	@Inject
	private transient Cache cache;

	@Inject
	private GsMessages messages;

	@Inject
	private GsRedirect redirect;

	@Inject
	private GenericTreeBean genericTreeBean;

	@Inject
	private WrapperBean wrapperBean;

	private List<StructuralWrapper> structuralWrappers = new ArrayList<>();

	@PostConstruct
	public void init() {
		// TODO TEST
		// Type vehicle = cache.newType("Vehicle");
		// Type car = vehicle.newSubType("Car");
		// Type color = cache.newType("Color");
		// Type time = cache.newType("Time");
		// Attribute power = vehicle.setAttribute("power");
		// Relation vehicleColor = vehicle.setRelation("vehicleColor", color);
		// Relation vehicleColorTime = vehicle.setRelation("vehicleColorTime", color, time);
		// Generic myVehicle = vehicle.newInstance("myVehicle");
		// Generic red = color.newInstance("red");
		// Generic yellow = color.newInstance("yellow");
		// vehicle.setValue(power, 1);
		// car.setValue(power, 2);
		// // myVehicle.setValue(power, 136);
		// myVehicle.setLink(vehicleColor, "myVehicleRed", red);
		// myVehicle.bind(vehicleColorTime, red, time.newInstance("myTime"));
		// vehicle.bind(vehicleColor, yellow);
		// car.newInstance("myCar");
		//
		// Type human = cache.newType("Human");
		// Generic nicolas = human.newInstance("Nicolas");
		// Generic michael = human.newInstance("Michael");
		// Generic quentin = human.newInstance("Quentin");
		// Relation isTallerOrEqualThan = human.setRelation("isTallerOrEqualThan", human);
		// nicolas.bind(isTallerOrEqualThan, michael);
		// nicolas.bind(isTallerOrEqualThan, nicolas);
		// Relation isBrotherOf = human.setRelation("isBrotherOf", human);
		// isBrotherOf.enableMultiDirectional();
		// // quentin.bind(isBrotherOf, michael);
		// quentin.setLink(isBrotherOf, "link", michael);
		// Relation isBossOf = human.setRelation("isBossOf", human);
		// nicolas.bind(isBossOf, michael);
		//
		// michael.getProperties().put("KEY TEST", "VALUE TEST");
	}

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
		((Type) genericTreeBean.getSelectedTreeNodeGeneric()).getProperties().put(key, value);
		messages.info("createRootProperty", key, value);
	}

	public void newInstance(String newValue) {
		((Type) genericTreeBean.getSelectedTreeNodeGeneric()).newInstance(newValue);
		messages.info("createRootInstance", newValue, genericTreeBean.getSelectedTreeNodeGeneric().getValue());
	}

	public List<StructuralWrapper> getStructurals() {
		List<StructuralWrapper> list = new ArrayList<>();
		for (Structural structural : genericTreeBean.getSelectedTreeNodeGeneric().getStructurals())
			list.add(getStructuralWrapper(structural));
		structuralWrappers = list;
		return list;
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

	public void removePhantoms(Attribute attribute) {
		genericTreeBean.getSelectedTreeNodeGeneric().removePhantoms(attribute);
		messages.info("phantomsRemoved", attribute);
	}

	public List<Generic> getOtherTargets(Holder holder) {
		return genericTreeBean.getSelectedTreeNodeGeneric().getOtherTargets(holder);
	}

	public void addValue(Attribute attribute, String newValue) {
		Generic currentInstance = genericTreeBean.getSelectedTreeNodeGeneric();
		currentInstance.setValue(attribute, newValue);
		messages.info("addValue", newValue, attribute, currentInstance);
	}

	public void removeHolder(Holder holder) {
		genericTreeBean.getSelectedTreeNodeGeneric().removeHolder(holder);
		messages.info("remove", holder);
	}

	public void removeAttribute(Attribute attribute) {
		attribute.remove();
		messages.info("remove", attribute);
	}

	// TODO call clearAll...
	public String delete() {
		Generic generic = genericTreeBean.getSelectedTreeNodeGeneric();
		if (isValue(generic)) {
			genericTreeBean.setSelectedTreeNode(genericTreeBean.getSelectedTreeNode().getParent());
			removeHolder((Holder) generic);
		} else {
			generic.remove();
			messages.info("deleteFile", generic.getValue());
			genericTreeBean.setSelectedTreeNode(genericTreeBean.getSelectedTreeNode().getParent());
		}
		return "";
	}

	public List<Entry<Serializable, Serializable>> getProperties() {
		return (List) genericTreeBean.getSelectedTreeNodeGeneric().getProperties().entrySet();
	}

	public void removeProperty(Entry<Serializable, Serializable> entry) {
		genericTreeBean.getSelectedTreeNodeGeneric().getProperties().remove(entry.getKey());
		messages.info("remove", entry.getKey());
	}

	// TODO in GS CORE
	public boolean isValue(Generic generic) {
		return generic.isConcrete() && generic.isAttribute();
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
