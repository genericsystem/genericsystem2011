package org.genericsystem.framework.component.generic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.core.Snapshot.Projector;
import org.genericsystem.framework.InstanceRow;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;

public abstract class AbstractTypeComponent extends AbstractGenericComponent {

	private String newValue;

	public AbstractTypeComponent(AbstractComponent parent, Generic selected) {
		super(parent, selected);
	}

	public abstract boolean isSelected(Attribute attribute);

	public abstract AbstractAttributeComponent buildComponent(Attribute attribute);

	@Override
	public List<AbstractAttributeComponent> initChildren() {
		return ((Type) getGeneric()).getAttributes().filter(new Filter<Attribute>() {
			@Override
			public boolean isSelected(Attribute candidate) {
				return AbstractTypeComponent.this.isSelected(candidate);
			}
		}).project(new Projector<AbstractAttributeComponent, Attribute>() {
			private final Map<Attribute, AbstractAttributeComponent> map = new HashMap<Attribute, AbstractAttributeComponent>() {
				private static final long serialVersionUID = -1162281462201347017L;

				@Override
				public AbstractAttributeComponent get(Object key) {
					AbstractAttributeComponent result = super.get(key);
					if (result == null)
						put((Attribute) key, result = buildComponent((Attribute) key));
					return result;
				}
			};

			@Override
			public AbstractAttributeComponent project(Attribute attribute) {
				return map.get(attribute);
			}
		});
	}

	public String add() {
		Generic instance = ((Type) getGeneric()).setInstance(newValue);
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

	// public String getValue() {
	// return Objects.toString(getSelected());
	// }

	public String editMsg() {
		return "Edit instance";
	}

	public String getAddMsg() {
		return "Set instance";
	}

	public String getRemoveMsg() {
		return "Remove instance";
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
}
