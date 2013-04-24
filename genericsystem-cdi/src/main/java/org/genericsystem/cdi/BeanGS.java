package org.genericsystem.cdi;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;

@SessionScoped
@Named
public class BeanGS implements Serializable {

	private static final long serialVersionUID = 5417152822444949996L;

	@Inject
	private Cache cache;

	private Type editType;

	private Generic editInstance;

	public Type getEditType() {
		return editType;
	}

	public void setEditType(Serializable typeValue) {
		editType = getType(typeValue);
		if (editType == null)
			throw new IllegalStateException("not find the type " + typeValue);
	}

	public Generic getEditInstance() {
		return editInstance;
	}

	public void setEditInstance(Serializable instanceValue) {
		editInstance = getInstance(instanceValue);
		if (editInstance == null)
			throw new IllegalStateException("not find the instance " + instanceValue + " for the type " + editType.info());
	}

	public void save() {
		cache.flush();
	}

	public Type getType(Serializable typeValue) {
		return cache.getType(typeValue);
	}

	public void newType(Serializable value) {
		cache.newType(value);
	}

	public Attribute getAttribute(Serializable attributeValue) {
		return editType.getAttribute(cache, attributeValue);
	}

	public Snapshot<Attribute> getAttributes() {
		return editType.getAttributes(cache);
	}

	public void setAttribute(Serializable value) {
		editType.setAttribute(cache, value);
	}

	public Relation getRelation(Serializable relationValue) {
		return editType.getRelation(cache, relationValue);
	}

	public Snapshot<Relation> getRelations() {
		return editType.getRelations(cache);
	}

	// TODO KK
	// public void setRelation(Serializable value, Serializable... targetsValue) {
	public void setRelation(Serializable value, Serializable targetValue) {
		// Type[] targets = new Type[targetsValue.length];
		// for (int i = 0; i < targetsValue.length; i++)
		// targets[i] = (Type) generics.get(targetsValue[i]);
		// generics.put(value, ((Type) generics.get(base)).setRelation(cache, value, targets));
		editType.setRelation(cache, value, getType(targetValue));
	}

	public Generic getInstance(final Serializable instanceValue) {
		return editType.getInstances(cache).filter(new Filter<Generic>() {

			@Override
			public boolean isSelected(Generic element) {
				return element.getValue().equals(instanceValue);
			}
		}).first();
	}

	public void newInstance(Serializable value) {
		editType.newInstance(cache, value);
	}

	public Serializable getValue(Serializable attributeValue) {
		return editInstance.getValue(cache, getAttribute(attributeValue));
	}

	public void setValue(Serializable attributeValue, Serializable value) {
		editInstance.setValue(cache, getAttribute(attributeValue), value);
	}

	public Link getLink(Serializable relationValue) {
		return editInstance.getLink(cache, getRelation(relationValue));
	}

	public Snapshot<Link> getLinks(Serializable relationValue) {
		return editInstance.getLinks(cache, getRelation(relationValue));
	}

	public void setLink(Serializable relationValue, Serializable value, Serializable targetValue) {
		editInstance.setLink(cache, getRelation(relationValue), value, getType(targetValue));
	}
}
