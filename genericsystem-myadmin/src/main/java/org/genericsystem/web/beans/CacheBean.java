package org.genericsystem.web.beans;

import java.io.Serializable;

import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@RequestScoped
public class CacheBean implements Serializable {

	private static final long serialVersionUID = 1752488068961882618L;

	protected static Logger log = LoggerFactory.getLogger(CacheBean.class);

	@Inject
	private transient Cache cache;

	public void save() {
		cache.flush();
	}

	public void discard() {
		cache.clear();
	}

}
