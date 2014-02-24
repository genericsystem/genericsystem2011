package org.genericsystem.jsf.example.component;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named
@RequestScoped
public class GenericBean {

	private AbstractComponent root;

	private AbstractComponent selected;

	public String changeChild(AbstractComponent component) {
		return ((SelectionComponent) root).changeChild(component);
	}

	public AbstractComponent getRoot() {
		return root;
	}

	public void setRoot(AbstractComponent root) {
		this.root = root;
	}

	public AbstractComponent getSelected() {
		return selected;
	}

	public void setSelected(AbstractComponent selected) {
		this.selected = selected;
	}

	@Override
	public String toString() {
		return selected == null ? "" : selected.toString();
	}

}
