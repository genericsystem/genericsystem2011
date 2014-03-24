package org.genericsystem.jsf.example.component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot.Projector;
import org.genericsystem.framework.InstanceRow;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.generic.AbstractValuedGenericComponent;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Type;

public class AttributeComponent extends AbstractValuedGenericComponent {

	public AttributeComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Collections.emptyList();
	}

	@Override
	public String getColumnTitleAttribute() {
		if (!isRelation())
			return Objects.toString(getGeneric());
		else
			return Objects.toString((this.<TypeComponent> getParent()).getGeneric().<Type> getOtherTargets((Attribute) getGeneric()).get(0).<Class<?>> getValue().getSimpleName());
	}

	public List<InstanceRow> getTargetRows() {
		return this.<TypeComponent> getParent().getGeneric().<Type> getOtherTargets((Attribute) getGeneric()).get(0).getAllInstances().<InstanceRow> project(new Projector<InstanceRow, Generic>() {

			@Override
			public InstanceRow project(Generic instance) {
				return new InstanceRow(instance);
			}
		});
	}

	@Override
	public boolean isRelation() {
		return getGeneric().isRelation();
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/attribute.xhtml";
	}

}
