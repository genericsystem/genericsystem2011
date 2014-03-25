package org.genericsystem.tracker.component.generic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.core.Snapshot.Projector;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.genericsystem.tracker.InstanceRow;
import org.genericsystem.tracker.structure.Attributes;
import org.genericsystem.tracker.structure.Relations;

public class TypeComponent extends AbstractGenericComponent {

	private String newValue;

	private AbstractGenericComponent child;

	public TypeComponent(AbstractComponent parent, Generic selected) {
		super(parent, selected);
		this.children = initChildren();
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	@Override
	public List<AttributeComponent> initChildren() {
		return ((Type) getGeneric()).getAttributes().filter(new Filter<Attribute>() {
			@Override
			public boolean isSelected(Attribute candidate) {
				Class<?> clazz = candidate.<Class<?>> getValue().getEnclosingClass();
				return clazz != null && (Attributes.class.equals(clazz) || Relations.class.equals(clazz));
			}
		}).project(new Projector<AttributeComponent, Attribute>() {
			private final Map<Attribute, AttributeComponent> map = new HashMap<Attribute, AttributeComponent>() {
				private static final long serialVersionUID = -1162281462201347017L;

				@Override
				public AttributeComponent get(Object key) {
					AttributeComponent result = super.get(key);
					if (result == null)
						put((Attribute) key, result = new AttributeComponent(TypeComponent.this, (Attribute) key));
					return result;
				}
			};

			@Override
			public AttributeComponent project(Attribute attribute) {
				return map.get(attribute);
			}
		});
	}

	public String add() {
		Generic instance = ((Type) getGeneric()).setInstance(newValue);
		for (AttributeComponent attributeComponent : this.<AttributeComponent> getChildren()) {
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

	public String createMsg() {
		return "+";
	}

	public String getAddMsg() {
		return "Set instance";
	}

	public String getRemoveMsg() {
		return "Remove instance";
	}

	public AbstractComponent getChild() {
		return child;
	}

	public void setEdit(InstanceRow instanceRow) {
		child = new EditComponent(this, instanceRow);
	}

	public void setCreate() {
		child = new CreateComponent(this, generic);
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/type.xhtml";
	}
}
