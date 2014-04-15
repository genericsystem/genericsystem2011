package org.genericsystem.tracker.component;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.AbstractRootComponent;
import org.genericsystem.jsf.util.GsMessages;
import org.genericsystem.security.manager.SecurityManager;

@Named
@SessionScoped
public class RootComponent extends AbstractRootComponent implements Serializable {

	private static final long serialVersionUID = -4077220982114605888L;

	@PostConstruct
	public void init() {
		this.children = initChildren();
	}

	@Inject
	private SecurityManager securityManager;

	@Inject
	GsMessages gsMessage;

	public GsMessages getGSMessage() {
		return gsMessage;
	}

	public Object getListener() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		buildJsfChildren(ctx.getViewRoot());
		logComponent(ctx.getViewRoot());

		return null;
	}

	private void logComponent(UIComponent component) {
		log.info("Log Component : " + component.getClass());
		for (UIComponent child : component.getChildren()) {
			logComponent(child);
		}
	}

	public SecurityManager getSecurityManager() {
		return securityManager;
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Arrays.asList(new ConnectionComponent(this), new SelectorTypeComponent(this));
	}

	@Override
	protected int getComponentIndex() {
		throw new IllegalStateException();
	}

	@Override
	protected String getInternalElExpression() {
		return "rootComponent";
	}

}
