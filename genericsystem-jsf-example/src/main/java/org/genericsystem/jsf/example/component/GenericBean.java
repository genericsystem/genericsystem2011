package org.genericsystem.jsf.example.component;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named
@SessionScoped
public class GenericBean implements Serializable {

	private static final long serialVersionUID = 984160719232912707L;

	private AbstractComponent root;

	private AbstractComponent selected;

	public String changeChild(AbstractComponent component) {
		return ((SelectionComponent) root).changeChild(component);
	}

	public <T extends AbstractComponent> List<T> children(Object component) {
		// first call
		if (AbstractComponent.class.isAssignableFrom(component.getClass()) && selected == null) {
			selected = (AbstractComponent) component;
			root = selected;
		}
		return selected.getChildren();
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
