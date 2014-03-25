package org.genericsystem.tracker.component.generic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.generic.AbstractGenericComponent;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Type;
import org.genericsystem.tracker.InstanceRow;

public class SelectItemComponent extends AbstractGenericComponent {

	private List<String> listInstances = new ArrayList<String>();

	private String value;

	public SelectItemComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
		editSelectedItem();

	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Collections.emptyList();
	}

	public void editSelectedItem() {

		TypeComponent typeSelected = this.<AbstractGenericComponent> getParent().getParent();
		Type targetType = typeSelected.getGeneric().<Type> getOtherTargets((Attribute) getGeneric()).get(0);
		List<InstanceRow> list = AttributeComponent.getTargetRows(targetType);
		for (InstanceRow instance : list) {
			getListInstances().add(instance.toString());
		}
	}

	public List<String> getListInstances() {
		return listInstances;
	}

	public void setListInstances(List<String> listInstances) {
		this.listInstances = listInstances;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String getXhtmlPath() {
		return "selectItem.xhtml";
	}

}
