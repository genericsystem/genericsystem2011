package org.genericsystem.myadmin.beans;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;
import org.genericsystem.myadmin.util.GsMessages;

@Named
@SessionScoped
public class CacheBean implements Serializable {

	private static final long serialVersionUID = 1752488068961882618L;

	@Inject
	private GsMessages messages;

	@Inject
	private transient Cache cache;

	public void save() {
		cache.flush();
	}

	public String discard() {
		cache.clear();
		return "HOME";
	}

	public String newCache() {
		Cache superCache = cache.newSuperCache();
		messages.info("addCache");
		return "HOME";
	}
}
