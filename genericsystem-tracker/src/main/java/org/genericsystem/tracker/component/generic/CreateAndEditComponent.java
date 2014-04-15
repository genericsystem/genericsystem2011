package org.genericsystem.tracker.component.generic;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlPanelGrid;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.generic.AbstractGenericCollectableChildrenComponent;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.genericsystem.tracker.structure.Attributes;
import org.genericsystem.tracker.structure.Relations;

@SuppressWarnings("unchecked")
public class CreateAndEditComponent extends AbstractGenericCollectableChildrenComponent {
	private String title;

	private RowComponent instanceRow;

	public CreateAndEditComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
		title = generic.isStructural() ? "add" : "update";
		instanceRow = new RowComponent(this, generic);
	}

	@Override
	public <T extends AbstractComponent> List<T> getChildren() {
		ArrayList<T> children = new ArrayList<>(super.<T> getChildren());
		children.add(0, (T) instanceRow);
		return children;
	}

	@Override
	public <T extends Generic> Snapshot<T> getGenerics() {
		return (Snapshot<T>) ((Type) getGeneric()).getAttributes();
	}

	@Override
	public <T extends Generic> boolean isSelected(T candidate) {
		Class<?> clazz = candidate.<Class<?>> getValue().getEnclosingClass();
		return clazz != null && (Attributes.class.equals(clazz) || Relations.class.equals(clazz));
	}

	@Override
	public <T extends AbstractComponent, U extends Generic> T buildComponent(U generic) {
		return (T) new RowComponent(this, generic);
	}

	public String execute() {
		List<RowComponent> rows = this.<RowComponent> getChildren();
		InputTextComponent inputTextComponent = rows.get(0).<InputTextComponent> getChildren().get(1);
		setGeneric(generic.isStructural() ? ((Type) generic).setInstance(inputTextComponent.getNewValue()) : getGeneric().setValue(inputTextComponent.getNewValue()));
		for (int i = 1; i < rows.size(); i++) {
			Generic attribute = null;
			for (AbstractComponent component : rows.get(i).getChildren()) {
				if (component instanceof OutputTextComponent)
					attribute = ((OutputTextComponent) component).getGeneric();
				else if (component instanceof SelectOneMenuComponent) {
					Generic instance = ((Type) getGeneric().<Type> getOtherTargets((Attribute) attribute).get(0)).getInstance(((SelectOneMenuComponent) component).getNewValue());
					getGeneric().bind((Relation) attribute, instance);
				} else if (component instanceof InputTextComponent)
					getGeneric().setValue((Attribute) attribute, (((InputTextComponent) component).getNewValue()).toString());
			}
		}
		getParentSelector().child = null;
		return "index.xhtml";
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public <T> T getSecurityManager() {
		return null;
	}

	@Override
	protected UIComponent buildJsfContainer(UIComponent father) {
		HtmlForm form = new HtmlForm();

		HtmlPanelGrid panelGrid = new HtmlPanelGrid();
		if (getParentSelector().child != null) {
			panelGrid.setColumns(2);
			panelGrid.setStyleClass("order-table");
			panelGrid.setHeaderClass("order-table-header");
			panelGrid.setRowClasses("order-table-odd-row,order-table-even-row");
			form.getChildren().add(panelGrid);
			father.getChildren().add(form);
		}
		return panelGrid;
	}

	@Override
	protected void buildJsfComponentsAfter(UIComponent container) {
		HtmlCommandButton button = new HtmlCommandButton();
		button.setValue(getTitle());
		button.setActionExpression(getMethodExpression("execute"));
		container.getChildren().add(button);
	}
}
