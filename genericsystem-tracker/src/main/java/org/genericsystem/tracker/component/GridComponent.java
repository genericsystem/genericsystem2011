package org.genericsystem.tracker.component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.core.Snapshot.Projector;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.generic.Type;
import org.genericsystem.tracker.component.generic.TypeComponent;
import org.genericsystem.tracker.structure.Types;

public class GridComponent extends AbstractComponent {

	public GridComponent(AbstractComponent parent) {
		super(parent);
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return getCache().getAllTypes().filter(new Filter<Type>() {
			@Override
			public boolean isSelected(Type candidate) {
				Serializable value = candidate.getValue();
				if (!value.getClass().isAssignableFrom(Class.class))
					return false;
				@SuppressWarnings("unchecked")
				Class<?> clazz = ((Class<? extends Serializable>) value).getEnclosingClass();
				return clazz != null && Types.class.equals(clazz);
			}
		}).project(new Projector<AbstractComponent, Type>() {
			private final Map<Generic, AbstractComponent> map = new HashMap<Generic, AbstractComponent>() {

				private static final long serialVersionUID = -7927996818181180784L;

				@Override
				public TypeComponent get(Object key) {
					TypeComponent result = (TypeComponent) super.get(key);
					if (result == null)
						put((Generic) key, result = new TypeComponent(GridComponent.this, (Type) key));
					return result;
				}
			};

			@Override
			public TypeComponent project(Type element) {
				return (TypeComponent) map.get(element);
			}
		});
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/grid.xhtml";
	}

}
