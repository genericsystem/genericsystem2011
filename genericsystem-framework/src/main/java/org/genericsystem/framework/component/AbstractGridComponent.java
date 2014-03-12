package org.genericsystem.framework.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.core.Snapshot.Projector;
import org.genericsystem.framework.component.generic.AbstractTypeComponent;
import org.genericsystem.generic.Type;

public abstract class AbstractGridComponent extends AbstractComponent {

	public AbstractGridComponent(AbstractComponent parent) {
		super(parent);
	}

	public abstract boolean isSelected(Type type);

	public abstract AbstractTypeComponent buildComponent(Type type);

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return getCache().getAllTypes().filter(new Filter<Type>() {
			@Override
			public boolean isSelected(Type candidate) {
				return AbstractGridComponent.this.isSelected(candidate);
			}
		}).project(new Projector<AbstractComponent, Type>() {
			private final Map<Generic, AbstractComponent> map = new HashMap<Generic, AbstractComponent>() {

				private static final long serialVersionUID = -7927996818181180784L;

				@Override
				public AbstractTypeComponent get(Object key) {
					AbstractTypeComponent result = (AbstractTypeComponent) super.get(key);
					if (result == null)
						put((Generic) key, result = buildComponent((Type) key));
					return result;
				}
			};

			@Override
			public AbstractTypeComponent project(Type element) {
				return (AbstractTypeComponent) map.get(element);
			}
		});
	}

}
