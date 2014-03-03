package org.genericsystem.jsf.example.component;

import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot.Projector;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;

public class InstanceRowComponent extends GenericComponent {

	public InstanceRowComponent(AbstractComponent parent, Generic selected) {
		super(parent, selected);
	}

	public List<String> getAttributeValues(final Attribute attribute) {
		if (attribute.isRelation()) {
			return selected.getHolders(attribute).project(new Projector<String, Holder>() {
				@Override
				public String project(Holder link) {
					return selected.getOtherTargets(link).get(0).getValue();
				}
			});
		} else
			return selected.getValues((Holder) attribute);
	}

}