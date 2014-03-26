package org.genericsystem.framework.component.generic;

import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractCollectableChildrenComponent;
import org.genericsystem.framework.component.AbstractComponent;

public abstract class AbstractGenericCollectableChildrenComponent extends AbstractCollectableChildrenComponent implements GenericComponent {

	protected Generic generic;

	public AbstractGenericCollectableChildrenComponent(AbstractComponent parent) {
		super(parent);
	}

	public AbstractGenericCollectableChildrenComponent(AbstractComponent parent, Generic current) {
		super(parent);
		this.generic = current;
	}

	@Override
	public Generic getGeneric() {
		return generic;
	}

	public void setGeneric(Generic generic) {
		this.generic = generic;
	}

	@Override
	public String toString() {
		return Objects.toString(generic);
	}
}
