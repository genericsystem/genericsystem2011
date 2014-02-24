package org.genericsystem.jsf.example.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;

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
		List<AbstractComponent> components = new ArrayList<>();
		components.add(new GridComponent(this));
		return components;
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
