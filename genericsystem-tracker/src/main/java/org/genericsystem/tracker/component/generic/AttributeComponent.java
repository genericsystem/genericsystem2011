package org.genericsystem.tracker.component.generic;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot.Projector;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Type;
import org.genericsystem.tracker.InstanceRow;
import org.genericsystem.tracker.component.AbstractComponent;

public class AttributeComponent extends AbstractGenericComponent {

	private String newValue;

	public String getNewValue() {
		return Objects.toString(newValue);
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public AttributeComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
	}

	public List<InstanceRow> getTargets() {
		return (this.<TypeComponent> getParent()).getGeneric().<Type> getOtherTargets((Attribute) getGeneric()).get(0).getAllInstances().<InstanceRow> project(new Projector<InstanceRow, Generic>() {

			@Override
			public InstanceRow project(Generic instance) {
				return new InstanceRow(instance);
			}

		});
	}

	public String getColumnTitleAttribute() {
		if (!isRelation())
			return Objects.toString(getGeneric());
		else
			return Objects.toString((this.<TypeComponent> getParent()).getGeneric().<Type> getOtherTargets((Attribute) getGeneric()).get(0).<Class<?>> getValue().getSimpleName());
	}

	public boolean isRelation() {
		return getGeneric().isRelation();
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/attribute.xhtml";
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Collections.emptyList();
	}

}
