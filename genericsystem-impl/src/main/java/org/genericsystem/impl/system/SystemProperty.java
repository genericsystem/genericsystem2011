package org.genericsystem.impl.system;

import java.io.Serializable;

import org.genericsystem.api.core.Generic;

public interface SystemProperty {

	<T extends Serializable> T getDefaultValue(Generic generic);

}
