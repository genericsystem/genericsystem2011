//package org.genericsystem.jsf.old;
//
//import java.io.Serializable;
//
//import javax.enterprise.context.SessionScoped;
//import javax.inject.Inject;
//import javax.inject.Named;
//
//import org.genericsystem.core.Cache;
//import org.genericsystem.core.Generic;
//import org.genericsystem.core.Snapshot;
//import org.genericsystem.jsf.example.structure.CarType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//@Named(value = "car")
//@SessionScoped
//public class CarManagerBean implements Serializable {
//
//	private static final long serialVersionUID = 200716091522569492L;
//	protected static Logger log = LoggerFactory.getLogger(CarManagerBean.class);
//
//	private String carInstance;
//
//	@Inject
//	private Cache cache;
//
//	public CarType getCar() {
//		return cache.find(CarType.class);
//	}
//
//	public void addInstance() {
//		getCar().addInstance(getCarInstance());
//	}
//
//	public void deleteInstance(Generic generic) {
//		generic.remove();
//	}
//
//	public Snapshot<Generic> getCarInstances() {
//		return getCar().getAllInstances();
//	}
//
//	public String getCarInstance() {
//		return carInstance;
//	}
//
//	public void setCarInstance(String carInstance) {
//		this.carInstance = carInstance;
//	}
//
// }
