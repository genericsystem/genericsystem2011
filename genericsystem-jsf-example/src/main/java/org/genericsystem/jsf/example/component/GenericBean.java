package org.genericsystem.jsf.example.component;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@SessionScoped
public class GenericBean implements Serializable {

	private static Logger log = LoggerFactory.getLogger(AbstractComponent.class);

	private static final long serialVersionUID = 984160719232912707L;

	private AbstractComponent parent;

	private AbstractComponent selected;

	private String newValue;

	public String changeChild(AbstractComponent component) {
		((SelectionComponent) selected).changeChild(component);
		return "#";
	}

	public String add() {
		((GenericComponent) selected).add(newValue);
		newValue = "";
		return "#";
	}

	public String remove(AbstractComponent component) {
		((GenericComponent) component).getSelected().remove();
		return "#";
	}

	public <T extends AbstractComponent> List<T> children(Object component) {
		// first call
		if (AbstractComponent.class.isAssignableFrom(component.getClass()) && selected == null)
			selected = (AbstractComponent) component;
		return selected.getChildren();
	}

	public String editMsg() {
		return "edit";
	}

	public String removeMsg() {
		return "remove";
	}

	public String addMsg() {
		return "+";
	}

	public AbstractComponent getParent() {
		return parent;
	}

	public void setParent(AbstractComponent parent) {
		this.parent = parent;
	}

	public AbstractComponent getSelected() {
		return selected;
	}

	public void setSelected(AbstractComponent selected) {
		this.selected = selected;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	@Override
	public String toString() {
		return selected == null ? "" : selected.toString();
	}

}
