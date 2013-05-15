package org.genericsystem.web.beans;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;

@Named
@SessionScoped
public class CacheBean implements Serializable {

	private static final long serialVersionUID = 1752488068961882618L;

	@Inject
	private transient Cache cache;

	public void save() {
		cache.flush();
	}

	public void discard() {
		cache.clear();
	}

}
