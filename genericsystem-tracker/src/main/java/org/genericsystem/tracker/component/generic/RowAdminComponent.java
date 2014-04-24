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
import org.genericsystem.tracker.component.SelectorInstanceAdministrationComponent;

public class RowAdminComponent extends AbstractGenericComponent {
	public RowAdminComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		if (generic.getComponents().isEmpty()) {
			Generic selectedType = ((SelectorInstanceAdministrationComponent) getParentSelector()).getTypeSelected();
			return Arrays.asList(new OutputTextComponent(this, selectedType), new InputTextComponent(this, generic.isStructural() ? null : generic));
		}
		OutputTextComponent outputTextComponent = new OutputTextComponent(this, getGeneric());
		Generic type = ((AbstractGenericCollectableChildrenComponent) getParentSelector().getChild()).getGeneric();
		SelectOneMenuComponent selectOneMenuComponent = null;
		if (getGeneric().isRelation()) {
			outputTextComponent.setNewValue(Objects.toString(getParentSelector().<AbstractGenericCollectableChildrenComponent> getChild().getGeneric().<Type> getOtherTargets((Attribute) getGeneric()).get(0)));
			Type targetType = type.<Type> getOtherTargets((Attribute) getGeneric()).get(0);
			selectOneMenuComponent = new SelectOneMenuComponent(this, targetType);
			SelectItemsComponent selectItemsComponent = new SelectItemsComponent(selectOneMenuComponent);
			for (InstanceRow instance : AttributeComponent.getTargetRows(targetType))
				selectItemsComponent.getValues().add(instance.toString());
			selectOneMenuComponent.getChildren().add(selectItemsComponent);
		}
		return Arrays.asList(outputTextComponent, getGeneric().isRelation() ? selectOneMenuComponent : new InputTextComponent(this, type.getHolder((Holder) getGeneric())));
	}

	@Override
	protected UIComponent buildJsfContainer(UIComponent father) {
		return father;
	}

}
