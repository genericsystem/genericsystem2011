package org.genericsystem.framework.component.generic;

import java.util.Collections;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.InstanceRow;
import org.genericsystem.framework.component.AbstractComponent;

public abstract class AbstractAttributeComponent extends AbstractCollectableChildrenComponent {

	public AbstractAttributeComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Collections.emptyList();
	}

	public abstract List<InstanceRow> getTargetRows();
}
