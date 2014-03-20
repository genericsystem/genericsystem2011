package org.genericsystem.jsf.example.component;

import java.io.Serializable;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.generic.AbstractAttributeComponent;
import org.genericsystem.framework.component.generic.AbstractTypeComponent;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.genericsystem.jsf.example.structure.Attributes;
import org.genericsystem.jsf.example.structure.Relations;

public class TypeComponent extends AbstractTypeComponent {

	public TypeComponent(AbstractComponent parent, Generic selected) {
		super(parent, selected);
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

	private void createRelation(Generic instance, AbstractAttributeComponent attributeComponent) {
		if (!attributeComponent.getGeneric().isRelation())
			instance.setValue((Attribute) attributeComponent.getGeneric(), attributeComponent.getNewValue());
		else {
			Generic newTarget = getGeneric().<Type> getOtherTargets((Attribute) attributeComponent.getGeneric()).get(0).getInstance(attributeComponent.getNewValue());
			if (newTarget != null)
				instance.bind((Relation) attributeComponent.getGeneric(), newTarget);
		}
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/type.xhtml";
	}

}
