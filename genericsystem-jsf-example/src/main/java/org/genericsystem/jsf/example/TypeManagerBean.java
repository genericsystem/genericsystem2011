package org.genericsystem.jsf.example;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Snapshot;
import org.genericsystem.generic.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@SessionScoped
public class TypeManagerBean implements Serializable {
	private static final long serialVersionUID = 8493992142514268751L;
	protected static Logger log = LoggerFactory.getLogger(TypeManagerBean.class);

	private String newTypeString;
	private boolean sortAscending = true;

	@Inject
	private Cache cache;

	public void addNewType() {
		Type newType = cache.addType(getNewTypeString());
	}

	public Snapshot<Type> getAllAvailableTypes() {
		return (cache.getAllTypes());
	}

	public void sortByName() {
		if (sortAscending) {
			// ascending order
			Collections.sort(new ArrayList<>(getAllAvailableTypes()), new Comparator<Type>() {
				@Override
				public int compare(Type t1, Type t2) {
					return t1.getValue().toString().compareTo(t2.getValue().toString());
				}
			});
			sortAscending = false;
		} else {
			// descending order
			Collections.sort(new ArrayList<>(getAllAvailableTypes()), new Comparator<Type>() {
				@Override
				public int compare(Type t1, Type t2) {
					return t2.getValue().toString().compareTo(t1.getValue().toString());
				}
			});
			sortAscending = true;
		}
	}

	public String getNewTypeString() {
		return newTypeString;
	}

	public void setNewTypeString(String newTypeString) {
		this.newTypeString = newTypeString;
	}

}
