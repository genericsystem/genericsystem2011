package org.genericsystem.framework;

import java.util.List;
import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot.Projector;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;

public class InstanceRow {

	private final Generic instance;

	public InstanceRow(Generic instance) {
		this.instance = instance;
	}

	public List<String> getAttributeValues(final Attribute attribute) {
		if (attribute.isRelation()) {
			return getInstance().getHolders(attribute).project(new Projector<String, Holder>() {
				@Override
				public String project(Holder link) {
					return getInstance().getOtherTargets(link).get(0).getValue();
				}
			});
		} else
			return getInstance().getValues((Holder) attribute);
	}

	@Override
	public String toString() {
		return Objects.toString(instance);
	}

	public Generic getInstance() {
		return instance;
	}

}