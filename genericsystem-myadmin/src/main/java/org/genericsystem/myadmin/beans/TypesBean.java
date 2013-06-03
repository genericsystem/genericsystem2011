package org.genericsystem.myadmin.beans;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;
import org.genericsystem.core.CacheImpl;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.genericsystem.myadmin.beans.GenericTreeNode.TreeType;
import org.genericsystem.myadmin.beans.PanelBean.PanelTitleChangeEvent;
import org.genericsystem.myadmin.beans.TreeBean.TreeSelectionEvent;
import org.genericsystem.myadmin.util.GsMessages;
import org.genericsystem.myadmin.util.GsRedirect;

@Named
@SessionScoped
public class TypesBean implements Serializable {

	private static final long serialVersionUID = 8042406937175946234L;

	// TODO clean
	// private static Logger log = LoggerFactory.getLogger(TypesBean.class);

	@Inject
	private transient Cache cache;

	@Inject
	private GsMessages messages;

	@Inject
	private GsRedirect redirect;

	private GenericTreeNode selectedTreeNode;

	@Inject
	private Event<PanelTitleChangeEvent> panelTitleChangeEvent;

	private GenericTreeNode rootTreeNode;

	private boolean implicitShow;

	@PostConstruct
	public void init() {
		rootTreeNode = new GenericTreeNode(null, cache.getEngine(), GenericTreeNode.TreeType_DEFAULT);
		selectedTreeNode = rootTreeNode;

		// TEST
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Attribute power = vehicle.addAttribute(cache, "power");
		Relation vehicleColor = vehicle.setRelation(cache, "vehicleColor", color);
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		Generic red = color.newInstance(cache, "red");
		Generic yellow = color.newInstance(cache, "yellow");
		myVehicle.setValue(cache, power, 123);
		myVehicle.setValue(cache, power, 136);
		myVehicle.setLink(cache, vehicleColor, "myVehicleRed", red);
		myVehicle.bind(cache, vehicleColor, yellow);
	}

	public List<GenericTreeNode> getRoot() {
		return Collections.singletonList(rootTreeNode);
	}

	public List<GenericTreeNode> getChildrens(final GenericTreeNode genericTreeNode) {
		return genericTreeNode.getChildrens(cache, implicitShow);
	}

	public void changeType(@Observes/* @TreeSelection */TreeSelectionEvent treeSelectionEvent) {
		if (treeSelectionEvent.getId().equals("typestree")) {
			selectedTreeNode = (GenericTreeNode) treeSelectionEvent.getObject();
			panelTitleChangeEvent.fire(new PanelTitleChangeEvent("typesmanager", ((GenericImpl) getSelectedTreeNodeGeneric()).toCategoryString()));
			messages.info("typeselectionchanged", getSelectedTreeNodeGeneric().toString());
		}
	}

	public void changeTreeType(TreeType treeType) {
		selectedTreeNode.setTreeType(treeType);
		messages.info("showchanged", treeType);
	}

	public void newType(String newValue) {
		cache.newType(newValue);
		messages.info("createRootType", newValue);
	}

	public void newSubType(String newValue) {
		((Type) getSelectedTreeNodeGeneric()).newSubType(cache, newValue);
		messages.info("createSubType", newValue, getSelectedTreeNodeGeneric().getValue());
	}

	public void addAttribute(String newValue) {
		((Type) getSelectedTreeNodeGeneric()).addAttribute(cache, newValue);
		messages.info("createRootAttribute", newValue, getSelectedTreeNodeGeneric().getValue());
	}

	public void newInstance(String newValue) {
		((Type) getSelectedTreeNodeGeneric()).newInstance(cache, newValue);
		messages.info("createRootInstance", newValue, getSelectedTreeNodeGeneric().getValue());
	}

	public String delete() {
		selectedTreeNode.getGeneric().remove(cache);
		selectedTreeNode = null;
		redirect.redirectInfo("deleteFile", getSelectedTreeNodeGeneric().getValue());
		return "HOME";
	}

	public boolean isTreeNodeSelected() {
		return selectedTreeNode == null;
	}

	public Generic getSelectedTreeNodeGeneric() {
		return selectedTreeNode.getGeneric();
	}

	public String getSelectedTreeNodeValue() {
		return selectedTreeNode.getValue();
	}

	public boolean isTreeTypeSelected(TreeType treeType) {
		return selectedTreeNode != null && selectedTreeNode.getTreeType() == treeType;
	}

	public boolean isImplicitShow() {
		return implicitShow;
	}

	public void setImplicitShow(boolean implicitShow) {
		this.implicitShow = implicitShow;
	}

	public Wrapper getWrapper(GenericTreeNode genericTreeNode) {
		return new Wrapper(genericTreeNode);
	}

	public class Wrapper {
		private GenericTreeNode genericTreeNode;

		public Wrapper(GenericTreeNode genericTreeNode) {
			this.genericTreeNode = genericTreeNode;
		}

		public String getValue() {
			return genericTreeNode.getValue();
		}

		public void setValue(String newValue) {
			Generic generic = genericTreeNode.getGeneric();
			if (!newValue.equals(generic.toString())) {
				genericTreeNode.setGeneric(((CacheImpl) cache).update(generic, newValue));
				messages.info("updateShortPath", newValue, generic.getValue());
			}
		}
	}

	public String getExpandedIcon() {
		return getExpandedIcon(selectedTreeNode);
	}

	public String getExpandedIcon(GenericTreeNode genericTreeNode) {
		switch (genericTreeNode.getTreeType()) {
		case SUPERS:
			return messages.getInfos("up_green_arrow");
		case INSTANCES:
			return messages.getInfos("down_green_arrow");
		case INHERITINGS:
			return messages.getInfos("down_right_green_arrow");
		case COMPONENTS:
			return messages.getInfos("left_green_arrow");
		case COMPOSITES:
			return messages.getInfos("right_green_arrow");
		case ATTRIBUTES:
			return messages.getInfos("up_right_green_arrow");
		case VALUES:
			return messages.getInfos("right_green_arrow");
		default:
			break;
		}
		throw new IllegalStateException();
	}

	public String getCollapsedIcon() {
		return getCollapsedIcon(selectedTreeNode);
	}

	public String getCollapsedIcon(GenericTreeNode genericTreeNode) {
		switch (genericTreeNode.getTreeType()) {
		case SUPERS:
			return messages.getInfos("up_red_arrow");
		case INSTANCES:
			return messages.getInfos("down_red_arrow");
		case INHERITINGS:
			return messages.getInfos("down_right_red_arrow");
		case COMPONENTS:
			return messages.getInfos("left_red_arrow");
		case COMPOSITES:
			return messages.getInfos("right_red_arrow");
		case ATTRIBUTES:
			return messages.getInfos("up_right_red_arrow");
		case VALUES:
			return messages.getInfos("right_red_arrow");
		default:
			break;
		}
		throw new IllegalStateException();
	}

	public String getIconTitle() {
		return getIconTitle(selectedTreeNode);
	}

	public String getIconTitle(GenericTreeNode genericTreeNode) {
		switch (genericTreeNode.getTreeType()) {
		case SUPERS:
			return messages.getMessage("super");
		case INSTANCES:
			return messages.getMessage("instance");
		case INHERITINGS:
			return messages.getMessage("inheriting");
		case COMPONENTS:
			return messages.getMessage("component");
		case COMPOSITES:
			return messages.getMessage("composite");
		case ATTRIBUTES:
			return messages.getMessage("attribute");
		case VALUES:
			return messages.getMessage("value");
		default:
			break;
		}
		throw new IllegalStateException();
	}

	public String getTypeIcon(GenericTreeNode genericTreeNode) {
		Generic generic = genericTreeNode.getGeneric();
		if (generic.isMeta()) {
			if (generic.isType())
				return messages.getInfos("bullet_square_red");
			if (generic.isReallyAttribute())
				return messages.getInfos("bullet_triangle_red");
			if (generic.isRelation())
				return messages.getInfos("bullet_ball_red");
		} else if (generic.isStructural()) {
			if (generic.isType())
				return messages.getInfos("bullet_square_yellow");
			if (generic.isReallyAttribute())
				return messages.getInfos("bullet_triangle_yellow");
			if (generic.isRelation())
				return messages.getInfos("bullet_ball_yellow");
		} else if (generic.isConcrete()) {
			if (generic.isType())
				return messages.getInfos("bullet_square_green");
			if (generic.isReallyAttribute())
				return messages.getInfos("bullet_triangle_green");
			if (generic.isRelation())
				return messages.getInfos("bullet_ball_green");
		}
		throw new IllegalStateException();
	}

	public String getTypeIconTitle(GenericTreeNode genericTreeNode) {
		Generic generic = genericTreeNode.getGeneric();
		if (generic.isMeta() && generic.isType())
			return messages.getMessage("meta") + " " + messages.getMessage("type");
		else if (generic.isMeta() && generic.isReallyAttribute())
			return messages.getMessage("meta") + " " + messages.getMessage("attribute");
		else if (generic.isMeta() && generic.isRelation())
			return messages.getMessage("meta") + " " + messages.getMessage("relation");
		else if (generic.isStructural() && generic.isType())
			return messages.getMessage("type");
		else if (generic.isStructural() && generic.isReallyAttribute())
			return messages.getMessage("attribute");
		else if (generic.isStructural() && generic.isRelation())
			return messages.getMessage("relation");
		else if (generic.isConcrete() && generic.isType())
			return messages.getMessage("instance");
		else if (generic.isConcrete() && generic.isReallyAttribute())
			return messages.getMessage("value");
		else if (generic.isConcrete() && generic.isRelation())
			return messages.getMessage("link");
		throw new IllegalStateException();
	}

	public String getStyle(GenericTreeNode genericTreeNode) {
		return genericTreeNode.isImplicitAutomatic(genericTreeNode.getGeneric()) ? "implicitColor" : "";
	}

	public List<Attribute> getAttributes() {
		return ((Type) selectedTreeNode.getGeneric()).getAttributes(cache).toList();
	}

	public List<Holder> getValues(Attribute attribute) {
		return ((Type) selectedTreeNode.getGeneric()).getHolders(cache, attribute).toList();
	}

	public void addValue(Attribute attribute, String newValue) {
		Generic currentInstance = getSelectedTreeNodeGeneric();
		currentInstance.setValue(cache, attribute, newValue);
		messages.info("addValue", newValue, attribute, currentInstance);
	}

	public void remove(Generic generic) {
		generic.remove(cache);
		// TODO add messages
		// messages.info("addValue", newValue, attribute, currentInstance);
	}
}
