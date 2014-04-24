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

	public AbstractComponent() {
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

	public void reInitChildren() {
		children = null;
	}

	private <T extends AbstractComponent> List<T> initAndGetChildren() {
		if (children == null)
			children = initChildren();
		return getChildren();
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractComponent> List<T> getChildren() {
		return (List<T>) children;
	}

	public void setChildren(List<? extends AbstractComponent> children) {
		this.children = children;
	}

	public <T extends AbstractComponent> T getRoot() {
		return getParent().getRoot();
	}

	public Cache getCache() {
		return getRoot().getCache();
	}

	protected UIComponent buildJsfComponentsBefore(UIComponent container) {
		return null;
	}

	protected UIComponent buildJsfComponentsAfter(UIComponent container) {
		return null;
	}

	protected UIComponent buildJsfContainer(UIComponent father) {
		return new HtmlPanelGroup();
	}

	protected UIComponent buildJsfChildren(UIComponent father) {
		UIComponent container = buildJsfContainer(father);
		UIComponent before = buildJsfComponentsBefore(container);
		if (before != null)
			container.getChildren().add(before);
		for (AbstractComponent component : initAndGetChildren()) {
			UIComponent children = component.buildJsfChildren(container);
			if (children != null)
				container.getChildren().add(children);
		}
		UIComponent after = buildJsfComponentsAfter(container);
		if (after != null)
			container.getChildren().add(after);
		return container;
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

	public <T> T getSecurityManager() {
		return getRoot().getSecurityManager();
	}
}
