package org.genericsystem.jsf.example.component.generic;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot.Projector;
import org.genericsystem.framework.InstanceRow;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.generic.AbstractGenericComponent;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Type;

public class AttributeComponent extends AbstractGenericComponent {

	private String newValue;

	public AttributeComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Collections.emptyList();
	}

	public List<InstanceRow> getTargetRows() {
		return getTargetRows(this.<TypeComponent> getParent().getGeneric().<Type> getOtherTargets((Attribute) getGeneric()).get(0));
	}

	public String getNewValue() {
		return Objects.toString(newValue);
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public static List<InstanceRow> getTargetRows(Type targetType) {
		return (targetType.getAllInstances()).<InstanceRow> project(new Projector<InstanceRow, Generic>() {

			@Override
			public InstanceRow project(Generic instance) {
				return new InstanceRow(instance);
			}

		});
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/attribute.xhtml";
	}

	@Override
	public <T extends AbstractComponent, U extends Generic> T buildComponent(U generic) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Generic> boolean isSelected(T candidate) {
		// TODO Auto-generated method stub
		return false;
	}

}
