package org.genericsystem.framework.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.core.Snapshot.Projector;
import org.genericsystem.framework.component.generic.ButtonComponent;
import org.genericsystem.generic.Type;

public abstract class AbstractSelectorComponent extends AbstractComponent {

	private AbstractComponent child;

	public AbstractSelectorComponent(AbstractComponent rootComponent) {
		super(rootComponent);
		selectDefaultComponent();
	}

	public abstract boolean isSelected(Type candidate);

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return getCache().getAllTypes().filter(new Filter<Type>() {
			@Override
			public boolean isSelected(Type candidate) {
				return AbstractSelectorComponent.this.isSelected(candidate);
			}
		}).project(new Projector<AbstractComponent, Type>() {
			private final Map<Generic, AbstractComponent> map = new HashMap<Generic, AbstractComponent>() {

				private static final long serialVersionUID = -7927996818181180784L;

				@Override
				public ButtonComponent get(Object key) {
					ButtonComponent result = (ButtonComponent) super.get(key);
					if (result == null)
						put((Generic) key, result = new ButtonComponent(AbstractSelectorComponent.this, (Type) key));
					return result;
				}
			};

			@Override
			public ButtonComponent project(Type element) {
				return (ButtonComponent) map.get(element);
			}
		});
	}

	public abstract void selectDefaultComponent();

	public abstract void select(Generic selected);

	@SuppressWarnings("unchecked")
	public <T extends AbstractComponent> T getChild() {
		return (T) child;
	}

	public void setChild(AbstractComponent child) {
		this.child = child;
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/selector.xhtml";
	}

}
