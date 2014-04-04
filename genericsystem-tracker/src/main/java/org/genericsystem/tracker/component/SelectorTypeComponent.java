package org.genericsystem.tracker.component;

import java.util.Arrays;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

import org.genericsystem.core.Generic;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.AbstractSelectorComponent;
import org.genericsystem.tracker.component.generic.CreateAndEditComponent;
import org.genericsystem.tracker.component.generic.TypeComponent;
import org.genericsystem.tracker.structure.Types.Issues;

public class SelectorTypeComponent extends AbstractSelectorComponent implements SystemEventListener {

	public SelectorTypeComponent(AbstractComponent parent) {
		super(parent);
		// log.info("-------------------------->" + this.getThisExpression());
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Arrays.asList(new PanelGridComponent(this));
	}

	public void selectDefaultComponent() {
		select(getCache().find(Issues.class));
	}

	@Override
	public void select(Generic selected) {
		this.child = new SelectorEditComponent(this, selected);
	}

	public static class SelectorEditComponent extends SelectorTypeComponent {

		public SelectorEditComponent(AbstractComponent parent, Generic selected) {
			super(parent);
			children = Arrays.asList(new TypeComponent(this, selected));
		}

		@Override
		public void selectDefaultComponent() {
			child = null;
		}

		@Override
		public void select(Generic selected) {
			child = new CreateAndEditComponent(this, selected);
		}

	}

	@Override
	public String getXhtmlPath() {
		return "/pages/selector.xhtml";
	}

	@Override
	public boolean isListenerForSource(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void processEvent(SystemEvent arg0) throws AbortProcessingException {
		// TODO Auto-generated method stub

	}

}
