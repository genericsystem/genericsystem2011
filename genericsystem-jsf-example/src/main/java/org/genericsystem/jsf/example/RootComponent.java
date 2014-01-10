package org.genericsystem.jsf.example;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;
import org.genericsystem.generic.Type;

@SessionScoped
@Named
public class RootComponent implements GraphicComponent, Serializable {

	private static final long serialVersionUID = 2850354827205302320L;

	@Inject
	private Cache cache;

	private List<GraphicComponent> children;

	@PostConstruct
	public void init() {
		children = new ArrayList<GraphicComponent>();
		for (Type type : cache.getAllTypes())
			children.add(new TypeComponent(null, type));
	}

	@Override
	public String getSrc() {
		return "/pages/types.xhtml";
	}

	@Override
	public List<GraphicComponent> getChildren() {
		return children;
	}

}
