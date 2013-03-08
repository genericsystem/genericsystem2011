package org.genericsystem.web.beans;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Type;
import org.genericsystem.web.qualifiers.TypeChangeEvent;

@Named
@ConversationScoped
public class AttributesManager implements Serializable {

	private static final long serialVersionUID = -3449771217822982572L;

	private List<Attribute> attributes;

	@Named
	@Produces
	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public void buildAttributes(@Observes @TypeChangeEvent final Type type) {
		// attributes = new AbstractSequentialList<Attribute>() {
		// @SuppressWarnings({ "unchecked", "rawtypes" })
		// @Override
		// public Iterator<Attribute> iterator() {
		// return (Iterator) type.getAttributes().iterator();
		// }
		// };
	}
}
