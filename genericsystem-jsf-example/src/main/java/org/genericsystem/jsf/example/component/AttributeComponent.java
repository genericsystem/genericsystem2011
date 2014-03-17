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
	public String getColumnTitleAttribute() {
		if (!isRelation())
			return Objects.toString(getGeneric());
		else
			return Objects.toString((this.<AbstractTypeComponent> getParent()).getGeneric().<Type> getOtherTargets((Attribute) getGeneric()).get(0).<Class<?>> getValue().getSimpleName());
	}

	@Override
	public List<InstanceRow> getTargetRows() {
		return this.<AbstractTypeComponent> getParent().getGeneric().<Type> getOtherTargets((Attribute) getGeneric()).get(0).getAllInstances().<InstanceRow> project(new Projector<InstanceRow, Generic>() {

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
	public <T extends AbstractComponent, U extends Generic> T buildComponent(U generic) {
		return null;
	}

	@Override
	public <T extends Generic> boolean isSelected(T candidate) {
		return false;
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/attribute.xhtml";
	}

}
