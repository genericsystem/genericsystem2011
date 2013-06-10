package org.genericsystem.myadmin.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.el.MethodExpression;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.genericsystem.core.Cache;
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
import org.richfaces.component.UIMenuGroup;
import org.richfaces.component.UIMenuItem;
import org.richfaces.event.DropEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@SessionScoped
public class TypesBean implements Serializable {

	private static final long serialVersionUID = 8042406937175946234L;

	// TODO clean
	private static Logger log = LoggerFactory.getLogger(TypesBean.class);

	@Inject
	private transient Cache cache;

	@Inject
	private GsMessages messages;

	@Inject
	private GsRedirect redirect;

	private GenericTreeNode rootTreeNode;

	private GenericTreeNode selectedTreeNode;

	@Inject
	private Event<PanelTitleChangeEvent> panelTitleChangeEvent;

	private boolean implicitShow;

	private UIMenuGroup valuesMenuGroup;

	@PostConstruct
	public void init() {
		rootTreeNode = new GenericTreeNode(null, cache.getEngine(), GenericTreeNode.TreeType_DEFAULT);
		selectedTreeNode = rootTreeNode;
		valuesMenuGroup = (UIMenuGroup) FacesContext.getCurrentInstance().getApplication().createComponent(UIMenuGroup.COMPONENT_TYPE);
		valuesMenuGroup.setLabel("show values ...");

		// TODO TEST
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Type color = cache.newType("Color");
		Type time = cache.newType("Time");
		Attribute power = vehicle.addAttribute(cache, "power");
		Relation vehicleColor = vehicle.setRelation(cache, "vehicleColor", color);
		Relation vehicleColorTime = vehicle.setRelation(cache, "vehicleColorTime", color, time);
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		Generic red = color.newInstance(cache, "red");
		Generic yellow = color.newInstance(cache, "yellow");
		vehicle.setValue(cache, power, 123);
		myVehicle.setValue(cache, power, 136);
		myVehicle.setLink(cache, vehicleColor, "myVehicleRed", red);
		myVehicle.bind(cache, vehicleColorTime, red, time.newInstance(cache, "myTime"));
		vehicle.bind(cache, vehicleColor, yellow);
		car.newInstance(cache, "myCar");

		Type human = cache.newType("Human");
		Generic nicolas = human.newInstance(cache, "Nicolas");
		Generic michael = human.newInstance(cache, "Michael");
		Generic michaelBrother = human.newInstance(cache, "MichaelBrother");
		Relation isTallerOrEqualThan = human.setRelation(cache, "isTallerOrEqualThan", human);
		nicolas.bind(cache, isTallerOrEqualThan, michael);
		nicolas.bind(cache, isTallerOrEqualThan, nicolas);
		Relation isBrotherOf = human.setRelation(cache, "isBrotherOf", human);
		isBrotherOf.enableMultiDirectional(cache); // bug
		michaelBrother.bind(cache, isBrotherOf, michael);
		// Generic michaelBrother2 = human.newInstance(cache, "MichaelBrother2");
		// michaelBrother2.bind(cache, isBrotherOf, michael);
		Relation isBossOf = human.setRelation(cache, "isBossOf", human);
		nicolas.bind(cache, isBossOf, michael);
	}

	public List<GenericTreeNode> getRoot() {
		return Collections.singletonList(rootTreeNode);
	}

	public List<GenericTreeNode> getChildrens(final GenericTreeNode genericTreeNode) {
		return genericTreeNode.getChildrens(cache, implicitShow);
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

	public List<Attribute> getAttributes() {
		return ((Type) selectedTreeNode.getGeneric()).getAttributes(cache).toList();
	}

	public List<Holder> getValues(Attribute attribute) {
		return ((Type) selectedTreeNode.getGeneric()).getHolders(cache, attribute).toList();
	}

	public List<Generic> getTargets(Attribute attribute, final Holder holder) {
		List<Generic> componentsList = new ArrayList<>();
		Generic[] components = ((GenericImpl) holder).getComponentsArray();
		int pos = selectedTreeNode.getGeneric().getBasePos(attribute, components);
		for (int i = 0; i < components.length; i++)
			if (i != pos)
				componentsList.add(components[i]);
		return componentsList;
	}

	public class TargetWrapper {
		private Generic generic;

		private Holder holder;

		public TargetWrapper(Generic generic, Holder holder) {
			this.generic = generic;
			this.holder = holder;
		}

		public boolean isBaseComponent() {
			return holder.getBaseComponent().equals(generic);
		}

		public Generic getGeneric() {
			return generic;
		}

		public void setGeneric(Generic generic) {
			this.generic = generic;
		}

		public Holder getHolder() {
			return holder;
		}

		public void setHolder(Holder holder) {
			this.holder = holder;
		}
	}

	public void addValue(Attribute attribute, String newValue) {
		Generic currentInstance = getSelectedTreeNodeGeneric();
		currentInstance.setValue(cache, attribute, newValue);
		messages.info("addValue", newValue, attribute, currentInstance);
	}

	public void removeHolder(Holder holder) {
		if (holder.getBaseComponent().equals(selectedTreeNode.getGeneric())) {
			holder.remove(cache);
			messages.info("remove", holder);
		} else {
			selectedTreeNode.getGeneric().cancel(cache, holder, true);
			messages.info("cancel", holder);
		}
	}

	public String delete() {
		Generic generic = getSelectedTreeNodeGeneric();
		if (isValue(generic)) {
			selectedTreeNode = selectedTreeNode.getParent();
			removeHolder((Holder) generic);
		} else {
			generic.remove(cache);
			messages.info("deleteFile", generic.getValue());
			selectedTreeNode = selectedTreeNode.getParent();
		}
		return "";
	}

	public void changeType(@Observes/* @TreeSelection */TreeSelectionEvent treeSelectionEvent) {
		if (treeSelectionEvent.getId().equals("typestree")) {
			selectedTreeNode = (GenericTreeNode) treeSelectionEvent.getObject();
			panelTitleChangeEvent.fire(new PanelTitleChangeEvent("typesmanager", ((GenericImpl) getSelectedTreeNodeGeneric()).toCategoryString()));
			buildValuesMenuGroup();
			messages.info("typeselectionchanged", getSelectedTreeNodeGeneric().toString());
		}
	}

	private UIMenuGroup buildValuesMenuGroup() {
		valuesMenuGroup.getChildren().clear();
		FacesContext facesContext = FacesContext.getCurrentInstance();
		int i = 0;
		for (GenericTreeNode genericTreeNode : selectedTreeNode.getChildrens(cache, TreeType.ATTRIBUTES, implicitShow)) {
			UIMenuItem uiMenuItem = (UIMenuItem) facesContext.getApplication().createComponent(UIMenuItem.COMPONENT_TYPE);
			uiMenuItem.setLabel("show values of " + genericTreeNode.getGeneric());
			MethodExpression methodExpression = facesContext.getApplication().getExpressionFactory().createMethodExpression(facesContext.getELContext(), "#{typesBean.changeAttributeSelected(" + i + ")}", void.class, new Class<?>[] { Integer.class });
			uiMenuItem.setActionExpression(methodExpression);
			uiMenuItem.setRender("typestree, typestreetitle");
			valuesMenuGroup.getChildren().add(uiMenuItem);
			i++;
		}
		return valuesMenuGroup;
	}

	public void changeAttributeSelected(int attributeIndex) {
		Attribute attribute = (Attribute) selectedTreeNode.getChildrens(cache, TreeType.ATTRIBUTES, implicitShow).get(attributeIndex).getGeneric();
		selectedTreeNode.setAttribute(attribute);
		selectedTreeNode.setTreeType(TreeType.VALUES);
		messages.info("showvalues", attribute);
	}

	public void changeTreeType(TreeType treeType) {
		selectedTreeNode.setTreeType(treeType);
		messages.info("showchanged", treeType);
	}

	public void processDrop(DropEvent dropEvent) {
		System.out.println("getDragValue " + ((GenericTreeNode) dropEvent.getDragValue()).getGeneric());
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

	// TODO in GS CORE
	public boolean isValue(Generic generic) {
		return generic.isConcrete() && generic.isAttribute();
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
				genericTreeNode.setGeneric(generic.updateKey(cache, newValue));
				messages.info("updateShortPath", newValue, generic.getValue());
			}
		}
	}

	public GenericWrapper getGenericWrapper(Generic generic) {
		return new GenericWrapper(generic);
	}

	public class GenericWrapper {
		private Generic wrappedGeneric;

		public GenericWrapper(Generic wrappedGeneric) {
			this.wrappedGeneric = wrappedGeneric;
		}

		public String getValue() {
			return wrappedGeneric.toString();
		}

		public void setValue(String newValue) {
			if (!newValue.equals(wrappedGeneric.toString())) {
				wrappedGeneric.updateKey(cache, newValue);
				messages.info("updateValue", wrappedGeneric, newValue);
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
		return genericTreeNode.isImplicitAutomatic(genericTreeNode.getGeneric()) || (isValue(genericTreeNode.getGeneric()) && !((Holder) genericTreeNode.getGeneric()).getBaseComponent().equals(selectedTreeNode.getGeneric())) ? "implicitColor" : "";
	}

	public boolean isBaseComponent(Holder holder) {
		return holder.getBaseComponent().equals(selectedTreeNode.getGeneric());
	}

	public boolean isImplicitShow() {
		return implicitShow;
	}

	public void setImplicitShow(boolean implicitShow) {
		this.implicitShow = implicitShow;
	}

	public UIMenuGroup getValuesMenuGroup() {
		return valuesMenuGroup;
	}

	public void setValuesMenuGroup(UIMenuGroup valuesMenuGroup) {}

}
