//package org.genericsystem.jsf.example.component;
//
//import java.util.Arrays;
//import java.util.List;
//
//public class GridComponent extends AbstractComponent {
//
//	public GridComponent(AbstractComponent parent) {
//		super(parent);
//	}
//
//	@Override
//	public List<? extends AbstractComponent> initChildren() {
//		return Arrays.asList(new GenericComponent(this, getCache().getEngine()));
//	}
//
//	@Override
//	public String getXhtmlPath() {
//		return "/pages/grid.xhtml";
//	}
//
// }
