package org.genericsystem.tracker.component.generic;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.faces.component.UIComponent;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.generic.AbstractGenericCollectableChildrenComponent;
import org.genericsystem.framework.component.generic.AbstractGenericComponent;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Type;
import org.genericsystem.tracker.InstanceRow;
import org.genericsystem.tracker.component.SelectItemsComponent;

public class RowComponent extends AbstractGenericComponent {

	public RowComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		if (generic.getComponents().isEmpty())
			return Arrays.asList(new OutputTextComponent(this, generic), new InputTextComponent(this, generic));
		Generic type = ((AbstractGenericCollectableChildrenComponent) getParentSelector().getChild()).getGeneric();
		Generic value = type.getHolder((Holder) getGeneric());

		SelectOneMenuComponent selectOneMenuComponent = null;
		if (getGeneric().isRelation()) {
			Type targetType = type.<Type> getOtherTargets((Attribute) getGeneric()).get(0);
			selectOneMenuComponent = new SelectOneMenuComponent(this, targetType);
			SelectItemsComponent selectItemsComponent = new SelectItemsComponent(selectOneMenuComponent);
			for (InstanceRow instance : AttributeComponent.getTargetRows(targetType))
				selectItemsComponent.getValues().add(instance.toString());

			selectOneMenuComponent.getChildren().add(selectItemsComponent);
		}

		OutputTextComponent outputTextComponent = new OutputTextComponent(this, getGeneric());
		if (getGeneric().isRelation())
			outputTextComponent.setNewValue(Objects.toString(getParentSelector().<AbstractGenericCollectableChildrenComponent> getChild().getGeneric().<Type> getOtherTargets((Attribute) getGeneric()).get(0)));
		return Arrays.asList(outputTextComponent, getGeneric().isRelation() ? selectOneMenuComponent : new InputTextComponent(this, value));
	}

	@Override
	protected UIComponent buildJsfContainer(UIComponent father) {
		return father;
	}

	@Override
	public String getXhtmlPath() {
		return null;
	}
}
