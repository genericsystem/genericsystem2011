package org.genericsystem.jsf.example.component;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;
import org.genericsystem.jsf.example.structure.Types.Cars;
import org.genericsystem.jsf.example.structure.Types.Colors;

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
		return Arrays.asList(new TypeComponent(cache.<Cars> find(Cars.class)), new TypeComponent(cache.<Colors> find(Colors.class)));
	}

	@Override
	public RootComponent getRoot() {
		return this;
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/index.xhtml";
	}

}
