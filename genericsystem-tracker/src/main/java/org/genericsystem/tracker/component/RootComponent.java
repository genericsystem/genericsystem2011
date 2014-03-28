package org.genericsystem.tracker.component;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.AbstractRootComponent;

@Named
@SessionScoped
public class RootComponent extends AbstractRootComponent implements Serializable {

	private static final long serialVersionUID = -6596418502248220835L;

	@Inject
	private Cache cache;

	@PostConstruct
	public void init() {
		this.children = initChildren();
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Arrays.asList(new SelectorComponent(this));
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/index.xhtml";
	}
}
