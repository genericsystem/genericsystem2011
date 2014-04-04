package org.genericsystem.framework.component;

import java.util.List;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
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

	public abstract String getXhtmlPath();

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

	protected void buildJsfComponentsBefore(UIComponent father) {
		log.info("---Before------");
	}

	protected void buildJsfComponentsAfter(UIComponent father) {
		log.info("----After---------");
	}

	protected void buildChildren(UIComponent father) {
		log.info("-->father : " + father);
		log.info("EL Expression : " + getElExpression());
		log.info("Evaluate El Expression : " + getEvaluateExpression());
		buildJsfComponentsBefore(father);
		for (AbstractComponent component : getChildren()) {
			log.info("-->component :" + component);
			component.buildChildren(father);
			// FacesContext ctx = FacesContext.getCurrentInstance();
			// FaceletContext faceletContext = (FaceletContext) ctx.getAttributes().get(FaceletContext.FACELET_CONTEXT_KEY);
			// faceletContext.includeFacelet(father, arg1);
		}
		buildJsfComponentsAfter(father);
	}

	protected int getComponentIndex() {
		return getParent().getChildren().indexOf(this);
	}

	protected String getInternalElExpression() {
		return getParent().getInternalElExpression() + ".children[" + getComponentIndex() + "]";
	}

	protected String getElExpression() {
		return "#{" + getInternalElExpression() + "}";
	}

	protected ValueExpression getValueExpression() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		return ctx.getApplication().getExpressionFactory().createValueExpression(getElExpression(), AbstractComponent.class);
	}

	protected AbstractComponent getEvaluateExpression() {
		FacesContext context = FacesContext.getCurrentInstance();
		AbstractComponent result = context.getApplication().evaluateExpressionGet(context, getElExpression(), AbstractComponent.class);
		return result;
	}
}
