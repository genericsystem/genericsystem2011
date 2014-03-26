package org.genericsystem.tracker.component.generic;

import java.util.Collections;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot.Projector;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.generic.AbstractValuedGenericComponent;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Type;
import org.genericsystem.tracker.InstanceRow;

public class AttributeComponent extends AbstractValuedGenericComponent {

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

}
