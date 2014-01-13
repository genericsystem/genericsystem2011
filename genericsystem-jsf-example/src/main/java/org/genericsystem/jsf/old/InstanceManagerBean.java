//package org.genericsystem.jsf.old;
//
//import java.io.Serializable;
//
//import javax.enterprise.context.SessionScoped;
//import javax.inject.Named;
//
//import org.genericsystem.generic.Type;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//@Named(value = "instance")
//@SessionScoped
//public class InstanceManagerBean implements Serializable {
//	private static final long serialVersionUID = -790637948959259101L;
//
//	protected static Logger log = LoggerFactory.getLogger(TypeManagerBean.class);
//
//	private String newInstanceString;
//
//	public void addNewInstance(Type type) {
//		type.addInstance(getNewInstanceString());
//		log.info(type.getAllInstances().toString());
//	}
//
//	public String getNewInstanceString() {
//		return newInstanceString;
//	}
//
//	public void setNewInstanceString(String newInstanceString) {
//		this.newInstanceString = newInstanceString;
//	}
//
// }
