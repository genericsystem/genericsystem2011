package org.genericsystem.framework.component.generic;

import java.util.List;
import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;

public abstract class AbstractEditComponent extends AbstractValueAndGenericComponent {

	public AbstractEditComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
		initChildren();
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return getGenerics().filter(new FilterGeneric<Generic>()).project(new ProjectorGeneric<AbstractComponent, Generic>());
	}

	public String getInstanceName() {
		return Objects.toString(getGeneric().toString());
	}

	public void setInstanceName(String name) {
		newValue = name;
	}

	public void modify() {
		if (!getInstanceName().equals(newValue))
			setGeneric(getGeneric().setValue(newValue));
		for (AbstractRowComponent row : this.<AbstractRowComponent> getChildren())
			for (AbstractComponent selectItem : row.<AbstractComponent> getChildren())
				modify(selectItem);
	}

	public abstract void modify(AbstractComponent selectItem);

	@Override
	public boolean isRelation() {
		return getGeneric().isRelation();
	}

}
