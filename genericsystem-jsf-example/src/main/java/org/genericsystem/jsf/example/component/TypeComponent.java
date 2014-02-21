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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypeComponent extends AbstractComponent {

	protected static Logger log = LoggerFactory.getLogger(AbstractComponent.class);

	private final Type type;
	private String addInstanceValue;

	public TypeComponent(AbstractComponent parent, Type type) {
		super(parent);
		this.type = type;
		this.children = initChildren();
	}

	@Override
	public List<AttributeComponent> initChildren() {
		return type.getAttributes().filter(new Filter<Attribute>() {
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
		Generic instance = type.setInstance(addInstanceValue);
		for (AttributeComponent attributeComponent : this.<AttributeComponent> getChildren()) {
			if (!attributeComponent.getAttribute().isRelation()) {
				instance.setValue(attributeComponent.getAttribute(), attributeComponent.getNewAttributeValue());
			} else {
				if (attributeComponent.getNewTargetInstance() != null)
					instance.bind((Relation) attributeComponent.getAttribute(), attributeComponent.getNewTargetInstance());
			}
		}
		return null;
	}

	public List<InstanceRowComponent> getInstanceRows() {
		return type.getAllInstances().<InstanceRowComponent> project(new Projector<InstanceRowComponent, Generic>() {

			@Override
			public InstanceRowComponent project(Generic instance) {
				return new InstanceRowComponent(instance);
			}
		});

	}

	public String getNameManagement() {
		return Objects.toString(getValue().substring(0, getValue().length() - 1) + " Management");
	}

	public String getValue() {
		return Objects.toString(type.<Class<?>> getValue().getSimpleName());
	}

	public void remove(InstanceRowComponent instanceRow) {
		instanceRow.getInstance().remove();
	}

	public String getAddMsg() {
		return "Add instance";
	}

	public String getRemoveMsg() {
		return "Remove";
	}

	public String getAddInstanceValue() {
		return addInstanceValue;
	}

	public void setAddInstanceValue(String addInstanceValue) {
		this.addInstanceValue = addInstanceValue;
	}

	public Type getType() {
		return type;
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/type.xhtml";
	}

}
