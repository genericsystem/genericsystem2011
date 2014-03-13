package org.genericsystem.jsf.example.component;

import java.util.List;
import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot.Projector;
import org.genericsystem.framework.InstanceRow;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.generic.AbstractAttributeComponent;
import org.genericsystem.framework.component.generic.AbstractTypeComponent;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Type;

public class AttributeComponent extends AbstractAttributeComponent {

	public AttributeComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/attribute.xhtml";
	}

	public List<InstanceRow> getTargets() {
		return (this.<AbstractTypeComponent> getParent()).getGeneric().<Type> getOtherTargets((Attribute) getGeneric()).get(0).getAllInstances().<InstanceRow> project(new Projector<InstanceRow, Generic>() {

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
			return Objects.toString((this.<AbstractTypeComponent> getParent()).getGeneric().<Type> getOtherTargets((Attribute) getGeneric()).get(0).<Class<?>> getValue().getSimpleName());
	}

	public boolean isRelation() {
		return getGeneric().isRelation();
	}

}
