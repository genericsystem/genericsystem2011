package org.genericsystem.jsf.example.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.core.Snapshot.Projector;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.genericsystem.jsf.example.structure.Attributes;
import org.genericsystem.jsf.example.structure.Relations;

public class GenericComponent extends AbstractComponent {

	protected final Generic selected;
	private String newValue;

	public GenericComponent(AbstractComponent parent, Generic selected) {
		super(parent);
		this.selected = selected;
		this.children = initChildren();
	}

	@Override
	public List<AttributeComponent> initChildren() {
		return ((Type) selected).getAttributes().filter(new Filter<Attribute>() {
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
						put((Attribute) key, result = new AttributeComponent(GenericComponent.this, (Attribute) key));
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
		Generic instance = ((Type) selected).setInstance(newValue);
		for (AttributeComponent attributeComponent : this.<AttributeComponent> getChildren()) {
			if (!attributeComponent.getSelected().isRelation())
				instance.setValue((Attribute) attributeComponent.getSelected(), attributeComponent.getNewValue());
			else
				instance.bind((Relation) attributeComponent.getSelected(), attributeComponent.getNewTarget());
		}
		return null;
	}

	public List<InstanceRowComponent> getInstanceRows() {
		return ((Type) selected).getAllInstances().<InstanceRowComponent> project(new Projector<InstanceRowComponent, Generic>() {

			@Override
			public InstanceRowComponent project(Generic instance) {
				return new InstanceRowComponent(GenericComponent.this, instance);
			}
		});

	}

	public void remove(InstanceRowComponent instanceRow) {
		instanceRow.getSelected().remove();
	}

	public String getValue() {
		return Objects.toString(selected);
	}

	public String getAddMsg() {
		return "Add instance";
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

	public Generic getSelected() {
		return selected;
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/genericComponent.xhtml";
	}

	@Override
	public String toString() {
		return selected.toString();
	}

}
