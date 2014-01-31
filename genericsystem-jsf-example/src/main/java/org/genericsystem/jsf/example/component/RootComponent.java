package org.genericsystem.jsf.example.component;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;

@Named
@SessionScoped
public class RootComponent extends AbstractComponent implements Serializable {

	private static final long serialVersionUID = 5319339268616566267L;

	@Inject
	private Cache cache;

	@PostConstruct
	public void init() {
		this.child = new GridComponent(this);
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Collections.emptyList();
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
