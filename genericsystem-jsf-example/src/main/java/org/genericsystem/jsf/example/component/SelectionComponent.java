package org.genericsystem.jsf.example.component;

public abstract class SelectionComponent extends AbstractComponent {

	protected AbstractComponent child;

	public SelectionComponent() {
		this(null);
	}

	public SelectionComponent(AbstractComponent parent) {
		super(parent);
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

}
