package org.genericsystem.jsf.example.component;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.AbstractSelectorComponent;

public class SelectorComponent extends AbstractSelectorComponent {

	public SelectorComponent(AbstractComponent rootComponent) {
		super(rootComponent);
	}

	// @Override
	// public <T extends Generic> boolean isSelected(Type candidate) {
	// Serializable value = candidate.getValue();
	// if (!value.getClass().isAssignableFrom(Class.class))
	// return false;
	// @SuppressWarnings("unchecked")
	// Class<?> clazz = ((Class<? extends Serializable>) value).getEnclosingClass();
	// return clazz != null && (Types.class.equals(clazz));
	// }

	@Override
	public void selectDefaultComponent() {
		// TODO Auto-generated method stub

	}

	@Override
	public void select(Generic selected) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getXhtmlPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends AbstractComponent, U extends Generic> T buildComponent(U generic) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Generic> boolean isSelected(T candidate) {
		// TODO Auto-generated method stub
		return false;
	}

	//
	// public SelectorComponent(AbstractComponent rootComponent) {
	// super(rootComponent);
	// }
	//
	// @Override
	// public <T extends Generic> boolean isSelected(Type candidate) {
	// Serializable value = candidate.getValue();
	// if (!value.getClass().isAssignableFrom(Class.class))
	// return false;
	// @SuppressWarnings("unchecked")
	// Class<?> clazz = ((Class<? extends Serializable>) value).getEnclosingClass();
	// return clazz != null && (Types.class.equals(clazz));
	// }

	// @Override
	// public void selectDefaultComponent() {
	// for (ButtonComponent component : this.<ButtonComponent> getChildren())
	// if (getCache().find(Issues.class).equals(component.getGeneric()))
	// select(component.getGeneric());
	// }
	//
	// @Override
	// public void select(Generic selected) {
	// this.setChild(new TypeComponent(this, selected) {
	//
	// @Override
	// public <T extends Generic> boolean isSelected(Attribute attribute) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// @Override
	// public AbstractAttributeComponent buildComponent(Attribute attribute) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public String getXhtmlPath() {
	// // TODO Auto-generated method stub
	// return null;
	// }
	// });
	// }
	//
	// @Override
	// public <T extends AbstractComponent, U extends Generic> T buildComponent(U generic) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public void selectDefaultComponent() {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public String getXhtmlPath() {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public <T extends Generic> boolean isSelected(T candidate) {
	// // TODO Auto-generated method stub
	// return false;
	// }

}
