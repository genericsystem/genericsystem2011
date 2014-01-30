package org.genericsystem.jsf.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.core.Snapshot.Projector;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.genericsystem.jsf.example.structure.Attributes;
import org.genericsystem.jsf.example.structure.Relations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypeCrudComponent {

	private static Logger log = LoggerFactory.getLogger(TypeCrudComponent.class);

	private final Type type;

	private String addInstanceValue;

	private List<AttributeWrapper> attributeWrappers;

	public String getXhtmlPath() {
		return "/pages/crud.xhtml";
	}

	public TypeCrudComponent(Type type) {
		this.type = type;
	}

	public String add() {
		Generic instance = type.setInstance(addInstanceValue);
		for (AttributeWrapper attributeWrapper : getAttributeWrappers()) {
			if (!attributeWrapper.isRelation())
				instance.setValue(attributeWrapper.getAttribute(), attributeWrapper.getNewAttributeValue());
			else
				instance.bind((Relation) attributeWrapper.getAttribute(), attributeWrapper.getNewTargetInstance());
		}
		return null;
	}

	public List<InstanceRow> getInstanceRows() {
		return type.getAllInstances().<InstanceRow> project(new Projector<InstanceRow, Generic>() {

			@Override
			public InstanceRow project(Generic instance) {
				return new InstanceRow(instance);
			}
		});

	}

	public List<AttributeWrapper> getAttributeWrappers() {
		return attributeWrappers != null ? attributeWrappers : (attributeWrappers = type.getAttributes().filter(new Filter<Attribute>() {
			@Override
			public boolean isSelected(Attribute candidate) {
				Class<?> clazz = candidate.<Class<?>> getValue().getEnclosingClass();
				return clazz != null && (Attributes.class.equals(clazz) || Relations.class.equals(clazz));
			}
		}).project(new Projector<AttributeWrapper, Attribute>() {
			private final Map<Attribute, AttributeWrapper> map = new HashMap<Attribute, AttributeWrapper>() {
				private static final long serialVersionUID = -1162281462201347017L;

				@Override
				public AttributeWrapper get(Object key) {
					AttributeWrapper result = super.get(key);
					if (result == null)
						put((Attribute) key, result = new AttributeWrapper((Attribute) key));
					return result;
				}
			};

			@Override
			public AttributeWrapper project(Attribute attribute) {
				return map.get(attribute);
			}
		}));
	}

	public String getNameManagement() {
		return Objects.toString(getValue().substring(0, getValue().length() - 1) + " Management");
	}

	public String getValue() {
		return Objects.toString(type.<Class<?>> getValue().getSimpleName());
	}

	public void remove(InstanceRow instanceRow) {
		instanceRow.getInstance().remove();
	}

	public String getAddMsg() {
		return "Add instance";
	}

	public String getRemoveMsg() {
		return "Remove";
	}

	public class AttributeWrapper {
		private final Attribute attribute;
		private String newAttributeValue;
		private Generic newTargetInstance;

		public AttributeWrapper(Attribute attribute) {
			this.attribute = attribute;
		}

		public List<InstanceRow> getTargetInstanceRows() {
			return type.<Type> getOtherTargets(attribute).get(0).getAllInstances().<InstanceRow> project(new Projector<InstanceRow, Generic>() {

				@Override
				public InstanceRow project(Generic instance) {
					return new InstanceRow(instance);
				}

			});
		}

		public String getColumnTitleAttribute() {
			if (!this.attribute.isRelation())
				return Objects.toString(this.attribute);
			else
				return Objects.toString(type.<Type> getOtherTargets(attribute).get(0).<Class<?>> getValue().getSimpleName());
		}

		public String getNewAttributeValue() {
			return newAttributeValue;
		}

		public void setNewAttributeValue(String newAttributeValue) {
			this.newAttributeValue = newAttributeValue;
		}

		public Attribute getAttribute() {
			return this.attribute;
		}

		public boolean isRelation() {
			return attribute.isRelation();
		}

		public Generic getNewTargetInstance() {
			return newTargetInstance;
		}

		public String getNewTargetValue() {
			return Objects.toString(newTargetInstance);
		}

		public void setNewTargetValue(String newTargetValue) {
			newTargetInstance = type.<Type> getOtherTargets(attribute).get(0).getInstance(newTargetValue);
		}
	}

	public static class InstanceRow {
		private final Generic instance;

		public InstanceRow(Generic instance) {
			this.instance = instance;
		}

		public Generic getInstance() {
			return instance;
		}

		public String getValue() {
			return Objects.toString(instance.getValue());
		}

		public List<String> getAttributeValues(final Attribute attribute) {
			if (attribute.isRelation()) {
				return instance.getHolders(attribute).project(new Projector<String, Holder>() {
					@Override
					public String project(Holder link) {
						return instance.getOtherTargets(link).get(0).getValue();
					}
				});
			} else
				return instance.getValues((Holder) attribute);
		}
	}

	public String getAddInstanceValue() {
		return addInstanceValue;
	}

	public void setAddInstanceValue(String addInstanceValue) {
		this.addInstanceValue = addInstanceValue;
	}

}
