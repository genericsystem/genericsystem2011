package org.genericsystem.web.old;
//package org.genericsystem.web.beans;
//
//import java.io.Serializable;
//
//import javax.enterprise.context.ConversationScoped;
//import javax.enterprise.event.Event;
//import javax.enterprise.event.Observes;
//import javax.enterprise.inject.Produces;
//import javax.inject.Inject;
//import javax.inject.Named;
//
//import org.genericsystem.core.Generic;
//import org.genericsystem.generic.Holder;
//import org.genericsystem.generic.Property;
//import org.genericsystem.generic.Type;
//import org.genericsystem.web.qualifiers.DiscardEvent;
//import org.genericsystem.web.qualifiers.InstanceDeleteEvent;
//import org.genericsystem.web.qualifiers.TypeChangeEvent;
//import org.slf4j.Logger;
//
//@Named
//@ConversationScoped
//public class SelectedInstanceManager implements Serializable {
//
//	private static final long serialVersionUID = -1013241212816652508L;
//
//	private Generic selectedInstance;
//
//	@Inject
//	private Logger log;
//
//	@Inject
//	private TypesManager typesManager;
//
//	@Inject
//	@TypeChangeEvent
//	private Event<Type> typeChangeEvent;
//
//	public void applySelectedInstance(Generic instance) {
//		typeChangeEvent.fire(typesManager.getSelectedType());
//		this.selectedInstance = instance;
//	}
//
//	// Avoid SuperRuleConstraintViolationException when there's a Type
//	// modification
//	public void onDiscardEvent(@Observes @DiscardEvent Generic generic) {
//		this.selectedInstance = null;
//	}
//
//	public void onInstanceDeleteEvent(@Observes @InstanceDeleteEvent Generic generic) {
//		this.selectedInstance = null;
//	}
//
//	public void onTypeChangeEvent(@Observes @TypeChangeEvent Type type) {
//		log.info("onTypeChange: " + type);
//		this.selectedInstance = null;
//	}
//
//	@Named
//	@Produces
//	public Generic getSelectedInstance() {
//		return selectedInstance;
//	}
//
//	public Wrapper getWrapper(Property property) {
//		return new Wrapper(property);
//	}
//
//	public class Wrapper {
//
//		private Property property;
//
//		public Wrapper(Property property) {
//			this.property = property;
//		}
//
//		public String getValue() {
//			log.info("getValue()");
//			Holder valueHolder = selectedInstance.getLink(property);
//			return valueHolder == null ? "" : "" + valueHolder.getImplicit();
//		}
//
//		public void setValue(String value) {
//			log.info("setValue(String value) : " + value);
//			// ((Property) selectedInstance).setPropertyValue(property, value);
//		}
//	}
//
// }
