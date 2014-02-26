package org.genericsystem.jsf.example.component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.core.Snapshot.Projector;
import org.genericsystem.generic.Type;
import org.genericsystem.jsf.example.structure.Attributes;
import org.genericsystem.jsf.example.structure.Instances;
import org.genericsystem.jsf.example.structure.Relations;
import org.genericsystem.jsf.example.structure.Types;

@Named
@SessionScoped
public class RootComponent extends AbstractComponent implements Serializable {

	private static final long serialVersionUID = -6596418502248220835L;

	@Inject
	private Cache cache;

	@PostConstruct
	public void init() {
		this.children = initChildren();
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
				return clazz != null && (Types.class.equals(clazz) || Attributes.class.equals(clazz) || Instances.class.equals(clazz) || Relations.class.equals(clazz));
			}
		}).project(new Projector<AbstractComponent, Type>() {
			private final Map<Generic, AbstractComponent> map = new HashMap<Generic, AbstractComponent>() {

				private static final long serialVersionUID = -7927996818181180784L;

				@Override
				public GenericComponent get(Object key) {
					GenericComponent result = (GenericComponent) super.get(key);
					if (result == null)
						put((Generic) key, result = new GenericComponent(RootComponent.this, (Generic) key));
					return result;
				}
			};

			@Override
			public GenericComponent project(Type element) {
				return (GenericComponent) map.get(element);
			}

		});
	}

	@Override
	public RootComponent getRoot() {
		return this;
	}

	@Override
	public Cache getCache() {
		return cache;
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/index.xhtml";
	}

}
