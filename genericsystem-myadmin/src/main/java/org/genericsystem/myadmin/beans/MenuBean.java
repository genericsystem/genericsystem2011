package org.genericsystem.myadmin.beans;

import java.io.Serializable;

import javax.el.MethodExpression;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.myadmin.beans.GenericTreeNode.TreeType;
import org.genericsystem.myadmin.util.GsMessages;
import org.richfaces.component.UIMenuGroup;
import org.richfaces.component.UIMenuItem;

@RequestScoped
@Named
public class MenuBean implements Serializable {

	private static final long serialVersionUID = 3205251309315588635L;

	private UIMenuGroup menuGroup;

	@Inject
	private GsMessages messages;

	@Inject
	private GenericTreeBean genericTreeBean;

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

	public boolean isConcrete() {
		return genericTreeBean.getSelectedTreeNodeGeneric().isConcrete();
	}

	public void changeType(@Observes MenuEvent menuEvent) {
		menuGroup.getChildren().clear();
		FacesContext facesContext = FacesContext.getCurrentInstance();
		int i = 0;
		for (GenericTreeNode genericTreeNode : menuEvent.getGenericTreeNode().getChildrens(TreeType.ATTRIBUTES, menuEvent.isImplicitShow())) {
			UIMenuItem uiMenuItem = (UIMenuItem) facesContext.getApplication().createComponent(UIMenuItem.COMPONENT_TYPE);
			uiMenuItem.setLabel("show values of " + genericTreeNode.getGeneric());
			MethodExpression methodExpression = facesContext.getApplication().getExpressionFactory().createMethodExpression(facesContext.getELContext(), "#{genericTreeBean.changeAttributeSelected(" + i + ")}", void.class, new Class<?>[] { Integer.class });
			uiMenuItem.setActionExpression(methodExpression);
			uiMenuItem.setRender("typestree, typestreetitle");
			menuGroup.getChildren().add(uiMenuItem);
			i++;
		}
	}

	public static class MenuEvent {
		private final GenericTreeNode genericTreeNode;
		private final boolean implicitShow;

		public MenuEvent(GenericTreeNode genericTreeNode, boolean implicitShow) {
			this.genericTreeNode = genericTreeNode;
			this.implicitShow = implicitShow;
		}

		public GenericTreeNode getGenericTreeNode() {
			return genericTreeNode;
		}

		public boolean isImplicitShow() {
			return implicitShow;
		}

	}
}
