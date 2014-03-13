package org.genericsystem.framework.component.generic;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.AbstractSelectorComponent;

public abstract class AbstractButtonComponent extends AbstractGenericComponent {

	public AbstractButtonComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
	}

	public void select() {
		// TODO class cast exception
		((AbstractSelectorComponent) this.getParent()).select(getGeneric());
	}

}
