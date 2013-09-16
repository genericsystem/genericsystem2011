package org.genericsystem.myadmin.beans;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@SessionScoped
public class SessionBean implements Serializable {

	private static final long serialVersionUID = -4027293737043933978L;

	@Inject
	private ExternalContext externalContext;

	public String invalidate() {
		externalContext.invalidateSession();
		return "HOME";
	}

}