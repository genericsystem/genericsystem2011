package org.genericsystem.framework.component.generic;

import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot.Projector;
import org.genericsystem.framework.InstanceRow;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;

public abstract class AbstractTypeComponent extends AbstractValueAndGenericComponent {

	public AbstractTypeComponent(AbstractComponent parent, Generic selected) {
		super(parent, selected);
	}

	@Override
	public List<AbstractAttributeComponent> initChildren() {
		return ((Type) getGeneric()).getAttributes().filter(new FilterGeneric<Attribute>()).project(new ProjectorGeneric<AbstractAttributeComponent, Attribute>());
	}

	public String add() {
		Generic instance = ((Type) getGeneric()).setInstance(getNewValue());
		for (AbstractAttributeComponent attributeComponent : this.<AbstractAttributeComponent> getChildren()) {
			if (!attributeComponent.getGeneric().isRelation())
				instance.setValue((Attribute) attributeComponent.getGeneric(), attributeComponent.getNewValue());
			else {
				// TODO : KK just add link for binary relation
				Generic newTarget = getGeneric().<Type> getOtherTargets((Attribute) attributeComponent.getGeneric()).get(0).getInstance(attributeComponent.getNewValue());
				if (newTarget != null)
					instance.bind((Relation) attributeComponent.getGeneric(), newTarget);
			}
		}
		return null;
	}

	public List<InstanceRow> getInstanceRows() {
		return ((Type) getGeneric()).getAllInstances().<InstanceRow> project(new Projector<InstanceRow, Generic>() {
			@Override
			public InstanceRow project(Generic instance) {
				return new InstanceRow(instance);
			}
		});
	}

	public void remove(InstanceRow instanceRow) {
		instanceRow.getInstance().remove();
	}

	public String editMsg() {
		return "Edit instance";
	}

	public String getAddMsg() {
		return "Set instance";
	}

	public String getRemoveMsg() {
		return "Remove instance";
	}
}
