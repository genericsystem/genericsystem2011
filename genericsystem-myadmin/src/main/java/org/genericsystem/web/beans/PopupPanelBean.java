package org.genericsystem.web.beans;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@RequestScoped
public class PopupPanelBean {

	protected static Logger log = LoggerFactory.getLogger(PopupPanelBean.class);

	private String popupValue;

	@Named
	public String getPopupValue() {
		return popupValue;
	}

	public void setPopupValue(String popupValue) {
		this.popupValue = popupValue;
	}

}
