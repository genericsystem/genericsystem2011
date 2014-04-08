package org.genericsystem.jsf.util;

import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;

public class FacesContextProvider {

	@Produces
	FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

}
