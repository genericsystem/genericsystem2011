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
public class CarManagerBean implements Serializable {

	protected static Logger log = LoggerFactory.getLogger(CarManagerBean.class);

	private static final long serialVersionUID = 200716091522569492L;

	private String carInstance;

	@Inject
	private Cache cache;

	public CarType getCar() {
		return cache.find(CarType.class);
	}

	public void addInstance() {
		getCar().addInstance(getCarInstance());
		// log.info(getCarInstance());
	}

	public String getCarInstance() {
		return carInstance;
	}

	public void setCarInstance(String carInstance) {
		this.carInstance = carInstance;
	}

}
