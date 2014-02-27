package org.genericsystem.jsf.example.component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot.Projector;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;

public class InstanceRowComponent extends AbstractComponent {
	private final Generic instance;

	public InstanceRowComponent(AbstractComponent parent, Generic instance) {
		super(parent);
		this.instance = instance;
	}

	public Generic getInstance() {
		return instance;
	}

	public String getValue() {
		return Objects.toString(instance.getValue());
	}

	public List<String> getAttributeValues(final Attribute attribute) {
		if (attribute.isRelation()) {
			return instance.getHolders(attribute).project(new Projector<String, Holder>() {
				@Override
				public String project(Holder link) {
					return instance.getOtherTargets(link).get(0).getValue();
				}
			});
		} else
			return instance.getValues((Holder) attribute);
	}

	@Override
	public List<? extends AbstractComponent> initChildren() {
		return Collections.emptyList();
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/genericComponent.xhtml";
	}

}