package org.genericsystem.myadmin.beans;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named
@RequestScoped
public class PopupPanelBean {

	@Named
	private String popupKey;

	@Named
	private String popupValue;

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

}
