package org.genericsystem.web.old;
//package org.genericsystem.web.beans;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.enterprise.context.ConversationScoped;
//import javax.enterprise.event.Event;
//import javax.inject.Inject;
//import javax.inject.Named;
//
//import org.genericsystem.core.Generic;
//import org.genericsystem.generic.Type;
//import org.genericsystem.web.qualifiers.TypeChangeEvent;
//import org.slf4j.Logger;
//
//@Named
//@ConversationScoped
//public class CreateRelationManager implements Serializable {
//
//	private static final long serialVersionUID = 6186994931050234095L;
//
//	@Inject
//	private Logger log;
//
//	@Inject
//	private TypesProvider typesProvider;
//
//	private List<Type> targets = new ArrayList<>();
//
//	public List<Type> getTargets() {
//		return targets;
//	}
//
//	private String targetSelected;
//
//	public String getTargetSelected() {
//		return targetSelected;
//	}
//
//	public void setTargetSelected(String targetSelected) {
//		this.targetSelected = targetSelected;
//	}
//
//	public void addTarget() {
//		log.info("targetSelected : " + targetSelected);
//		Type typeSelected = null;
//		for (Generic generic : typesProvider.getTypes()) {
//			if (generic.toString().equals(targetSelected)) {
//				typeSelected = (Type) generic;
//				break;
//			}
//		}
//		log.info("typeSelected : " + typeSelected);
//		if (typeSelected != null)
//			targets.add(typeSelected);
//	}
//
//	public void remove(Type target) {
//		targets.remove(target);
//	}
//
//	private String relationName;
//
//	public String getRelationName() {
//		return relationName;
//	}
//
//	public void setRelationName(String relationName) {
//		this.relationName = relationName;
//	}
//
//	@Inject
//	private TypesManager typesManager;
//
//	@Inject
//	@TypeChangeEvent
//	private Event<Type> typeChangeEvent;
//
//	public void createRelation() {
//		// Type selectedType = typesManager.getSelectedType();
//		// log.info("selectedType : " + selectedType);
//		// if (!"".equals(relationName)) {
//		// selectedType.addRelation(relationName, targets.toArray(new Type[targets.size()]));
//		// typeChangeEvent.fire(selectedType);
//		// }
//	}
//
// }
