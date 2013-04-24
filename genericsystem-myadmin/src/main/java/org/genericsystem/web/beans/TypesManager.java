package org.genericsystem.web.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.generic.Type;
import org.genericsystem.web.qualifiers.DiscardEvent;
import org.genericsystem.web.qualifiers.TypeChangeEvent;
import org.richfaces.component.UITree;
import org.richfaces.event.TreeSelectionChangeEvent;
import org.slf4j.Logger;

@Named
@ConversationScoped
public class TypesManager implements Serializable {

	private static final long serialVersionUID = 627461334896513027L;

	@Inject
	private Logger log;

	@Inject
	@TypeChangeEvent
	private Event<Type> typeChangeEvent;

	@Inject
	private Cache cacheContext;

	@Inject
	private TypesProvider typesProvider;

	private Type selectedType;

	public void onDiscardEvent(@Observes @DiscardEvent Generic generic) {
		this.selectedType = null;
	}

	public void applySelectedType(Type type) {
		log.info("applySelectedType:" + type);
		typeChangeEvent.fire(type);
		this.selectedType = type;
	}

	public void selectionChanged(TreeSelectionChangeEvent selectionChangeEvent) {
		// considering only single selection

		List<Object> selection = new ArrayList<Object>(selectionChangeEvent.getNewSelection());
		Object currentSelectionKey = selection.get(0);

		UITree tree = (UITree) selectionChangeEvent.getSource();
		Object storedKey = tree.getRowKey();
		tree.setRowKey(currentSelectionKey);
		Type currentSelection = (Type) tree.getRowData();
		tree.setRowKey(storedKey);

		applySelectedType(currentSelection);
		log.info("SELECTED TYPE : " + getSelectedType());
	}

	@Named
	@Produces
	public Type getSelectedType() {
		return this.selectedType;
	}

	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void createType() {
		if (!"".equals(getType())) {
			log.info("createType: " + getType());
			cacheContext.newType(getType());
		}
	}

	private String multipleInheritanceType;

	public String getMultipleInheritanceType() {
		return multipleInheritanceType;
	}

	public void setMultipleInheritanceType(String multipleInheritanceType) {
		this.multipleInheritanceType = multipleInheritanceType;
	}

	private List<Type> typesTargets = new ArrayList<>();

	public List<Type> getTypesTargets() {
		return typesTargets;
	}

	private String typeTargetSelected;

	public String getTypeTargetSelected() {
		return typeTargetSelected;
	}

	public void setTypeTargetSelected(String typeTargetSelected) {
		this.typeTargetSelected = typeTargetSelected;
	}

	public void addTypeTarget() {
		log.info("typeTargetSelected : " + typeTargetSelected);
		Type typeSelected = null;
		for (Generic generic : typesProvider.getTypes()) {
			if (generic.toString().equals(typeTargetSelected)) {
				typeSelected = (Type) generic;
				break;
			}
		}
		log.info("typeSelected : " + typeSelected);
		if (typeSelected != null)
			typesTargets.add(typeSelected);
	}

	public void removeType(Type typeTarget) {
		typesTargets.remove(typeTarget);
	}

	public void createMultipleInheritanceType() {
		if (!"".equals(type)) {
			log.info("createMultipleInheritanceType: " + getMultipleInheritanceType());
			cacheContext.newSubType(getMultipleInheritanceType(), typesTargets.toArray(new Type[typesTargets.size()]));
		}
	}

	private String subType;

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public void createSubType() {
		if (!"".equals(getSubType())) {
			log.info("createType: " + this.subType + ", extends:" + this.selectedType);
			this.selectedType.newSubType(cacheContext, getSubType());
		}
	}

	private String property;

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public void createProperty() {
		if (!"".equals(getProperty())) {
			log.info("createProperty: " + this.property + ", of:" + this.selectedType);
			this.selectedType.setProperty(cacheContext, getProperty());
			typeChangeEvent.fire(selectedType);
		}
	}

	private String attribute;

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public void createAttribute() {
		if (!"".equals(getAttribute())) {
			log.info("createAttribute: " + this.attribute + ", of:" + this.selectedType);
			this.selectedType.setAttribute(cacheContext, getAttribute());
			typeChangeEvent.fire(selectedType);
		}
	}

}
