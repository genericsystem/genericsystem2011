package org.genericsystem.web.beans;
//package org.genericsystem.web.beans;
//
//import java.io.Serializable;
//import java.util.Iterator;
//import java.util.List;
//
//import javax.enterprise.context.ConversationScoped;
//import javax.enterprise.event.Observes;
//import javax.enterprise.inject.Produces;
//import javax.inject.Inject;
//import javax.inject.Named;
//
//import org.genericsystem.generic.Relation;
//import org.genericsystem.generic.Type;
//import org.genericsystem.web.qualifiers.TypeChangeEvent;
//import org.genericsystem.web.util.AbstractSequentialList;
//import org.slf4j.Logger;
//
//@Named
//@ConversationScoped
//public class RelationManager implements Serializable {
//
//	private static final long serialVersionUID = -272083317562903529L;
//	
//	@Inject
//	private Logger log;
//
//	private List<Relation> relations;
//
//	@Named
//	@Produces
//	public List<Relation> getRelations() {
//		return relations;
//	}
//
//	public void setRelations(List<Relation> relations) {
//		this.relations = relations;
//	}
//
//	public void buildRelations(@Observes @TypeChangeEvent final Type type) {
//		log.info("buildRelations of " + type);
//		relations = new AbstractSequentialList<Relation>() {
//			@SuppressWarnings({ "unchecked", "rawtypes" })
//			@Override
//			public Iterator<Relation> iterator() {
//				return (Iterator) type.getRelations().iterator();
//			}
//		};
//	}
// }
