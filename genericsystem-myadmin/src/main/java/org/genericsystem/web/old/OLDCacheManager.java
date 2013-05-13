package org.genericsystem.web.old;
//package org.genericsystem.web.beans;
//
//import java.io.Serializable;
//
//import javax.faces.bean.ViewScoped;
//import javax.inject.Inject;
//import javax.inject.Named;
//
//import org.genericsystem.cdi.CacheProvider;
//import org.genericsystem.core.Cache;
//import org.slf4j.Logger;
//
//@Named
//@ViewScoped
//public class CacheManager implements Serializable {
//
//	private static final long serialVersionUID = 3799172132114867212L;
//
//	@Inject
//	private Logger log;
//
//	// @Inject
//	// private Conversation conversation;
//
//	// @Inject
//	// @DiscardEvent
//	// private Event<Generic> discardEvent;
//
//	// @Inject
//	// private TypesManager typesManager;
//
//	@Inject
//	private Cache cache;
//
//	@Inject
//	private CacheProvider cacheProvider;
//
//	public void discard() {
//		cacheProvider.init();
//		// if (typesManager.getSelectedType() != null)
//		// discardEvent.fire(typesManager.getSelectedType());
//		// try {
//		// FacesContext.getCurrentInstance().getExternalContext().redirect("/gsmyadmin/pages/index.xhtml");
//		// } catch (IOException e) {
//		// log.info(e.getMessage(), e.getCause());
//		// }
//	}
//
//	public void save() {
//		cache.flush();
//	}
//
// }
