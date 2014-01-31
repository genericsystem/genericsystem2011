package org.genericsystem.jsf.example;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot.Projector;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Type;
import org.genericsystem.jsf.AbstractComponent;

public class AttributeComponent extends AbstractComponent {
	private final Attribute attribute;
	private Generic newTargetInstance;
	private String newAttributeValue;

	public AttributeComponent(AbstractComponent parent, Attribute attribute) {
		super(parent);
		this.attribute = attribute;
		this.children = initChildren();
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Collections.emptyList();
	}

	public List<InstanceRow> getTargetInstanceRows() {
		return (this.<TypeComponent> getParent()).getType().<Type> getOtherTargets(attribute).get(0).getAllInstances().<InstanceRow> project(new Projector<InstanceRow, Generic>() {

			@Override
			public InstanceRow project(Generic instance) {
				return new InstanceRow(instance);
			}

		});
	}

	public String getColumnTitleAttribute() {
		if (!this.attribute.isRelation())
			return Objects.toString(this.attribute);
		else
			return Objects.toString((this.<TypeComponent> getParent()).getType().<Type> getOtherTargets(attribute).get(0).<Class<?>> getValue().getSimpleName());
	}

	public boolean isRelation() {
		return attribute.isRelation();
	}

	public String getNewTargetValue() {
		return Objects.toString(newTargetInstance);
	}

	public void setNewTargetValue(String newTargetValue) {
		newTargetInstance = this.<TypeComponent> getParent().getType().<Type> getOtherTargets(attribute).get(0).getInstance(newTargetValue);
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/attribute.xhtml";
	}

	public Attribute getAttribute() {
		return this.attribute;
	}

	public String getNewAttributeValue() {
		return newAttributeValue;
	}

	public void setNewAttributeValue(String newAttributeValue) {
		this.newAttributeValue = newAttributeValue;
	}

	public Generic getNewTargetInstance() {
		return newTargetInstance;
	}

}
