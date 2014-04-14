package org.genericsystem.jsf.example.component;

import java.io.Serializable;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot;
import org.genericsystem.framework.InstanceRow;
import org.genericsystem.framework.component.AbstractCollectableChildrenComponent;
import org.genericsystem.framework.component.AbstractCollectableChildrenComponent.FilterGeneric;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.ValuedComponent;
import org.genericsystem.framework.component.generic.GenericComponent;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.genericsystem.jsf.example.structure.Attributes;
import org.genericsystem.jsf.example.structure.Relations;

public class TypeComponent extends AbstractCollectableChildrenComponent implements GenericComponent, ValuedComponent {

	private String newValue;
	private final Generic generic;

	public TypeComponent(AbstractComponent parent, Generic selected) {
		super(parent);
		this.generic = selected;
	}

	@Override
	public Generic getGeneric() {
		return generic;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends AbstractComponent, U extends Generic> T buildComponent(U generic) {
		return (T) new AttributeComponent(this, generic);
	}

	@Override
	public <T extends Generic> boolean isSelected(T candidate) {
		Serializable value = candidate.getValue();
		if (!value.getClass().isAssignableFrom(Class.class))
			return false;
		@SuppressWarnings("unchecked")
		Class<?> clazz = ((Class<? extends Serializable>) value).getEnclosingClass();
		return clazz != null && (Attributes.class.equals(clazz) || Relations.class.equals(clazz));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Generic> Snapshot<T> getGenerics() {
		return (Snapshot<T>) ((Type) getGeneric()).getAttributes().filter(new FilterGeneric<Attribute>());
	}

	// private void createRelation(Generic instance, AttributeComponent attributeComponent) {
	// if (!attributeComponent.getGeneric().isRelation())
	// instance.setValue((Attribute) attributeComponent.getGeneric(), attributeComponent.getNewValue());
	// else {
	// Generic newTarget = getGeneric().<Type> getOtherTargets((Attribute) attributeComponent.getGeneric()).get(0).getInstance(attributeComponent.getNewValue());
	// if (newTarget != null)
	// instance.bind((Relation) attributeComponent.getGeneric(), newTarget);
	// }
	// }

	public String add() {
		Generic instance = ((Type) getGeneric()).setInstance(getNewValue());
		for (AttributeComponent attributeComponent : this.<AttributeComponent> getChildren()) {
			if (!attributeComponent.getGeneric().isRelation())
				instance.setValue((Attribute) attributeComponent.getGeneric(), attributeComponent.getNewValue());
			else {
				// TODO : KK just add link for binary relation
				Generic newTarget = getGeneric().<Type> getOtherTargets((Attribute) attributeComponent.getGeneric()).get(0).getInstance(attributeComponent.getNewValue());
				if (newTarget != null)
					instance.bind((Relation) attributeComponent.getGeneric(), newTarget);
			}
		}
		return null;
	}

	public List<InstanceRow> getInstanceRows() {
		return ((Type) getGeneric()).getAllInstances().<InstanceRow> project(instance -> new InstanceRow(instance));
	}

	public void remove(InstanceRow instanceRow) {
		instanceRow.getInstance().remove();
	}

	public String editMsg() {
		return "Edit instance";
	}

	public String createMsg() {
		return "+";
	}

	public String getAddMsg() {
		return "Set instance";
	}

	public String getRemoveMsg() {
		return "Remove instance";
	}

	@Override
	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	@Override
	public String toString() {
		return getGeneric().toString();
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/type.xhtml";
	}

	@Override
	public <T> T getSecurityManager() {
		// TODO Auto-generated method stub
		return null;
	}

}
