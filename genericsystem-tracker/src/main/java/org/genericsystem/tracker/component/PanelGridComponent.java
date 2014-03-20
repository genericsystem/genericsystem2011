package org.genericsystem.tracker.component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.core.Snapshot.Projector;
import org.genericsystem.generic.Type;
import org.genericsystem.tracker.component.generic.CommandButtonComponent;
import org.genericsystem.tracker.component.generic.TypeComponent;
import org.genericsystem.tracker.structure.Types;
import org.genericsystem.tracker.structure.Types.Issues;

public class PanelGridComponent extends AbstractComponent {

	private AbstractComponent child;

	public PanelGridComponent(RootComponent rootComponent) {
		super(rootComponent);
		selectDefaultComponent();
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return getCache().getAllTypes().filter(new Filter<Type>() {
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
				public CommandButtonComponent get(Object key) {
					CommandButtonComponent result = (CommandButtonComponent) super.get(key);
					if (result == null)
						put((Generic) key, result = new CommandButtonComponent(PanelGridComponent.this, (Type) key));
					return result;
				}
			};

			@Override
			public CommandButtonComponent project(Type element) {
				return (CommandButtonComponent) map.get(element);
			}
		});
	}

	private void selectDefaultComponent() {
		for (CommandButtonComponent component : this.<CommandButtonComponent> getChildren())
			if (getCache().find(Issues.class).equals(component.getGeneric()))
				select(component.getGeneric());
	}

	public void select(Generic selected) {
		this.child = new TypeComponent(this, selected);
	}

	public <T extends AbstractComponent> T getSelectedChild() {
		return (T) child;
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/panelGrid.xhtml";
	}

}
