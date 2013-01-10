package org.genericsystem.impl.system;

import java.io.Serializable;

import org.genericsystem.api.core.Generic;

public abstract class AbstractSystemProperty implements SystemProperty {

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Serializable> T getDefaultValue(Generic generic) {
		return (T) Boolean.FALSE;
	}
}
