package org.genericsystem.framework.component.generic;

import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.ValuedComponent;
import org.genericsystem.generic.Attribute;

public abstract class AbstractCreateAndEditComponent extends AbstractGenericCollectableChildrenComponent implements ValuedComponent {
	protected MODE mode = MODE.CREATION;
	protected String newValue;

	public static enum MODE {
		CREATION, EDITION
	};

	public AbstractCreateAndEditComponent(AbstractComponent parent, Generic generic, MODE mode) {
		super(parent, generic);
		this.mode = mode;
	}

	public abstract void create(Generic generic, AbstractComponent abstractComponent, Attribute attribute);

	public abstract void edit(AbstractGenericComponent listItem);

	public abstract void execute();

	@Override
	public String getNewValue() {
		switch (mode) {
		case EDITION:
			return Objects.toString(getGeneric());
		case CREATION:
			return newValue;
		default:
			throw new IllegalStateException();
		}
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
}
