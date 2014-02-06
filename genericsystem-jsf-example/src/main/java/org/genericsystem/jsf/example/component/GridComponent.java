package org.genericsystem.jsf.example.component;

import java.util.Arrays;
import java.util.List;

import org.genericsystem.jsf.example.structure.Types.Cars;
import org.genericsystem.jsf.example.structure.Types.Colors;

public class GridComponent extends AbstractComponent {

	public GridComponent(AbstractComponent parent) {
		this.parent = parent;
		this.children = initChildren();
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Arrays.asList(new TypeComponent(getCache().<Cars> find(Cars.class)), new TypeComponent(getCache().<Colors> find(Colors.class)));
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/grid.xhtml";
	}

}
