package org.genericsystem.api.generic;

import org.genericsystem.api.core.Generic;

/**
 * @author Nicolas Feybesse
 * 
 */
public interface Value extends Generic {

	<T extends Generic> T getBaseComponent();

	<T extends Generic> T getComponent(int componentPos);

}
