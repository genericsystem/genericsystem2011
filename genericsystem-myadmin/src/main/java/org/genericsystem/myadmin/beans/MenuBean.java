package org.genericsystem.myadmin.beans;

import java.io.Serializable;

import javax.el.MethodExpression;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.myadmin.gui.GuiTreeNode;
import org.genericsystem.myadmin.gui.GuiTreeNode.GuiTreeChildrenType;
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
		return genericTreeBean.getSelectedTreeNode().getGeneric().isConcrete();
	}

	public void changeType(@Observes MenuEvent menuEvent) {
		menuGroup.getChildren().clear();
		FacesContext facesContext = FacesContext.getCurrentInstance();
		int i = 0;
		for (GuiTreeNode genericTreeNode : menuEvent.getGenericTreeNode().getChildren(GuiTreeChildrenType.ATTRIBUTES)) {
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
		private final GuiTreeNode genericTreeNode;

		public MenuEvent(GuiTreeNode genericTreeNode) {
			this.genericTreeNode = genericTreeNode;
		}

		public GuiTreeNode getGenericTreeNode() {
			return genericTreeNode;
		}
	}
}
