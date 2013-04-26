package org.genericsystem.web.beans;
//package org.genericsystem.web.beans;
//
//import java.io.Serializable;
//import java.util.List;
//
//import javax.enterprise.context.ConversationScoped;
//import javax.enterprise.event.Event;
//import javax.enterprise.event.Observes;
//import javax.inject.Inject;
//import javax.inject.Named;
//
//import org.genericsystem.core.Generic;
//import org.genericsystem.generic.Type;
//import org.genericsystem.web.qualifiers.InstanceDeleteEvent;
//import org.slf4j.Logger;
//
//@Named
//@ConversationScoped
//public class InstancesManager implements Serializable {
//
//	private static final long serialVersionUID = -6156952255935245663L;
//
//	@Inject
//	private TypesManager typesManager;
//
//	@Inject
//	private Logger log;
//
//	@Inject
//	@InstanceDeleteEvent
//	private Event<Generic> instanceDeleteEvent;
//
//	private List<Generic> instances;
//
//	public List<Generic> getInstances() {
//		return instances;
//	}
//
//	public void remove(Generic instance) {
//		instanceDeleteEvent.fire(typesManager.getSelectedType());
//		// instance.remove();
//	}
//
//	public void onChangeType(@Observes final Type type) {
//		log.info("onChangeType:" + type);
//		// instances = new AbstractSequentialList<Generic>() {
//		// @Override
//		// public Iterator<Generic> iterator() {
//		// return type.getAllInstances().iterator();
//		// }
//		// };
//	}
//
//	private String newInstance;
//
//	public String getNewInstance() {
//		return newInstance;
//	}
//
//	public void setNewInstance(String newInstance) {
//		this.newInstance = newInstance;
//	}
//
//	public void add() {
//		// if (!"".equals(getNewInstance())) {
//		// this.typesManager.getSelectedType().newInstance(getNewInstance());
//		// }
//	}
// }
