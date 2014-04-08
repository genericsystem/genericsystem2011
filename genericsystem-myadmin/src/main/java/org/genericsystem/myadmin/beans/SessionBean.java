package org.genericsystem.myadmin.beans;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Bean for management of Session via GUI of MyAdmin.
 * 
 */
@Named
@SessionScoped
public class SessionBean implements Serializable {

	private static final long serialVersionUID = -4027293737043933978L;

	@Inject
	private FacesContext context;

	/**
	 * Invalidates the current session.
	 * 
	 * @return string "HOME".
	 */
	public String invalidateSession() {
		// TODO: Fix session invalidation
		context.getExternalContext().invalidateSession();
		return "HOME";
	}

}
