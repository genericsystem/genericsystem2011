package org.genericsystem.tracker.component;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.AbstractRootComponent;
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
	private CashManagementComponent cashManagement;

	public Object getListener() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		buildJsf(ctx.getViewRoot());
		logComponent(ctx.getViewRoot());
		return null;
	}

	private void logComponent(UIComponent component) {
		log.info("Log Component : " + component.getClass() + " " + component.getId());
		for (UIComponent child : component.getChildren()) {
			logComponent(child);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public SecurityManager getSecurityManager() {
		return securityManager;
	}

	public String action() {
		return INDEX_XHTML;
	}

	@Override
	public CashManagementComponent getCashManagement() {
		return cashManagement;
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Arrays.asList(new ConnectionComponent(this), new SecurityComponent(this));
	}

	@Override
	protected int getComponentIndex() {
		throw new IllegalStateException();
	}

	@Override
	protected String getInternalElExpression() {
		return "rootComponent";
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public void setDirty(boolean isDirty) {
		return;
	}

}
