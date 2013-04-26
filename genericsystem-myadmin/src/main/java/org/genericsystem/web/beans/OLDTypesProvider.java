package org.genericsystem.web.beans;
//package org.genericsystem.web.beans;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Iterator;
//import java.util.List;
//
//import javax.enterprise.context.ConversationScoped;
//import javax.enterprise.inject.Produces;
//import javax.inject.Inject;
//import javax.inject.Named;
//
//import org.genericsystem.core.Cache;
//import org.genericsystem.core.Generic;
//import org.genericsystem.generic.Type;
//import org.genericsystem.web.util.AbstractSequentialList;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//@Named
//@ConversationScoped
//public class TypesProvider implements Serializable {
//
//	private static final long serialVersionUID = 8523560809293368502L;
//
//	protected static Logger log = LoggerFactory.getLogger(AbstractSequentialList.class);
//
//	@Inject
//	private Cache cacheContext;
//
//	@Produces
//	public List<Generic> getTypes() {
//		return new AbstractSequentialList<Generic>() {
//
//			@Override
//			public Iterator<Generic> iterator() {
//				return cacheContext.getEngine().getSubTypes(cacheContext).iterator();
//			}
//		};
//	}
//
//	/* rich:tree */
//	public synchronized List<Generic> getRoots() {
//		return Collections.<Generic> singletonList(cacheContext.getEngine());
//	}
//
//	public List<Generic> getTypes(final Generic generic) {
//		return new AbstractSequentialList<Generic>() {
//			@Override
//			public Iterator<Generic> iterator() {
//				return ((Type) generic).getSubTypes(cacheContext).iterator();
//			}
//		};
//	}
//
//	public List<Generic> getTypesWithImplicit(final Generic generic) {
//		return new ArrayList<>();
//		// return new AbstractSequentialList<Generic>() {
//		// @Override
//		// public Iterator<Generic> iterator() {
//		// return ((Type) generic).getSubTypesWithImplicit().iterator();
//		// }
//		// };
//	}
// }
