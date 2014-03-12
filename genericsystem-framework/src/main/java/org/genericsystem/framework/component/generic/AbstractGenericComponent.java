package org.genericsystem.framework.component.generic;

import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;

public abstract class AbstractGenericComponent extends AbstractComponent {

	private final Generic generic;

	public AbstractGenericComponent(AbstractComponent parent, Generic generic) {
		super(parent);
		this.generic = generic;
	}

	public Generic getGeneric() {
		return generic;
	}

	@Override
	public String toString() {
		return Objects.toString(generic);
	}

}
