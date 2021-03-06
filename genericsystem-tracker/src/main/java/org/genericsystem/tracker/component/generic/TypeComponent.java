package org.genericsystem.tracker.component.generic;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlColumn;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot;
import org.genericsystem.core.Snapshot.Projector;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.ValuedComponent;
import org.genericsystem.framework.component.generic.AbstractGenericCollectableChildrenComponent;
import org.genericsystem.framework.component.generic.GenericComponent;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Type;
import org.genericsystem.tracker.InstanceRow;
import org.genericsystem.tracker.structure.Attributes;
import org.genericsystem.tracker.structure.Relations;

public class TypeComponent extends AbstractGenericCollectableChildrenComponent implements ValuedComponent, Serializable {

	private static final long serialVersionUID = -3768075190240927077L;

	private String newValue;

	public TypeComponent(AbstractComponent parent, Generic selected) {
		super(parent, selected);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Generic> Snapshot<T> getGenerics() {
		return (Snapshot<T>) ((Type) getGeneric()).getAttributes();
	}

	@Override
	public <T extends Generic> boolean isSelected(T candidate) {
		Class<?> clazz = candidate.<Class<?>> getValue().getEnclosingClass();
		return clazz != null && (Attributes.class.equals(clazz) || Relations.class.equals(clazz));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends AbstractComponent, U extends Generic> T buildComponent(U generic) {
		return (T) new AttributeComponent(this, generic);
	}

	public List<InstanceRow> getInstanceRows() {
		return ((Type) getGeneric()).getAllInstances().<InstanceRow> project(new Projector<InstanceRow, Generic>() {
			@Override
			public InstanceRow project(Generic instance) {
				return new InstanceRow(instance);
			}
		});
	}

	public String setEdit(InstanceRow instanceRow) {
		getParentSelector().select(instanceRow.getInstance());
		return INDEX_XHTML;
	}

	public String setCreate() {
		getParentSelector().select(generic);
		return INDEX_XHTML;
	}

	public String remove(InstanceRow instanceRow) {
		instanceRow.getInstance().remove();
		return INDEX_XHTML;
	}

	@Override
	public String toString() {
		return getGeneric().toString();
	}

	protected UIComponent buildJsfContainer(UIComponent father) {
		HtmlDataTable dataTable = new HtmlDataTable();
		dataTable.setId("dataTableType");
		dataTable.setValueExpression("value", getValueExpression("getInstanceRows()"));
		dataTable.setVar("row");
		setStyle(dataTable);
		createHeader(dataTable);

		HtmlColumn column1 = new HtmlColumn();
		HtmlOutputText outputInstance = new HtmlOutputText();
		outputInstance.setValueExpression("value", createValueExpression("row"));
		column1.getChildren().add(outputInstance);
		dataTable.getChildren().add(column1);
		return dataTable;
	}

	@Override
	protected UIComponent buildJsfComponentsAfter(UIComponent container) {
		HtmlColumn column = new HtmlColumn();
		HtmlForm form = new HtmlForm();
		HtmlCommandLink editLink = new HtmlCommandLink();
		editLink.setValue(getEditMsg());
		editLink.setActionExpression(getMethodExpression("setEdit(row)"));

		form.getChildren().add(editLink);
		column.getChildren().add(form);
		container.getChildren().add(column);

		HtmlColumn columnRemove = new HtmlColumn();
		HtmlForm formRemove = new HtmlForm();
		HtmlCommandLink removeLink = new HtmlCommandLink();
		removeLink.setValue(getRemoveMsg());
		removeLink.setActionExpression(getMethodExpression("remove(row)"));

		formRemove.getChildren().add(removeLink);
		columnRemove.getChildren().add(formRemove);
		container.getChildren().add(columnRemove);

		return container;
	}

	private void setStyle(HtmlDataTable dataTable) {
		dataTable.setStyleClass("order-table");
		dataTable.setHeaderClass("order-table-header");
		dataTable.setRowClasses("order-table-odd-row,order-table-even-row");
	}

	private void createHeader(HtmlDataTable dataTable) {
		HtmlForm form = new HtmlForm();
		form.setId("formulaireCreateEdit");
		HtmlPanelGroup panel = new HtmlPanelGroup();
		HtmlCommandButton button = new HtmlCommandButton();
		button.setValue(getCreateMsg());
		button.setActionExpression(getMethodExpression("setCreate"));
		HtmlOutputText header = new HtmlOutputText();
		header.setValue(generic);
		panel.getChildren().add(header);
		panel.getChildren().add(button);
		form.getChildren().add(panel);
		dataTable.setHeader(form);
	}

	public String getColumnTitleAttribute() {
		if (!getGeneric().isRelation())
			return Objects.toString(getGeneric());
		else
			return Objects.toString(((GenericComponent) this.getParent()).getGeneric().<Type> getOtherTargets((Attribute) getGeneric()).get(0).<Class<?>> getValue().getSimpleName());
	}

	public String getEditMsg() {
		return "Edit instance";
	}

	public String getCreateMsg() {
		return "+";
	}

	public String getAddMsg() {
		return "Set instance";
	}

	public String getRemoveMsg() {
		return "Remove instance";
	}

	@Override
	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
}
