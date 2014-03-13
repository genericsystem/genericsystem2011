package org.genericsystem.jsf.example.component;

import java.io.Serializable;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.AbstractSelectorComponent;
import org.genericsystem.generic.Type;
import org.genericsystem.jsf.example.structure.Types;
import org.genericsystem.jsf.example.structure.Types.Issues;

public class SelectorComponent extends AbstractSelectorComponent {
	public SelectorComponent(RootComponent rootComponent) {
		super(rootComponent);
		selectDefaultComponent();
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return getCache().getAllTypes().filter(new FilterGeneric<Type>()).project(new ProjectorGeneric<ButtonComponent, Type>());
	}

	@Override
	public void selectDefaultComponent() {
		for (ButtonComponent component : this.<ButtonComponent> getChildren())
			if (getCache().find(Issues.class).equals(component.getGeneric()))
				select(component.getGeneric());
	}

	@Override
	public void select(Generic selected) {
		this.setChild(new TypeComponent(this, selected));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends AbstractComponent, U extends Generic> T buildComponent(U generic) {
		return (T) new ButtonComponent(SelectorComponent.this, generic);
	}

	@Override
	public <T extends Generic> boolean isSelected(T candidate) {
		Serializable value = candidate.getValue();
		if (!value.getClass().isAssignableFrom(Class.class))
			return false;
		@SuppressWarnings("unchecked")
		Class<?> clazz = ((Class<? extends Serializable>) value).getEnclosingClass();
		return clazz != null && Types.class.equals(clazz);
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractComponent> T getSelectedChild() {
		return (T) this.getChild();
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/selector.xhtml";
	}

}
