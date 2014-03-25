package org.genericsystem.tracker.component;

import java.io.Serializable;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot;
import org.genericsystem.framework.component.AbstractCollectableChildrenComponent;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.tracker.component.generic.CommandButtonComponent;
import org.genericsystem.tracker.component.generic.TypeComponent;
import org.genericsystem.tracker.structure.Types;
import org.genericsystem.tracker.structure.Types.Issues;

public class PanelGridComponent extends AbstractCollectableChildrenComponent {

	private AbstractComponent child;

	public PanelGridComponent(RootComponent rootComponent) {
		super(rootComponent);
		selectDefaultComponent();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Generic> Snapshot<T> getGenerics() {
		return (Snapshot<T>) getCache().getAllTypes();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends AbstractComponent, U extends Generic> T buildComponent(U key) {
		return (T) new CommandButtonComponent(PanelGridComponent.this, key);
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

	private void selectDefaultComponent() {
		for (CommandButtonComponent component : this.<CommandButtonComponent> getChildren())
			if (getCache().find(Issues.class).equals(component.getGeneric()))
				select(component.getGeneric());
	}

	public void select(Generic selected) {
		this.child = new TypeComponent(this, selected);
	}

	public <T extends AbstractComponent> T getSelectedChild() {
		return (T) child;
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/panelGrid.xhtml";
	}
}
