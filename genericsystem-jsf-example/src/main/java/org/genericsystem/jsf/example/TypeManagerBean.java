package org.genericsystem.jsf.example;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Snapshot;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.generic.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named(value = "type")
@SessionScoped
public class TypeManagerBean implements Serializable {
	private static final long serialVersionUID = 8493992142514268751L;
	protected static Logger log = LoggerFactory.getLogger(TypeManagerBean.class);

	private String newTypeString;
	private boolean sortAscending = true;

	@Inject
	private Cache cache;

	public void addNewType() {
		cache.addType(getNewTypeString());
	}

	public Snapshot<Type> getAllAvailableTypes() {
		return (cache.getAllTypes().filter(new Filter<Type>() {

			@Override
			public boolean isSelected(Type type) {
				return !type.isSystem();
			}
		}));
	}

	public void deleteType(Type type) {
		type.remove();
	}

	public void sortByName() {
		List<Type> sortedTypesList = new ArrayList<>(getAllAvailableTypes());

		if (sortAscending) {
			// ascending order
			Collections.sort(sortedTypesList, new Comparator<Type>() {
				@Override
				public int compare(Type t1, Type t2) {
					return t1.getValue().toString().compareTo(t2.getValue().toString());
				}
			});
			sortAscending = false;
			// log.info(sortedTypesList.toString());
		} else {
			// descending order
			Collections.sort(new ArrayList<>(getAllAvailableTypes()), new Comparator<Type>() {
				@Override
				public int compare(Type t1, Type t2) {
					return t2.getValue().toString().compareTo(t1.getValue().toString());
				}
			});
			sortAscending = true;
			// log.info(sortedTypesList.toString());
		}
	}

	public String getNewTypeString() {
		return newTypeString;
	}

	public void setNewTypeString(String newTypeString) {
		this.newTypeString = newTypeString;
	}

}
