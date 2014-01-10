package org.genericsystem.jsf.example;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SessionScoped
@Named
public class ColorManagerBean implements Serializable {

	protected static Logger log = LoggerFactory.getLogger(CarManagerBean.class);

	private static final long serialVersionUID = 6627001842443642530L;

	private String colorInstance;

	@Inject
	private Cache cache;

	public ColorType getColor() {
		return cache.find(ColorType.class);
	}

	public void addInstance() {
		getColor().addInstance(getColorInstance());
		log.info(getColorInstance());
	}

	public String getColorInstance() {
		return colorInstance;
	}

	public void setColorInstance(String colorInstance) {
		this.colorInstance = colorInstance;
	}

}
