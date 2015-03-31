package org.genericsystem.tracker.factory;

import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.context.PartialViewContextFactory;

import org.genericsystem.tracker.context.MyPartialViewContextImpl;

public class MyPartielViewContextFactory extends PartialViewContextFactory {

	private PartialViewContextFactory parent;

	public MyPartielViewContextFactory(PartialViewContextFactory parent) {
		this.parent = parent;
	}

	@Override
	public PartialViewContext getPartialViewContext(FacesContext context) {
		return new MyPartialViewContextImpl(context);
	}

}
