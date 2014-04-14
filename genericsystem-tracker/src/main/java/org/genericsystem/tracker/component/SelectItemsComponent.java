package org.genericsystem.tracker.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;

import org.genericsystem.framework.component.AbstractComponent;

public class SelectItemsComponent extends AbstractComponent {

	private List<String> values = new ArrayList<String>();

	public SelectItemsComponent(AbstractComponent parent) {
		super(parent);
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
		return null;
	}

	@Override
	protected UIComponent buildJsfContainer(UIComponent father) {
		UISelectItems selectItems = new UISelectItems();
		selectItems.setValueExpression("value", getValueExpression("values"));
		father.getChildren().add(selectItems);
		return selectItems;
	}
}
