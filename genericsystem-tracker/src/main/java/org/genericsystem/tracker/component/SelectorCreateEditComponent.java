package org.genericsystem.tracker.component;

import java.util.Arrays;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.AbstractSelectorComponent;
import org.genericsystem.tracker.component.generic.DisplayerCreateEditComponent;

public class SelectorCreateEditComponent extends AbstractSelectorComponent {

	Generic selected;

	public SelectorCreateEditComponent(AbstractComponent parent, Generic selected) {
		super(parent);
		this.selected = selected;
	}

	DisplayerCreateEditComponent displayerCreateEdit = new DisplayerCreateEditComponent(this);

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Arrays.asList(new ChooserCreateEditComponent(this), displayerCreateEdit);
	}

	@Override
	public void selectDefaultComponent() {

	}

	@Override
	public void select(Generic selected) {
		child = displayerCreateEdit.buildChild(selected);
	}

	public DisplayerCreateEditComponent getFilterCreateEdit() {
		return displayerCreateEdit;
	}

	public void setFilterCreateEdit(DisplayerCreateEditComponent filterCreateEdit) {
		this.displayerCreateEdit = filterCreateEdit;
	}

	public Generic getSelected() {
		return selected;
	}

	public void setSelected(Generic selected) {
		this.selected = selected;
	}

}
