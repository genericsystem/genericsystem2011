package org.genericsystem.framework.component.generic;

import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;

public abstract class AbstractCreateAndEditComponent extends AbstractGenericCollectableChildrenComponent {
	private MODE mode = MODE.CREATION;
	protected String newValue;

	protected static enum MODE {
		CREATION, EDITION
	};

	public AbstractCreateAndEditComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
	}

	public AbstractCreateAndEditComponent(AbstractComponent parent, Generic generic, MODE mode) {
		super(parent, generic);
		this.mode = mode;
	}

	public abstract void create();

	public abstract void modify();

	public MODE getMode() {
		return mode;
	}

	public void setMode(MODE mode) {
		this.mode = mode;
	}

	public String getNewValue() {
		return getMode().equals(MODE.CREATION) ? newValue : Objects.toString(getGeneric().toString());
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public String getInstanceName() {
		return Objects.toString(getGeneric().toString());
	}

	public void setInstanceName(String name) {
		newValue = name;
	}
}
