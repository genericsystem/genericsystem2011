package org.genericsystem.jsf.example.component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.core.Snapshot.Projector;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Type;
import org.genericsystem.jsf.example.structure.Attributes;
import org.genericsystem.jsf.example.structure.Instances;
import org.genericsystem.jsf.example.structure.Relations;
import org.genericsystem.jsf.example.structure.Types;

public class GenericComponent extends AbstractComponent {

	private Generic selected;

	private String newValue;

	public GenericComponent(AbstractComponent parent, Generic generic) {
		super(parent);
		selected = generic;
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return ((Type) selected).getAttributes().filter(new Filter<Attribute>() {
			public boolean isSelected(Attribute candidate) {
				Serializable value = candidate.getValue();
				if (!value.getClass().isAssignableFrom(Class.class))
					return false;
				@SuppressWarnings("unchecked")
				Class<?> clazz = ((Class<? extends Serializable>) value).getEnclosingClass();
				return clazz != null && (Types.class.equals(clazz) || Attributes.class.equals(clazz) || Instances.class.equals(clazz) || Relations.class.equals(clazz));
			}
		}).project(new Projector<AbstractComponent, Attribute>() {
			private final Map<Generic, AbstractComponent> map = new HashMap<Generic, AbstractComponent>() {

				private static final long serialVersionUID = -7927996818181180784L;

				@Override
				public GenericComponent get(Object key) {
					GenericComponent result = (GenericComponent) super.get(key);
					if (result == null)
						put((Generic) key, result = new GenericComponent(GenericComponent.this, (Generic) key) {
							@Override
							public String getXhtmlPath() {
								return "/pages/attribute.xhtml";
							}
						});
					return result;
				}
			};

			@Override
			public GenericComponent project(Attribute element) {
				return (GenericComponent) map.get(element);
			}
		});
	}

	public List<GenericComponent> getInstanceRows() {
		return ((Type) selected).getAllInstances().<GenericComponent> project(new Projector<GenericComponent, Generic>() {

			@Override
			public GenericComponent project(Generic instance) {
				return new GenericComponent(GenericComponent.this, instance);
			}
		});
	}

	public List<String> getAttributeValues(final Attribute attribute) {
		if (attribute.isRelation()) {
			return selected.getHolders(attribute).project(new Projector<String, Holder>() {
				@Override
				public String project(Holder link) {
					return selected.getOtherTargets(link).get(0).getValue();
				}
			});
		} else
			return selected.getValues((Holder) attribute);
	}

	public String add() {
		if (selected.isStructural())
			((Type) selected).setInstance(newValue);
		if (selected.isMeta())
			getCache().addType(newValue);
		return "#";
	}

	public String remove() {
		selected.remove();
		return "#";
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/genericComponent.xhtml";
	}

	public String removeMsg() {
		return "remove";
	}

	public String addMsg() {
		return "+";
	}

	public Generic getSelected() {
		return selected;
	}

	public void setSelected(Generic selected) {
		this.selected = selected;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	@Override
	public String toString() {
		return selected.toString();
	}

}
