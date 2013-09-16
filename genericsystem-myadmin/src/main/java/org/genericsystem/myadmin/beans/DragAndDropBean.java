package org.genericsystem.myadmin.beans;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Structural;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.genericsystem.myadmin.util.GsMessages;
import org.richfaces.event.DropEvent;

@Named
@RequestScoped
public class DragAndDropBean {

	@Inject
	private GenericTreeBean genericTreeBean;

	@Inject
	private GsMessages messages;

	public void addAttribute(DropEvent dropEvent) {
		String dragValue = (String) dropEvent.getDragValue();
		Type type = (Type) genericTreeBean.getSelectedTreeNodeGeneric();
		Attribute attribute = type.setAttribute("new_attribute");
		if (dragValue.equals("int"))
			attribute.setConstraintClass(Integer.class);
		if (dragValue.equals("long"))
			attribute.setConstraintClass(Long.class);
		if (dragValue.equals("float"))
			attribute.setConstraintClass(Float.class);
		if (dragValue.equals("double"))
			attribute.setConstraintClass(Double.class);
		if (dragValue.equals("boolean"))
			attribute.setConstraintClass(Boolean.class);
		if (dragValue.equals("string"))
			attribute.setConstraintClass(String.class);
		messages.info("createRootAttribute", "new_attribute", type);
	}

	public void addTarget(DropEvent dropEvent) {
		Generic target = ((GenericTreeNode) dropEvent.getDragValue()).getGeneric();
		Attribute attribute = ((Structural) dropEvent.getDropValue()).getAttribute();
		if (target.isStructural()) {
			attribute.addComponent(attribute.getComponentsSize(), target);
			messages.info("targetRelation", target, attribute);
		} else if (target.isConcrete()) {
			if (attribute.isReallyRelation()) {
				genericTreeBean.getSelectedTreeNodeGeneric().bind((Relation) attribute, target);
				messages.info("targetLink", target, attribute);
			} else
				messages.info("errorTargetLink");
		}
	}
}