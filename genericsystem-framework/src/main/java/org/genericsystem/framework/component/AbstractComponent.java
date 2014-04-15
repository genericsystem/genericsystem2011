package org.genericsystem.framework.component;

import java.util.List;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;

import org.genericsystem.core.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractComponent {

	protected static Logger log = LoggerFactory.getLogger(AbstractComponent.class);

	protected AbstractComponent parent;

	protected List<? extends AbstractComponent> children;

	AbstractComponent() {
		this(null);
	}

	public AbstractComponent(AbstractComponent parent) {
		this.parent = parent;
	}

	public AbstractSelectorComponent getParentSelector() {
		return getParent().getParentSelector();
	}

	public abstract List<? extends AbstractComponent> initChildren();

	@SuppressWarnings("unchecked")
	public <T extends AbstractComponent> T getParent() {
		return (T) parent;
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractComponent> List<T> getChildren() {
		if (children == null)
			children = initChildren();
		return (List<T>) children;
	}

	public <T extends AbstractComponent> T getRoot() {
		return getParent().getRoot();
	}

	public Cache getCache() {
		return getRoot().getCache();
	}

	protected void buildJsfComponentsBefore(UIComponent container) {
	}

	protected void buildJsfComponentsAfter(UIComponent container) {
	}

	protected UIComponent buildJsfContainer(UIComponent father) {
		HtmlPanelGroup panelGroup = new HtmlPanelGroup();
		father.getChildren().add(panelGroup);
		return panelGroup;
	}

	protected void buildJsfChildren(UIComponent father) {
		UIComponent container = buildJsfContainer(father);
		buildJsfComponentsBefore(container);
		for (AbstractComponent component : getChildren()) {
			component.buildJsfChildren(container);
		}
		buildJsfComponentsAfter(container);
	}

	protected int getComponentIndex() {
		return getParent().getChildren().indexOf(this);
	}

	protected String getInternalElExpression() {
		return getParent().getInternalElExpression() + ".children[" + getComponentIndex() + "]";
	}

	protected String getElExpression(String action) {
		return "#{" + getInternalElExpression() + "." + action + "}";
	}

	protected String getElExpression() {
		return "#{" + getInternalElExpression() + "}";
	}

	protected ValueExpression getValueExpression(String attribut) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		return ctx.getApplication().getExpressionFactory().createValueExpression(ctx.getELContext(), getElExpression(attribut), Object.class);
	}

	protected AbstractComponent getEvaluateExpression() {
		FacesContext context = FacesContext.getCurrentInstance();
		return context.getApplication().evaluateExpressionGet(context, getElExpression(), AbstractComponent.class);
	}

	protected MethodExpression getMethodExpression(String action) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		return ctx.getApplication().getExpressionFactory().createMethodExpression(ctx.getELContext(), getElExpression(action), String.class, new Class[] {});
	}

	protected ValueExpression createValueExpression(String attribut) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		return ctx.getApplication().getExpressionFactory().createValueExpression(ctx.getELContext(), "#{" + attribut + "}", Object.class);
	}
}
