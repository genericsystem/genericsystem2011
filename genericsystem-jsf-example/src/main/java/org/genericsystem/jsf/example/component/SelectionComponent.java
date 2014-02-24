package org.genericsystem.jsf.example.component;

import java.util.List;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.solder.beanManager.BeanManagerLocator;
import org.jboss.solder.beanManager.BeanManagerUtils;

public class SelectionComponent extends AbstractComponent {

	protected AbstractComponent child;

	private BeanManager beanManager = new BeanManagerLocator().getBeanManager();

	public SelectionComponent() {
		this(null);
	}

	public SelectionComponent(AbstractComponent parent) {
		super(parent);
	}

	public String changeChild(AbstractComponent component) {
		GenericBean genericBean = BeanManagerUtils.getContextualInstance(beanManager, GenericBean.class);
		genericBean.setRoot(this);
		genericBean.setSelected(component);
		return component.getXhtmlPath();
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		child = initChild();
		return child.initChildren();
	}

	public AbstractComponent initChild() {
		return child != null ? child : new GenericComponent(this, getCache().getEngine());
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/selectionComponent.xhtml";
	}

	public AbstractComponent getChild() {
		return child;
	}

	public void setChild(AbstractComponent child) {
		this.child = child;
	}

	@Override
	public String toString() {
		return child.toString();
	}
}
