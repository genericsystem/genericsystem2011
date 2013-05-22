package org.genericsystem.myadmin.backbeans;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;
import org.genericsystem.core.CacheImpl;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.generic.Type;
import org.genericsystem.myadmin.backbeans.ITreeNode.GenericTreeNode;
import org.genericsystem.myadmin.beans.TreeBean.TreeSelectionEvent;
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

	public List<GenericTreeNode> getRoot() {
		return Collections.singletonList(new GenericTreeNode(null, cache.getEngine()));
	}

	public List<ITreeNode> getDirectSubTypes(final ITreeNode iTreeNode) {
		return iTreeNode.getChildrens(cache);
	}

	public List<Generic> getInstances(final Type type) {
		return type.getInstances(cache).toList();
	}

	public void changeType(@Observes @TreeSelection TreeSelectionEvent treeSelectionEvent) {
		if (treeSelectionEvent.getId().equals("typestree")) {
			ITreeNode iTreeNode = (ITreeNode) treeSelectionEvent.getObject();
			if (iTreeNode instanceof GenericTreeNode) {
				selectedType = ((GenericTreeNode) iTreeNode).getGeneric();
				messages.info("selectionchanged", messages.getMessage("type"), selectedType.toString());
			}
		}
	}

	public void newType(String newValue) {
		cache.newType(newValue);
		messages.info("createRoot", messages.getMessage("type"), newValue);
	}

	public void newSubType(String newValue) {
		((Type) selectedType).newSubType(cache, newValue);
		messages.info("createSub", messages.getMessage("type"), newValue, selectedType.getValue());
	}

	public void addAttribute(String newValue) {
		((Type) selectedType).addAttribute(cache, newValue);
		messages.info("createRoot", messages.getMessage("attribute"), newValue, selectedType.getValue());
	}

	public void newInstance(String newValue) {
		((Type) selectedType).newInstance(cache, newValue);
		messages.info("createRoot", messages.getMessage("instance"), newValue, selectedType.getValue());
	}

	public Wrapper getWrapper(ITreeNode iTreeNode) {
		return new Wrapper(iTreeNode);
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

	public GenericImpl getSelectedType() {
		return (GenericImpl) selectedType;
	}

	public void setSelectedType(Generic selectedType) {
		this.selectedType = selectedType;
	}

	public class Wrapper {
		private ITreeNode iTreeNode;

		public Wrapper(ITreeNode iTreeNode) {
			this.iTreeNode = iTreeNode;
		}

		public String getValue() {
			return iTreeNode.getValue();
		}

		public void setValue(String newValue) {
			if (iTreeNode instanceof GenericTreeNode) {
				Generic generic = ((GenericTreeNode) iTreeNode).getGeneric();
				if (!newValue.equals(generic.toString())) {
					selectedType = ((CacheImpl) cache).update(generic, newValue);
					messages.info("updateShortPath", newValue, generic.getValue());
				}
			}
		}
	}
}
