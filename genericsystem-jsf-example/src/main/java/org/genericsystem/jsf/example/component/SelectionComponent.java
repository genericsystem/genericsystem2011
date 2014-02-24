package org.genericsystem.jsf.example.component;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.solder.beanManager.BeanManagerLocator;
import org.jboss.solder.beanManager.BeanManagerUtils;

public abstract class SelectionComponent extends AbstractComponent {

	private BeanManager beanManager = new BeanManagerLocator().getBeanManager();

	public SelectionComponent() {
		this(null);
	}

	public SelectionComponent(AbstractComponent parent) {
		super(parent);
	}

	public void changeChild(AbstractComponent component) {
		GenericBean genericBean = BeanManagerUtils.getContextualInstance(beanManager, GenericBean.class);
		genericBean.setParent(component.getParent());
		genericBean.setSelected(component);
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/selectionComponent.xhtml";
	}

}
