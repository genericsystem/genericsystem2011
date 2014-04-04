package org.genericsystem.tracker.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.genericsystem.framework.component.AbstractComponent;

public class SelectItemsComponent extends AbstractComponent {

	private List<String> values = new ArrayList<String>();

	public SelectItemsComponent(AbstractComponent parent) {
		super(parent);
		// log.info("-------------------------->" + this.getThisExpression());
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Collections.emptyList();
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	@Override
	public String getXhtmlPath() {
		return "selectItems.xhtml";
	}

}
