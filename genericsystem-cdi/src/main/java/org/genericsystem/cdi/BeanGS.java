package org.genericsystem.cdi;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;

@SessionScoped
@Named
public class BeanGS implements Serializable {

	private static final long serialVersionUID = 5417152822444949996L;

	@Inject
	private CacheProvider cacheProvider;

	private Map<Serializable, Generic> generics = new HashMap<>();

	public void newType(Serializable value) {
		generics.put(value, cacheProvider.getCache().newType(value));
	}

	public Type getType(Serializable typeValue) {
		return cacheProvider.getCache().getType(typeValue);
	}

	public Attribute getAttribute(Serializable base, Serializable attributeValue) {
		return ((Type) generics.get(base)).getAttribute(cacheProvider.getCache(), attributeValue);
	}

	public Snapshot<Attribute> getAttributes(Serializable base) {
		return ((Type) generics.get(base)).getAttributes(cacheProvider.getCache());
	}

	public void setAttribute(Serializable base, Serializable value) {
		generics.put(value, ((Type) generics.get(base)).setAttribute(cacheProvider.getCache(), value));
	}

	public Relation getRelation(Serializable base, Serializable relationValue) {
		return ((Type) generics.get(base)).getRelation(cacheProvider.getCache(), relationValue);
	}

	public Snapshot<Relation> getRelations(Serializable base) {
		return ((Type) generics.get(base)).getRelations(cacheProvider.getCache());
	}

	// TODO KK
	// public void setRelation(Serializable base, Serializable value, Serializable... targetsValue) {
	public void setRelation(Serializable base, Serializable value, Serializable targetValue) {
		Cache cache = cacheProvider.getCache();
		// Type[] targets = new Type[targetsValue.length];
		// for (int i = 0; i < targetsValue.length; i++)
		// targets[i] = (Type) generics.get(targetsValue[i]);
		// generics.put(value, ((Type) generics.get(base)).setRelation(cache, value, targets));
		generics.put(value, ((Type) generics.get(base)).setRelation(cache, value, ((Type) generics.get(targetValue))));
	}

	public void newInstance(Serializable base, Serializable value) {
		generics.put(value, ((Type) generics.get(base)).newInstance(cacheProvider.getCache(), value));
	}

	public Serializable getValue(Serializable base, Serializable attributeValue) {
		return ((Type) generics.get(base)).getValue(cacheProvider.getCache(), ((Attribute) generics.get(attributeValue)));
	}

	public void setValue(Serializable base, Serializable attributeValue, Serializable value) {
		generics.put(value, ((Type) generics.get(base)).setValue(cacheProvider.getCache(), ((Attribute) generics.get(attributeValue)), value));
	}

	public Link getLink(Serializable base, Serializable relationValue) {
		return generics.get(base).getLink(cacheProvider.getCache(), (Relation) generics.get(relationValue));
	}

	public Snapshot<Link> getLinks(Serializable base, Serializable relationValue) {
		return generics.get(base).getLinks(cacheProvider.getCache(), (Relation) generics.get(relationValue));
	}

	public void setLink(Serializable base, Serializable relationValue, Serializable value, Serializable targetValue) {
		generics.put(value, generics.get(base).setLink(cacheProvider.getCache(), (Relation) generics.get(relationValue), value, generics.get(targetValue)));
	}
}
