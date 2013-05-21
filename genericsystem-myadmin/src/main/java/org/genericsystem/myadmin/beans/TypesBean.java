package org.genericsystem.myadmin.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;
import org.genericsystem.core.CacheImpl;
import org.genericsystem.core.Generic;
import org.genericsystem.generic.Type;
import org.genericsystem.myadmin.beans.qualifier.TreeSelection;
import org.genericsystem.myadmin.util.GsMessages;
import org.genericsystem.myadmin.util.GsRedirect;

@Named
@SessionScoped
public class TypesBean implements Serializable {

	private static final long serialVersionUID = 8042406937175946234L;

	@Inject
	private transient Cache cache;

	@Inject
	private GsMessages messages;

	@Inject
	GsRedirect redirect;

	private Generic selectedType;

	public List<Type> getRoot() {
		return new ArrayList<Type>() {
			private static final long serialVersionUID = -6764937845089064529L;

			{
				add(cache.getEngine());
			}
		};
	}

	public List<Type> getDirectSubTypes(final Type type) {
		return type.<Type> getDirectSubTypes(cache).toList();
	}

	public List<Generic> getInstances(final Type type) {
		return type.getInstances(cache).toList();
	}

	public void changeType(@Observes @TreeSelection Generic generic) {
		selectedType = generic;
		messages.info("selectionchanged", messages.getMessage("type"), selectedType.getValue());
	}

	public void newType(String newValue) {
		cache.newType(newValue);
		messages.info("createRootDirectory", newValue);
	}

	public void newSubType(String newValue) {
		((Type) selectedType).newSubType(cache, newValue);
		messages.info("createSubDirectory", newValue, selectedType.getValue());
	}

	public void newInstance(String newValue) {
		((Type) selectedType).newInstance(cache, newValue);
		messages.info("createFile", newValue, selectedType.getValue());
	}

	public Wrapper getWrapper(Generic generic) {
		return new Wrapper(generic);
	}

	public String delete() {
		selectedType.remove(cache);
		selectedType = null;
		redirect.redirectInfo("deleteFile", selectedType.getValue());
		return "HOME";
	}

	public boolean isType() {
		return selectedType != null && selectedType.isType();
	}

	public boolean isInstance() {
		// TODO in core
		return selectedType != null && selectedType.getComponentsSize() == 0 && selectedType.isConcrete();
	}

	public class Wrapper {
		private Generic generic;

		public Wrapper(Generic generic) {
			this.generic = generic;
		}

		public String getValue() {
			return generic.toString();
		}

		public void setValue(String newValue) {
			if (!newValue.equals(generic.toString())) {
				selectedType = ((CacheImpl) cache).update(generic, newValue);
				messages.info("updateShortPath", newValue, generic.getValue());
			}
		}
	}

}
