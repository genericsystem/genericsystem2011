package org.genericsystem.framework.component.generic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;

public abstract class AbstractSelectItemComponent extends AbstractGenericComponent {

	private List<String> listInstances = new ArrayList<String>();
	private String stringSelected;

	public AbstractSelectItemComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
		editSelectedItem();

	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Collections.emptyList();
	}

	public abstract void editSelectedItem();

	public List<String> getListInstances() {
		return listInstances;
	}

	public void setListInstances(List<String> listInstances) {
		this.listInstances = listInstances;
	}

	public String getStringSelected() {
		return stringSelected;
	}

	public void setStringSelected(String stringSelected) {
		this.stringSelected = stringSelected;
	}
}
