package org.genericsystem.jsf.example.component;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.SessionScoped;
import javax.inject.Named;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.AbstractRootComponent;

@Named
@SessionScoped
public class RootComponent extends AbstractRootComponent implements Serializable {
	private static final long serialVersionUID = -6596418502248220835L;

	@PostConstruct
	public void init() {
		this.children = initChildren();
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Arrays.asList(new TypesGridComponent(RootComponent.this));
	}

	@Override
	public <T extends AbstractComponent, U extends Generic> T buildComponent(U generic) {
		return null;
	}

	@Override
	public <T extends Generic> boolean isSelected(T candidate) {
		return false;
	}

	@Override
	public <T extends Generic> Snapshot<T> getGenerics() {
		return null;
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/index.xhtml";
	}
}
