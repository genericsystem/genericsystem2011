package org.genericsystem.myadmin.beans;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.genericsystem.generic.Attribute;

@Named
@SessionScoped
public class PopupPanelBean implements Serializable {

	private static final long serialVersionUID = -3475636000594021248L;

	@Named
	private String popupKey;

	@Named
	private String popupValue;

	@Named
	private Attribute attribute;

	public String getPopupKey() {
		return popupKey;
	}

	public void setPopupKey(String popupKey) {
		this.popupKey = popupKey;
	}

	public String getPopupValue() {
		return popupValue;
	}

	public void setPopupValue(String popupValue) {
		this.popupValue = popupValue;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

}
