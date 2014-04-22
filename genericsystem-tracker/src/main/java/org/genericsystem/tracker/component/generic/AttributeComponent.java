package org.genericsystem.tracker.component.generic;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlColumn;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.component.html.HtmlOutputText;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot.Projector;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.generic.AbstractValuedGenericComponent;
import org.genericsystem.framework.component.generic.GenericComponent;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Type;
import org.genericsystem.tracker.InstanceRow;

public class AttributeComponent extends AbstractValuedGenericComponent {

	public AttributeComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Collections.emptyList();
	}

	public List<InstanceRow> getTargetRows() {
		return getTargetRows(this.<TypeComponent> getParent().getGeneric().<Type> getOtherTargets((Attribute) getGeneric()).get(0));
	}

	public static List<InstanceRow> getTargetRows(Type targetType) {
		return (targetType.getAllInstances()).<InstanceRow> project(new Projector<InstanceRow, Generic>() {

			@Override
			public InstanceRow project(Generic instance) {
				return new InstanceRow(instance);
			}

		});
	}

	public String getColumnTitleAttribute() {
		if (!getGeneric().isRelation())
			return Objects.toString(getGeneric());
		else
			return Objects.toString(((GenericComponent) this.getParent()).getGeneric().<Type> getOtherTargets((Attribute) getGeneric()).get(0).<Class<?>> getValue().getSimpleName());
	}

	@Override
	protected UIComponent buildJsfContainer(UIComponent father) {
		return new HtmlColumn();
	}

	@Override
	protected UIComponent buildJsfComponentsAfter(UIComponent container) {
		HtmlOutputText attributeValue = new HtmlOutputText();
		attributeValue.setValueExpression("value", getValueExpression("getColumnTitleAttribute()"));
		((HtmlColumn) container).setHeader(attributeValue);
		HtmlDataTable innerDataTable = new HtmlDataTable();
		innerDataTable.setValueExpression("value", createValueExpression("row.getAttributeValues(" + getInternalElExpression() + ".generic" + ")"));
		innerDataTable.setVar("value");
		HtmlColumn column = new HtmlColumn();
		HtmlOutputText attribut = new HtmlOutputText();
		attribut.setValueExpression("value", createValueExpression("value"));
		column.getChildren().add(attribut);
		innerDataTable.getChildren().add(column);
		return innerDataTable;
	}
}
