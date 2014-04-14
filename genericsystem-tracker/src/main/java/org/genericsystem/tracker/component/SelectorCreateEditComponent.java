package org.genericsystem.tracker.component;

import java.util.Arrays;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.AbstractSelectorComponent;
import org.genericsystem.tracker.component.generic.FilterCreateEditComponent;

public class SelectorCreateEditComponent extends AbstractSelectorComponent {

	Generic selected;

	public SelectorCreateEditComponent(AbstractComponent parent, Generic selected) {
		super(parent);
		this.selected = selected;
	}

	FilterCreateEditComponent filterCreateEdit = new FilterCreateEditComponent(this);

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Arrays.asList(new ChooserCreateEditComponent(this), filterCreateEdit);
	}

	@Override
	public void selectDefaultComponent() {

	}

	@Override
	public void select(Generic selected) {
		child = filterCreateEdit.buildChild(selected);
	}

	@Override
	public String getXhtmlPath() {
		return null;
	}

	public FilterCreateEditComponent getFilterCreateEdit() {
		return filterCreateEdit;
	}

	public void setFilterCreateEdit(FilterCreateEditComponent filterCreateEdit) {
		this.filterCreateEdit = filterCreateEdit;
	}

	public Generic getSelected() {
		return selected;
	}

	public void setSelected(Generic selected) {
		this.selected = selected;
	}

}
