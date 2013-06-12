package org.genericsystem.myadmin.beans;

import java.io.Serializable;

import javax.el.MethodExpression;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.genericsystem.core.Cache;
import org.genericsystem.myadmin.beans.GenericTreeNode.TreeType;
import org.richfaces.component.UIMenuGroup;
import org.richfaces.component.UIMenuItem;

@RequestScoped
@Named
public class MenuBean implements Serializable {

	private static final long serialVersionUID = 3205251309315588635L;

	private UIMenuGroup menuGroup;

	public void changeType(@Observes MenuEvent menuEvent) {
		menuGroup.getChildren().clear();
		FacesContext facesContext = FacesContext.getCurrentInstance();
		int i = 0;
		for (GenericTreeNode genericTreeNode : menuEvent.getGenericTreeNode().getChildrens(menuEvent.getCache(), TreeType.ATTRIBUTES, menuEvent.isImplicitShow())) {
			UIMenuItem uiMenuItem = (UIMenuItem) facesContext.getApplication().createComponent(UIMenuItem.COMPONENT_TYPE);
			uiMenuItem.setLabel("show values of " + genericTreeNode.getGeneric());
			MethodExpression methodExpression = facesContext.getApplication().getExpressionFactory().createMethodExpression(facesContext.getELContext(), "#{typesBean.changeAttributeSelected(" + i + ")}", void.class, new Class<?>[] { Integer.class });
			uiMenuItem.setActionExpression(methodExpression);
			uiMenuItem.setRender("typestree, typestreetitle");
			menuGroup.getChildren().add(uiMenuItem);
			i++;
		}
	}

	public static class MenuEvent {
		private final Cache cache;
		private final GenericTreeNode genericTreeNode;
		private final boolean implicitShow;

		public MenuEvent(Cache cache, GenericTreeNode genericTreeNode, boolean implicitShow) {
			this.cache = cache;
			this.genericTreeNode = genericTreeNode;
			this.implicitShow = implicitShow;
		}

		public Cache getCache() {
			return cache;
		}

		public GenericTreeNode getGenericTreeNode() {
			return genericTreeNode;
		}

		public boolean isImplicitShow() {
			return implicitShow;
		}

	}

	public UIMenuGroup getMenuGroup() {
		return menuGroup;
	}

	public void setMenuGroup(UIMenuGroup menuGroup) {
		this.menuGroup = menuGroup;
	}
}
