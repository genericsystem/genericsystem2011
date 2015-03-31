package org.genericsystem.tracker.context;

import java.util.Collection;
import java.util.List;

import javax.faces.context.FacesContext;

import org.genericsystem.framework.component.AbstractComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.faces.context.PartialViewContextImpl;

public class MyPartialViewContextImpl extends PartialViewContextImpl {

	private static Logger log = LoggerFactory.getLogger(AbstractComponent.class);

	private List<String> renderIds;

	public MyPartialViewContextImpl(FacesContext ctx) {
		super(ctx);
	}

	@Override
	public Collection<String> getRenderIds() {
		if (renderIds == null)
			return super.getRenderIds();
		return renderIds;

	}

	public void setRenderId(List<String> params) {
		this.renderIds = params;
	}
}
