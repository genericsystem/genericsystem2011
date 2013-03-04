package org.genericsystem.web.beans;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.genericsystem.generic.Property;
import org.genericsystem.generic.Type;
import org.genericsystem.web.qualifiers.TypeChangeEvent;
import org.genericsystem.web.util.AbstractSequentialList;

@Named
@ConversationScoped
public class PropertiesManager implements Serializable {

	private static final long serialVersionUID = -3449771217822982572L;

	private List<Property> properties;

	@Named
	@Produces
	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	public void buildProperties(@Observes @TypeChangeEvent final Type type) {
		properties = new AbstractSequentialList<Property>() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Iterator<Property> iterator() {
				return (Iterator) type.getAttributes().iterator();
			}
		};
	}
}
