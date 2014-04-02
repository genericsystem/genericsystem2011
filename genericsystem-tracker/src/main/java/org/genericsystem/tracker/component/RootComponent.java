package org.genericsystem.tracker.component;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PostAddToViewEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.AbstractRootComponent;
import org.genericsystem.security.manager.SecurityManager;

@Named
@SessionScoped
public class RootComponent extends AbstractRootComponent implements SystemEventListener, Serializable {

	private static final long serialVersionUID = -4077220982114605888L;

	@PostConstruct
	public void init() {
		this.children = initChildren();
		log.info("construct");
		FacesContext ctx = FacesContext.getCurrentInstance();
		ctx.getViewRoot().subscribeToViewEvent(PostAddToViewEvent.class, this);
	}

	@Inject
	SecurityManager securityManager;

	public SecurityManager getSecurityManager() {
		return securityManager;
	}

	@Override
	public boolean isListenerForSource(Object source) {
		return (source instanceof UIViewRoot);
	}

	@Override
	public void processEvent(SystemEvent arg0) throws AbortProcessingException {
		FacesContext ctx = FacesContext.getCurrentInstance();
		HtmlInputText dynamicallyGenerated = new HtmlInputText();
		HtmlCommandButton commandButton = new HtmlCommandButton();
		HtmlOutputText outputText = new HtmlOutputText();
		outputText.setValue("Yes");
		commandButton.setValue("Submit");
		// dynamicallyGenerated.setValueExpression("", "value", application.getExpressionFactory().createValueExpression(context.getELContext(), "#{_internal}", Object.class));
		dynamicallyGenerated.setValue("Test");
		ctx.getViewRoot().addComponentResource(ctx, dynamicallyGenerated);
		ctx.getViewRoot().addComponentResource(ctx, commandButton);

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
