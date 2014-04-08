package org.genericsystem.tracker.component;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.SessionScoped;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlOutputText;
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

	public Object getListener() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		HtmlInputText dynamicallyGenerated = new HtmlInputText();
		HtmlCommandButton commandButton = new HtmlCommandButton();
		HtmlOutputText outputText = new HtmlOutputText();
		outputText.setValue("Yes");
		commandButton.setValue("Submit");
		dynamicallyGenerated.setValue("Test");
		ctx.getViewRoot().addComponentResource(ctx, dynamicallyGenerated);
		ctx.getViewRoot().addComponentResource(ctx, commandButton);
		return null;
	}

	@Inject
	SecurityManager securityManager;

	public SecurityManager getSecurityManager() {
		return securityManager;
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Arrays.asList(new ConnectionComponent(this), new SelectorTypeComponent(this));
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/index.xhtml";
	}
}
