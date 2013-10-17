package org.genericsystem.myadmin.beans;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Generic;
import org.genericsystem.myadmin.beans.GenericBean.GenericEdited;
import org.genericsystem.myadmin.beans.GenericBean.ModifyViewEvent;
import org.genericsystem.myadmin.beans.GenericBean.View;
import org.genericsystem.myadmin.util.GsMessages;
import org.richfaces.component.UIMenuGroup;

@RequestScoped
@Named
public class MenuBean implements Serializable {

	private static final long serialVersionUID = 3205251309315588635L;

	private UIMenuGroup menuGroup;
	//
	@Inject
	private GsMessages messages;
	//
	// @Inject
	// private TreeBean genericTreeBean;
	//
	@Inject
	private Event<ModifyViewEvent> modifyViewEvent;

	@GenericEdited
	@Inject
	public Generic genericEdited;

	private View currentView;

	public UIMenuGroup getMenuGroup() {
		return menuGroup;
	}

	public void setMenuGroup(UIMenuGroup menuGroup) {
		this.menuGroup = menuGroup;
	}

	public String getMenuTypeIcon(String genericType) {
		switch (genericType) {
		case "TYPE":
			return messages.getInfos("bullet_square_yellow");
		case "ATTRIBUTE":
			return messages.getInfos("bullet_triangle_yellow");
		case "INSTANCE":
			return messages.getInfos("bullet_square_green");
		default:
			break;
		}
		throw new IllegalStateException();
	}

	public void changeView(View view) {
		modifyViewEvent.fire(new ModifyViewEvent(view));
		currentView = view;
		messages.info("showchanged", view);
	}

	//
	public boolean isViewSelected(View view) {
		return view.equals(currentView);
	}
	//
	// public void changeType(@Observes MenuEvent menuEvent) {
	// menuGroup.getChildren().clear();
	// FacesContext facesContext = FacesContext.getCurrentInstance();
	// int i = 0;
	// for (TreeNode genericTreeNode : menuEvent.getGenericTreeNode().getChildrens(TreeType.ATTRIBUTES, menuEvent.isImplicitShow())) {
	// UIMenuItem uiMenuItem = (UIMenuItem) facesContext.getApplication().createComponent(UIMenuItem.COMPONENT_TYPE);
	// uiMenuItem.setLabel("show values of " + genericTreeNode.getGeneric());
	// MethodExpression methodExpression = facesContext.getApplication().getExpressionFactory().createMethodExpression(facesContext.getELContext(), "#{genericTreeBean.changeAttributeSelected(" + i + ")}", void.class, new Class<?>[] { Integer.class });
	// uiMenuItem.setActionExpression(methodExpression);
	// uiMenuItem.setRender("typestree, typestreetitle");
	// menuGroup.getChildren().add(uiMenuItem);
	// i++;
	// }
	// }
	//
	// public static class MenuEvent {
	// private final TreeNode genericTreeNode;
	// private final boolean implicitShow;
	//
	// public MenuEvent(TreeNode genericTreeNode, boolean implicitShow) {
	// this.genericTreeNode = genericTreeNode;
	// this.implicitShow = implicitShow;
	// }
	//
	// public TreeNode getGenericTreeNode() {
	// return genericTreeNode;
	// }
	//
	// public boolean isImplicitShow() {
	// return implicitShow;
	// }
	//
	// }
}
