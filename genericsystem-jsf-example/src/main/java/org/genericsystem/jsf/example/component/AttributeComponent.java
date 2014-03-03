package org.genericsystem.jsf.example.component;

import java.util.List;
import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot.Projector;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Type;

public class AttributeComponent extends GenericComponent {
	private Generic newTarget;

	public AttributeComponent(AbstractComponent parent, Generic selected) {
		super(parent, selected);
	}

	public List<InstanceRowComponent> getTargets() {
		return (this.<GenericComponent> getParent()).getSelected().<Type> getOtherTargets((Attribute) selected).get(0).getAllInstances().<InstanceRowComponent> project(new Projector<InstanceRowComponent, Generic>() {

			@Override
			public InstanceRowComponent project(Generic instance) {
				return new InstanceRowComponent(AttributeComponent.this, instance);
			}

		});
	}

	public String getColumnTitleAttribute() {
		if (!isRelation())
			return Objects.toString(selected);
		else
			return Objects.toString((this.<GenericComponent> getParent()).getSelected().<Type> getOtherTargets((Attribute) selected).get(0).<Class<?>> getValue().getSimpleName());
	}

	public boolean isRelation() {
		return selected.isRelation();
	}

	public String getNewTargetValue() {
		return Objects.toString(newTarget);
	}

	public void setNewTargetValue(String newTargetValue) {
		setNewTarget(this.<GenericComponent> getParent().getSelected().<Type> getOtherTargets((Attribute) selected).get(0).getInstance(newTargetValue));
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/attribute.xhtml";
	}

	public Generic getNewTarget() {
		return newTarget;
	}

	public void setNewTarget(Generic newTarget) {
		this.newTarget = newTarget;
	}

}
