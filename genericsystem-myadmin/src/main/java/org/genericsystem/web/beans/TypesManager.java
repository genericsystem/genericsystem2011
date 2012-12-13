package org.genericsystem.web.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Link;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Type;
import org.genericsystem.api.generic.Value;
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

	@PostConstruct
	public void init() {

		Engine engine = cacheContext.getEngine();
		Type car = engine.newSubType("Car");
		Type pilot = engine.newSubType("Pilot");
		Type size = engine.newSubType("Size");
		Attribute power = car.addAttribute("Power");
		Attribute color = car.addAttribute("Color");
		Relation carPilot = car.addRelation("PilotRelation", pilot);
		Relation copilot = car.addRelation("Co-pilot", pilot);
		Relation carSize = car.addRelation("SizeCar", size);
		Generic height = size.newInstance("Height");
		Generic length = size.newInstance("Length");
		Generic vince = pilot.newInstance("Vincent");
		Generic ludo = pilot.newInstance("Ludovic");
		Generic BMW = car.newInstance("BMW");
		car.newInstance("Mercedes");
		car.newInstance("Nissan");
		car.newInstance("Renault");
		car.newInstance("Porsche");
		Value v = BMW.addValue(power, "356");
		BMW.addValue(power, "427");
		BMW.addValue(color, "red");
		Link link = BMW.addLink(carPilot, "35%", vince);
		BMW.addLink(carPilot, "35%", ludo);
		BMW.addLink(carSize, "456", height);
		BMW.addLink(carSize, "365", length);
		Relation carPilotSize = car.addRelation("CarPilotSize", pilot, size);
		BMW.addLink(carPilotSize, "2b", vince, height);
		//
		//
		//
		// log.info("GET COMPOSITION COMPONENT ON RELATION");
		// for (Generic g : carPilot.getCompositionComponents()) {
		// log.info("\t- " + g.getValue());
		// }
		//

		// log.info("GET COMPOSITION COMPONENT ON power");
		// for (Generic g : power.getCompositionComponents()) {
		// log.info("\t- " + g.getValue());
		// }
		//

		// log.info("REMOVE RELATON CAR PILOT BEGIN");
		// carPilot.remove();
		// log.info("LINK ALIVE : " + link.isAlive());
		//
		// log.info("REMOVE RELATON CAR PILOT END");
		// selectedType = car;
		// currentType = car;
		// currentInstance = BMW;
	}

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
			this.selectedType.newSubType(getSubType());
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
			this.selectedType.addProperty(getProperty());
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
			this.selectedType.addAttribute(getAttribute());
			typeChangeEvent.fire(selectedType);
		}
	}

}
