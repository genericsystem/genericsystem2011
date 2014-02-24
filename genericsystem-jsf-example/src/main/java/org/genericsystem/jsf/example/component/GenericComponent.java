package org.genericsystem.jsf.example.component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.core.Snapshot.Projector;

public class GenericComponent extends SelectionComponent {

	private enum Function {
		COMPOSITES, INHERITINGS;
	}

	private Function functionSelected = Function.INHERITINGS;

	private Generic selected;

	public GenericComponent(AbstractComponent parent, Generic generic) {
		super(parent);
		selected = generic;
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		Snapshot<Generic> dependencies = functionSelected.equals(Function.INHERITINGS) ? selected.getInheritings() : selected.getComposites();
		return dependencies.filter(new Filter<Generic>() {
			public boolean isSelected(Generic candidate) {
				return true;
				// Serializable value = candidate.getValue();
				// if (!value.getClass().isAssignableFrom(Class.class))
				// return false;
				// @SuppressWarnings("unchecked")
				// Class<?> clazz = ((Class<? extends Serializable>) value).getEnclosingClass();
				// return clazz != null && (Types.class.equals(clazz) || Attributes.class.equals(clazz) || Instances.class.equals(clazz) || Relations.class.equals(clazz));
			}
		}).project(new Projector<AbstractComponent, Generic>() {
			private final Map<Generic, AbstractComponent> map = new HashMap<Generic, AbstractComponent>() {

				private static final long serialVersionUID = -7927996818181180784L;

				@Override
				public GenericComponent get(Object key) {
					GenericComponent result = (GenericComponent) super.get(key);
					if (result == null)
						put((Generic) key, result = new GenericComponent(GenericComponent.this, (Generic) key));
					return result;
				}
			};

			@Override
			public GenericComponent project(Generic element) {
				return (GenericComponent) map.get(element);
			}

		});

	}

	public List<String> getFunctions() {
		return Arrays.asList(Function.COMPOSITES.name(), Function.INHERITINGS.name());
	}

	public Function getFunctionSelected() {
		return functionSelected;
	}

	public void setFunctionSelected(Function functionSelected) {
		this.functionSelected = functionSelected;
	}

	public Generic getSelected() {
		return selected;
	}

	public void setSelected(Generic selected) {
		this.selected = selected;
	}

	@Override
	public String toString() {
		return selected.toString();
	}

}
