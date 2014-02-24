package org.genericsystem.jsf.example.component;

import java.util.Arrays;
import java.util.List;

import org.genericsystem.jsf.example.structure.Types.Cars;
import org.genericsystem.jsf.example.structure.Types.Colors;

public class GridComponent extends AbstractComponent {

	public GridComponent(AbstractComponent parent) {
		super(parent);
		this.children = initChildren();
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Arrays.asList(new TypeComponent(this, getCache().<Cars> find(Cars.class)), new TypeComponent(this, getCache().<Colors> find(Colors.class)), new SelectionComponent(this));
		// return Arrays.asList(new TypeComponent(this, getCache().<Cars> find(Cars.class)), new TypeComponent(this, getCache().<Colors> find(Colors.class)));
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/grid.xhtml";
	}

}
