package org.genericsystem.impl.system;

import org.genericsystem.api.core.Generic;

public abstract class AbstractSystemProperty implements SystemProperty {

	@Override
	public boolean defaultIsActive(Generic generic) {
		return false;
	}
}
