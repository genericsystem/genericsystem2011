package org.genericsystem.myadmin.beans;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named
@RequestScoped
public class PopupPanelBean {

	private String popupValue;

	@Named
	public String getPopupValue() {
		return popupValue;
	}

	public void setPopupValue(String popupValue) {
		this.popupValue = popupValue;
	}

}
