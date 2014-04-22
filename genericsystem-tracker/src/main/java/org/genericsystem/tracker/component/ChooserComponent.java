package org.genericsystem.tracker.component;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlPanelGrid;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot;
import org.genericsystem.framework.component.AbstractChooserComponent;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.tracker.component.generic.CommandButtonComponent;
import org.genericsystem.tracker.structure.Types;

public class ChooserComponent extends AbstractChooserComponent {

	private static final int NB_COLUMNS_PANEL_GRID = 4;

	public ChooserComponent(AbstractComponent parent) {
		super(parent);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getSecurityManager() {
		return (T) this.<RootComponent> getRoot().getSecurityManager();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Generic> Snapshot<T> getGenerics() {
		return (Snapshot<T>) getCache().getAllTypes();
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
	@Override
	public <T extends AbstractComponent, U extends Generic> T buildComponent(U generic) {
		return (T) new CommandButtonComponent(ChooserComponent.this, generic);
	}

	@Override
	protected UIComponent buildJsfContainer(UIComponent father) {
		HtmlPanelGrid panelGrid = new HtmlPanelGrid();
		panelGrid.setColumns(NB_COLUMNS_PANEL_GRID);
		return panelGrid;
	}
}
