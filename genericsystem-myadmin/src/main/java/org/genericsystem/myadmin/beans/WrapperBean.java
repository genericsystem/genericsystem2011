package org.genericsystem.myadmin.beans;

import java.io.Serializable;
import java.util.Map.Entry;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.myadmin.beans.PanelBean.PanelTitleChangeEvent;
import org.genericsystem.myadmin.gui.GuiTreeNode;
import org.genericsystem.myadmin.util.GsMessages;

@Named
@RequestScoped
public class WrapperBean {

	@Inject
	private GsMessages messages;

	@Inject
	private GenericTreeBean genericTreeBean;

	@Inject
	private Event<PanelTitleChangeEvent> panelTitleChangeEvent;

	public boolean isBoolean(Entry<Serializable, Serializable> entry) {
		return Boolean.class.isInstance(entry.getValue());
	}

	public GenericWrapper getGenericWrapper(Generic generic) {
		return new GenericWrapper(generic);
	}

	public class GenericWrapper {
		private Generic generic;

		public GenericWrapper(Generic generic) {
			this.generic = generic;
		}

		public String getValue() {
			return generic.toString();
		}

		public void setValue(String newValue) {
			if (!newValue.equals(generic.toString())) {
				generic.updateValue(newValue);
				messages.info("updateValue", generic, newValue);
			}
		}
	}

	public GenericTreeNodeWrapper getGenericTreeNodeWrapper(GuiTreeNode genericTreeNode) {
		return new GenericTreeNodeWrapper(genericTreeNode);
	}

	public class GenericTreeNodeWrapper {
		private GuiTreeNode genericTreeNode;

		public GenericTreeNodeWrapper(GuiTreeNode genericTreeNode) {
			this.genericTreeNode = genericTreeNode;
		}

		public String getValue() {
			return genericTreeNode.getValue();
		}

		public void setValue(String newValue) {
			Generic generic = genericTreeNode.getGeneric();
			if (!newValue.equals(generic.toString())) {
				genericTreeNode.setGeneric(generic.updateValue(newValue));
				messages.info("updateGeneric", newValue, generic.getValue());
				panelTitleChangeEvent.fire(new PanelTitleChangeEvent("typesmanager", ((GenericImpl) genericTreeBean.getSelectedTreeNode().getGeneric()).toCategoryString()));
			}
		}
	}

	public PropertyWrapper getPropertyWrapper(Entry<Serializable, Serializable> entry) {
		return new PropertyWrapper(entry);
	}

	public ContraintWrapper getContraintWrapper(Entry<Serializable, Serializable> entry) {
		return new ContraintWrapper(entry);
	}

	public SystemPropertiesWrapper getSystemPropertiesWrapper(Entry<Serializable, Serializable> entry) {
		return new SystemPropertiesWrapper(entry);
	}

	public class PropertyWrapper {
		private Entry<Serializable, Serializable> entry;

		public PropertyWrapper(Entry<Serializable, Serializable> entry) {
			this.entry = entry;
		}

		public String getValue() {
			return (String) entry.getValue();
		}

		public void setValue(String newValue) {
			if (!newValue.equals(entry.getValue())) {
				genericTreeBean.getSelectedTreeNode().getGeneric().getPropertiesMap().put(entry.getKey(), newValue);
				messages.info("updateValue", entry.getValue(), newValue);
			}
		}
	}

	public class ContraintWrapper {
		private Entry<Serializable, Serializable> entry;

		public ContraintWrapper(Entry<Serializable, Serializable> entry) {
			this.entry = entry;
		}

		public Boolean getValue() {
			return (Boolean) entry.getValue();
		}

		public void setValue(Boolean newValue) {
			if (!newValue.equals(entry.getValue())) {
				genericTreeBean.getSelectedTreeNode().getGeneric().getConstraintsMap().put(entry.getKey(), newValue);
				messages.info("updateValue", entry.getValue(), newValue);
			}
		}
	}

	public class SystemPropertiesWrapper {
		private Entry<Serializable, Serializable> entry;

		public SystemPropertiesWrapper(Entry<Serializable, Serializable> entry) {
			this.entry = entry;
		}

		public Boolean getValue() {
			return (Boolean) entry.getValue();
		}

		public void setValue(Boolean newValue) {
			if (!newValue.equals(entry.getValue())) {
				genericTreeBean.getSelectedTreeNode().getGeneric().getSystemPropertiesMap().put(entry.getKey(), newValue);
				messages.info("updateValue", entry.getValue(), newValue);
			}
		}
	}

}
